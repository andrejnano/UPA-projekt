package main.java.model;

import javafx.embed.swing.SwingFXUtils;
import java.awt.image.BufferedImage;
import javafx.scene.image.Image;
import javax.imageio.ImageIO;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import oracle.ord.im.OrdImage;
import oracle.jdbc.*;

public class MultimediaHandler {

    private static MultimediaHandler instance = null;
    private Connection connection;
    private DatabaseManager dbManager;

    public MultimediaHandler() {
        instance = this;
        this.dbManager = DatabaseManager.getInstance();
        this.connection = dbManager.getConnection();
    }

    public static MultimediaHandler getInstance() { return instance; }

    // resizes current image
    // specified by id, takes new dimensions
    public void resizeImage(int id, int x, int y) {
        try {
            final boolean previousAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            try {
                OrdImage imgProxy = getProxy(id);
                try (OraclePreparedStatement pstmt = (OraclePreparedStatement) connection.prepareStatement(
                        "update pictures set picture = ? where id = " + id)) {
                    imgProxy.process("maxscale=" + x + " " + y + " fileformat=png");
                    pstmt.setORAData(1, imgProxy);
                    pstmt.executeUpdate();
                }
                catch (SQLException e) {
                    e.printStackTrace();
                }
                updateStillImage(id);
            } finally {
                connection.setAutoCommit(previousAutoCommit);
            }
        } catch (SQLException e) {
            System.err.println("SQLException: " + e.getMessage());
        }
    }

    public int editImage(int id, String operation) {
        int newId = prepareNewImage(getEstateId(id));
        try {
            final boolean previousAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            try {
                OrdImage newProxy = getProxy(newId);
                OrdImage oldProxy = getProxy(id);
                oldProxy.processCopy(operation, newProxy);
                OraclePreparedStatement pstmt = (OraclePreparedStatement) connection.prepareStatement(
                        "update pictures set picture = ? where id = " + newId);
                pstmt.setORAData(1, newProxy);
                pstmt.executeUpdate();
                pstmt.close();
                updateStillImage(newId);
                connection.commit();
            } finally {
                connection.setAutoCommit(previousAutoCommit);
            }
        } catch (SQLException e) {
            System.err.println("SQLException: " + e.getMessage());
        }
        return newId;
    }

