package model;

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
    public void resizeImage(int id, int x, int y) {
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
    }

    // returns ids of similar pictures sorted from most similar
    public List<Integer> getSimilarities(int id) {
        List<Integer> similarIDs = new ArrayList<Integer>();
        try (Statement stmt = connection.createStatement()) {
            String sqlString = "select src." + id + " as source, dst.id as destination, si_scorebyftrlist(new si_featurelist(" +
                    "src.picture_ac, 0.3, src.picture_ch, 0.3, src.picture_pc, 0.1, src.picture_tx, 0.3), " +
                    "dst.picture_si) as similarity from pictures src, pictures dst where src.id <> dst.id and src.id = " + id + " order by similarity asc";
            OracleResultSet rset = (OracleResultSet) stmt.executeQuery(sqlString);
            while (rset.next()) {
                similarIDs.add(rset.getInt("id"));
            }
            rset.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return similarIDs;
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
        assert buffer != null;
        return SwingFXUtils.toFXImage(buffer, null);
    }

    // sets new estateId for picture
    public void setEstateId(int id, int estateId) {
        try (OraclePreparedStatement pstmt = (OraclePreparedStatement) connection.prepareStatement(
                "update estateId from pictures where id = " + id)) {
            pstmt.setInt(1, estateId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // updates stored image
    public void setNewImage(int id, String filename) {
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

    // deletes picture from table
    public void deleteEntry(int id) {
        try (Statement stmt = connection.createStatement()) {
            String sqlString = "delete from pictures where id = " + id;
            stmt.executeUpdate(sqlString);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // stores image from file to database (insert with new id)
    public int storeImage(int estateId, String filename)
    {
        int id = 0;
        try {
            final boolean previousAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            try {
                id = dbManager.maxId("pictures");
                try (Statement stmt = connection.createStatement()) {
                    String sqlString = "insert into pictures (id, estateId, picture) values(" + id + ", " + estateId + ", ordsys.ordimage.init())";
                    stmt.executeUpdate(sqlString);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                setNewImage(id, filename);
                connection.commit();
            } finally {
                connection.setAutoCommit(previousAutoCommit);
            }
        } catch (SQLException e) {
            System.err.println("SQLException: " + e.getMessage());
        }
        return id;
    }

    // "for update" locks image until commit/rollback is issued
    public OrdImage getProxy(int id)
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
