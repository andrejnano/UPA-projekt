package model;

import oracle.jdbc.OraclePreparedStatement;
import oracle.jdbc.OracleResultSet;
import oracle.spatial.geometry.JGeometry;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SpatialHandler {

    private static SpatialHandler instance = null;
    private Connection connection;
    private DatabaseManager dbManager;

    public SpatialHandler() {
        instance = this;
        this.dbManager = DatabaseManager.getInstance();
        this.connection = dbManager.getConnection();
    }

    public static SpatialHandler getInstance() { return instance; }

    // loads geometry from db, internal method
    private JGeometry getGeometry(int id) {
        JGeometry jgeom = null;
        try (Statement stmt = connection.createStatement()) {
            ResultSet rset = stmt.executeQuery(
                    "select shape from map_entities where id = " + id);
            if (rset.next()) {
                Struct obj = (Struct) rset.getObject(1);
                if (obj != null)
                    jgeom = JGeometry.loadJS(obj);
            }
            rset.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return jgeom;
    }

    // updates geometry in db, internal method
    public void setGeometry(int id, JGeometry jgeom) {
        try (PreparedStatement pstmt = connection.prepareStatement(
                "update map_entities set shape = ? where id = " + id)) {
            Struct obj = JGeometry.storeJS(connection, jgeom);
            pstmt.setObject(1, obj);
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // deletes object specified by SpatialDBO object from database
    public void deleteObject(SpatialDBO object) {
        try (Statement stmt = connection.createStatement()) {
            String sqlString = "delete from map_entities where id = " + object.getId();
            stmt.executeUpdate(sqlString);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // deletes object specified by entity id from database
    public void deleteObject(int id) {
        try (Statement stmt = connection.createStatement()) {
            String sqlString = "delete from map_entities where id = " + id;
            stmt.executeUpdate(sqlString);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // updates object based on informations in SpatialDBO object
    public void updateObject(SpatialDBO object) {
        JGeometry temp = getGeometry(object.getId());
        try (Statement stmt = connection.createStatement()) {
            String sqlString = "update map_entities " +
                    "set name = '" + object.getName() + "', description = '" + object.getDescription() + "', type = '" + object.getType() + "' " +
                    "where id = " + object.getId();
            stmt.executeUpdate(sqlString);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        setGeometry(object.getId(), object.getShape());
        if (!checkShapeState(object.getId())) {
            if (object.getSpatialType().equals("POLYGON")) {
                setGeometry(object.getId(), object.getReverseShape());
            }
            if (!checkShapeState(object.getId())) {
                setGeometry(object.getId(), temp);
            }
        }
    }

    // inserts new geometry from SpatialDBO object into db
    public int insertObject (SpatialDBO object) {
        int id = dbManager.getNextId("map_entities");
        try (Statement stmt = connection.createStatement()) {
            String sqlString = "insert into map_entities (id, name, description, type) values(" +
                    "" + id + ", '" + object.getName() + "', '" + object.getDescription() + "', '" + object.getType() + "')";
            stmt.executeUpdate(sqlString);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        setGeometry(id, object.getShape());
        if (!checkShapeState(id)) {
            if (object.getSpatialType().equals("POLYGON")) {
                setGeometry(id, object.getReverseShape());
            }
            if (!checkShapeState(id)) {
                deleteObject(id);
                return -1;
            }
        }
        return id;
    }

    public boolean checkShapeState(int id) {
        String success = "";
        try (Statement stmt = connection.createStatement()) {
            ResultSet rset = stmt.executeQuery(
                    "select SDO_GEOM.VALIDATE_GEOMETRY_WITH_CONTEXT(shape, 5) valid " +
                            "from map_entities where id = " + id);
            if (rset.next()) {
                success = rset.getString(1);
            }
            rset.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (success.equals("TRUE"))
            return true;
        return false;
    }

    // loads object from database into SpatialDBO object
    public SpatialDBO loadObject(int id) {
        System.out.println("at least got here...");
        SpatialDBO object = new SpatialDBO();
        try (Statement stmt = connection.createStatement()) {
            ResultSet rset = stmt.executeQuery(
                    "select * from map_entities where id = " + id);
            if (rset.next()) {
                object.setId(id);
                object.setName(rset.getString("name"));
                object.setDescription(rset.getString("description"));
                object.setType(rset.getString("type"));
                object.setShape(getGeometry(id));
            }
            rset.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return object;
    }

    // returns list of offer ids containing / touching
    // specified type of object (lake/tree/...)
    public List<Integer> selectWithObject(String type) {
        List<Integer> spatialIdList = new ArrayList<Integer>();
        List<Integer> idList = new ArrayList<Integer>();
        // gets spatialIds
        try (Statement stmt = connection.createStatement()) {
            String sqlString = "select land_entity.id from map_entities land_entity, map_entities object " +
                    "where (land_entity.type = 'land' and object.type = '" + type + "' AND " +
                    "(SDO_RELATE(land_entity.shape, object.shape, 'mask=ANYINTERACT')) = 'TRUE')";
            OracleResultSet rset = (OracleResultSet) stmt.executeQuery(sqlString);
            while (rset.next()) {
                spatialIdList.add(rset.getInt(1));
            }
            rset.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // converts to offerIds
        OffersHandler OHandler = OffersHandler.getInstance();
        for (int spatialId: spatialIdList) {
            idList.add(OHandler.getOfferByObject(spatialId));
        }
        return idList;
    }

    public List<Integer> selectAllObjects() {
        List<Integer> idList = new ArrayList<Integer>();
        try (Statement stmt = connection.createStatement()) {
            String sqlString = "select object.id from map_entities object";
            OracleResultSet rset = (OracleResultSet) stmt.executeQuery(sqlString);
            while (rset.next()) {
                idList.add(rset.getInt(1));
            }
            rset.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return idList;
    }

    // returns list of object ids within specified area uses SDO_FILTER
    // requires loadObject for each id
    // borders: rectangle x,y x,y
    public List<Integer> selectWithinCanvas(int[] borders) {
        List<Integer> idList = new ArrayList<Integer>();
        try (Statement stmt = connection.createStatement()) {
            String sqlString = "select object.id from map_entities object where " +
                    "SDO_FILTER(object.shape, " +
                    "SDO_GEOMETRY(2003, NULL, NULL, " +
                    "SDO_ELEM_INFO_ARRAY(1, 1003, 3), " +
                    "SDO_ORDINATE_ARRAY(" + borders[0] + ", " + borders[1] + ", " + borders[2] + ", " + borders[3] + "))" +
                    ") = 'TRUE'";
            OracleResultSet rset = (OracleResultSet) stmt.executeQuery(sqlString);
            while (rset.next()) {
                idList.add(rset.getInt(1));
            }
            rset.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return idList;
    }

    // returns list of offers
    // specified by type (street/lake/...)
    // within distance from offers land area
    public List<Integer> selectWithinDistance(String type, int distance) {
        List<Integer> spatialIdList = new ArrayList<Integer>();
        List<Integer> idList = new ArrayList<Integer>();
        // gets spatialIds
        try (Statement stmt = connection.createStatement()) {
            String sqlString = "select land_entity.id from map_entities land_entity, map_entities object " +
                    "where land_entity.type = 'land' and object.type = '" + type + "' AND " +
                    "(SDO_WITHIN_DISTANCE(land_entity.shape, object.shape, 'distance=" + distance + "') = 'TRUE')";
            OracleResultSet rset = (OracleResultSet) stmt.executeQuery(sqlString);
            while (rset.next()) {
                spatialIdList.add(rset.getInt(1));
            }
            rset.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // converts to offerIds
        OffersHandler OHandler = OffersHandler.getInstance();
        for (int spatialId: spatialIdList) {
            idList.add(OHandler.getOfferByObject(spatialId));
        }
        return idList;
    }

    // returns area of object polygon
    // requires type
    public int selectObjectArea(String type, int id) {
        int area = 0;
        try (Statement stmt = connection.createStatement()) {
            String sqlString = "select SUM(SDO_GEOM.SDO_AREA(shape, 1)) from map_entities object " +
                    "where object.type = '" + type + "' and object.id = " + id + " ";
            OracleResultSet rset = (OracleResultSet) stmt.executeQuery(sqlString);
            if (rset.next()) {
                area = rset.getInt(1);
            }
            rset.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return area;
    }

    // returns distance between two spatial objects specified by id
    public int selectObjectDistance(int firstObjId, int secondObjId) {
        int distance = 0;
        try (Statement stmt = connection.createStatement()) {
            String sqlString = "select SDO_GEOM.SDO_DISTANCE(first.shape, second.shape, 1) from map_entities first, map_entities second " +
                    "where (first.id = " + firstObjId + " and second.id = " + secondObjId + " )";
            OracleResultSet rset = (OracleResultSet) stmt.executeQuery(sqlString);
            if (rset.next()) {
                distance = rset.getInt(1);
            }
            rset.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return distance;
    }

    // returns diameter/length of object specified by id
    // https://docs.oracle.com/database/121/SPATL/sdo_geom-sdo_length.htm#SPATL1120
    public int selectObjectLength(int id) {
        int length = 0;
        try (Statement stmt = connection.createStatement()) {
            String sqlString = "select SDO_GEOM.SDO_LENGTH(object.shape, meta.diminfo) from map_entities object, user_sdo_geom_metadata meta " +
                    "where (object.id = " + id + " and meta.column_name = 'SHAPE' )";
            OracleResultSet rset = (OracleResultSet) stmt.executeQuery(sqlString);
            if (rset.next()) {
                length = rset.getInt(1);
            }
            rset.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return length;
    }

}