    // returns list of picture ids corresponding with specified offer id
    public int getEstateId(int id) {
        int estateId = 0;
        try (Statement stmt = connection.createStatement()) {
            String sqlString = "select estateId from pictures where id = " + id;
            OracleResultSet rset = (OracleResultSet) stmt.executeQuery(sqlString);
            if (rset.next()) {
                estateId = rset.getInt("estateId");
            }
            rset.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return estateId;
    }

    public Image getProcessedPhotoFromDatabase(int id, String process) throws SQLException, IOException
    {
        int newId = editImage(id, process);
        return getPicture(newId);
        /*OrdImage imgProxy = this.getProxy(id);
        if(imgProxy == null)
        {
            return null;
        }
        imgProxy.process(process);
        BufferedImage bufferedImg = ImageIO.read(new ByteArrayInputStream(imgProxy.getDataInByteArray()));
        Image image = SwingFXUtils.toFXImage(bufferedImg, null);
        return image;*/
    }


    // returns ids of similar pictures
    // sorted from most similar to image specified by id
    public List<Integer> getSimilarities(int id) {
        int estateId = getEstateId(id);
        List<Integer> similarEstateIDs = new ArrayList<Integer>();
        try (Statement stmt = connection.createStatement()) {
            String sqlString = "SELECT dst.estateId, si_scorebyftrlist(new si_featurelist(" +
                    "src.picture_ac, 0.3, src.picture_ch, 0.3, src.picture_pc, 0.1, src.picture_tx, 0.3), " +
                    "dst.picture_si) as similarity FROM pictures src, pictures dst WHERE src.estateId <> dst.estateId and src.estateId = " + estateId + " ORDER BY similarity ASC";
            OracleResultSet rset = (OracleResultSet) stmt.executeQuery(sqlString);
            while (rset.next()) {
                similarEstateIDs.add(rset.getInt(1));
            }
            rset.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return similarEstateIDs;
    }

    // returns list of picture ids corresponding with specified offer id
    public List<Integer> getImageId(int offerId) {
        List<Integer> id = new ArrayList<Integer>();
        try (Statement stmt = connection.createStatement()) {
            String sqlString = "select id from pictures where estateId = " + offerId;
            OracleResultSet rset = (OracleResultSet) stmt.executeQuery(sqlString);
            while (rset.next()) {
                id.add(rset.getInt("id"));
            }
            rset.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }

    // returns first stored picture for specified offer
    public int getFirstImageId(int offerId) {
        List<Integer> images = getImageId(offerId);
        if (images != null && images.size() > 0)
            return images.get(0);
        return -1;
    }

    // returns picture in Image format
    // https://docs.oracle.com/cd/B12037_01/appdev.101/b10829/mm_imgref001.htm
    public Image getPicture(int id) {
        OrdImage imgProxy = getProxy(id);
        BufferedImage buffer = null;
        try {
            InputStream iStream = imgProxy.getBlobContent().getBinaryStream();
            buffer = ImageIO.read(iStream);
        }
        catch (IOException | SQLException e) {
            e.printStackTrace();
        }
        Image image = null;
        try {
            image = SwingFXUtils.toFXImage(buffer, null);
        } catch (NullPointerException e) {
            System.err.println("SQLException: " + e.getMessage());
        }
        return image;
    }

    // sets new estateId for picture specified by id
    public void setEstateId(int id, int estateId) {
        try (OraclePreparedStatement pstmt = (OraclePreparedStatement) connection.prepareStatement(
                "update estateId from pictures where id = " + id)) {
            pstmt.setInt(1, estateId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // updates stored image in database
    // for updating stored image only
    public void updateImage(int id, String filename) {
        try {
            OrdImage imgProxy = getProxy(id);
            try {
                imgProxy.loadDataFromFile(filename);
                imgProxy.setProperties();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try (OraclePreparedStatement pstmt = (OraclePreparedStatement) connection.prepareStatement(
                    "update pictures set picture = ? where id = " + id)) {
                pstmt.setORAData(1, imgProxy);
                pstmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        updateStillImage(id);
    }

    // deletes picture from database specified by id
    public void deleteEntry(int id) {
        try (Statement stmt = connection.createStatement()) {
            String sqlString = "delete from pictures where id = " + id;
            stmt.executeUpdate(sqlString);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // creates new image entry with init ordiamge
    public int prepareNewImage(int estateId)
    {
        int id = 0;
        id = dbManager.getNextId("pictures");
        try (Statement stmt = connection.createStatement()) {
            String sqlString = "insert into pictures (id, estateId, picture) values(" + id + ", " + estateId + ", ordsys.ordimage.init())";
            stmt.executeUpdate(sqlString);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }

    // creates new record for image and stores image from file
    // returns id of stored image
    // adds all necessary informations for image
    public int storeImage(int estateId, String filename)
    {
        int id = 0;
        try {
            final boolean previousAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            try {
                id = prepareNewImage(estateId);
                updateImage(id, filename);
                connection.commit();
            } finally {
                connection.setAutoCommit(previousAutoCommit);
            }
        } catch (SQLException e) {
            System.err.println("SQLException: " + e.getMessage());
        }
        return id;
    }

    // internal function, loads picture from db into ordImage obejct
    // "for update" locks image until commit/rollback is issued
    private OrdImage getProxy(int id)
    {
        OrdImage imgProxy = null;
        try (Statement stmt = connection.createStatement()) {
            String sqlString = "select picture from pictures where id = " + id + " for update";
            OracleResultSet rset = (OracleResultSet) stmt.executeQuery(sqlString);
            if (rset.next()) {
                imgProxy = (OrdImage) rset.getORAData("picture", OrdImage.getORADataFactory());
            }
            rset.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return imgProxy;
    }

    // internal function
    // updates stillimage data based on current ordimage
    // check if alias ok (multimedia demo)
    private void updateStillImage(int id) {
        try (Statement stmt3 = connection.createStatement()) {
            String sqlString = "update pictures p SET p.picture_si=SI_StillImage(p.picture.getContent()) WHERE p.id = " + id;
            stmt3.executeUpdate(sqlString);
            String sqlString2 = "update pictures SET " +
                    "picture_ac=SI_AverageColor(picture_si), " +
                    "picture_ch=SI_ColorHistogram(picture_si), " +
                    "picture_pc=SI_PositionalColor(picture_si), " +
                    "picture_tx=SI_Texture(picture_si) WHERE id = " + id;
            stmt3.executeUpdate(sqlString2);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
