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

    // returns geometry from db
    private JGeometry getGeometry(int id) {
        JGeometry jgeom = null;
        try (Statement stmt = connection.createStatement()) {
            ResultSet rset = stmt.executeQuery(
                    "select shape from map_entities where id = " + id);
            if (rset.next()) {
                Struct obj = (Struct) rset.getObject(1);
                jgeom = JGeometry.loadJS(obj);
            }
            rset.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return jgeom;
    }

    // updates geometry in db
    private void setGeometry(int id, JGeometry jgeom) {
        try (PreparedStatement pstmt = connection.prepareStatement(
                "update map_entities set shape = ? where id = " + id)) {
            Struct obj = JGeometry.storeJS(connection, jgeom);
            pstmt.setObject(1, obj);
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteObject(SpatialDBO object) {
        try (Statement stmt = connection.createStatement()) {
            String sqlString = "delete from map_entities where id = " + object.getId();
            stmt.executeUpdate(sqlString);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateObject(SpatialDBO object) {
        try (Statement stmt = connection.createStatement()) {
            String sqlString = "update map_entities " +
                    "set name = '" + object.getName() + "', description = '" + object.getDescription() + "', type = '" + object.getType() + "' " +
                    "where id = " + object.getId();
            stmt.executeUpdate(sqlString);
            setGeometry(object.getId(), object.getShape());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // inserts new geometry into db
    public int insertObject (SpatialDBO object) {
        int id = dbManager.maxId("map_entities");
        try (Statement stmt = connection.createStatement()) {
            String sqlString = "insert into map_entities (id, name, description, type) values(" +
                    "" + id + ", '" + object.getName() + "', '" + object.getDescription() + "', '" + object.getType() + "')";
            stmt.executeUpdate(sqlString);
            setGeometry(id, object.getShape());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }

    // loads object from database
    public SpatialDBO loadObject(int id) {
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

    // returns list of estate ids
    // takes type of object in relation with estate
    public List<Integer> withObject(String type) {
        List<Integer> idList = new ArrayList<Integer>();
        try (Statement stmt = connection.createStatement()) {
            String sqlString = "select estate.estateId from map_entities estate, map_entities object" +
                    "where (estate.type = 'land' and object.type = '" + type + "' AND " +
                    "(SDO_RELATE(estate.shape, object.shape, 'mask=ANYINTERACT') = 'TRUE')";
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

    // returns list of objects in specified area
    // borders: rectangle x,y x,y
    public List<Integer> withinCanvas(int[] borders) {
        List<Integer> idList = new ArrayList<Integer>();
        try (Statement stmt = connection.createStatement()) {
            String sqlString = "select object.id from map_entities object" +
                    "(SDO_FILTER(object.shape, " +
                    "SDO_GEOMETRY(2003, NULL, NULL," +
                    "(SDO_ELEM_INFO_ARRAY(1, 1003, 3)" +
                    "SDO_ORDINATE_ARRAY(" + borders[0] + ", " + borders[1] + ", " + borders[2] + ", " + borders[3] + "))" +
                    ") = 'TRUE')";
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

    // returns list of estate ids
    public List<Integer> withinDistance(String type, int distance) {
        List<Integer> idList = new ArrayList<Integer>();
        try (Statement stmt = connection.createStatement()) {
            String sqlString = "select estate.estateId from map_entities estate, map_entities object" +
                    "where estate.type = 'land' and object.type = '" + type + "' and " +
                    "(SDO_WITHIN_DISTANCE(estate.shape, object.shape, 'distance=" + distance + "') = 'TRUE')";
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

    // returns area of polygon
    // land / building
    public int ObjectArea(String type, int estateId) {
        int area = 0;
        try (Statement stmt = connection.createStatement()) {
            String sqlString = "select SUM(SDO_GEOM.SDO_AREA(shape, 1)) from map_entities estate" +
                    "where estate.type = '" + type + "' and estate.estateId = '" + estateId + "'";
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

    // returns distance between objects
    public int ObjectArea(int firstObjId, int secondObjId) {
        int distance = 0;
        try (Statement stmt = connection.createStatement()) {
            String sqlString = "select SDO_GEOM.SDO_DISTANCE(first.shape, second.shape, 1) from map_entities first, map_entities second" +
                    "where (first.id = '" + firstObjId + "' and second.id = '" + secondObjId + "' )";
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

    // returns diameter/length of object
    // https://docs.oracle.com/database/121/SPATL/sdo_geom-sdo_length.htm#SPATL1120
    public int ObjectLength(int id) {
        int length = 0;
        try (Statement stmt = connection.createStatement()) {
            String sqlString = "select SDO_GEOM.SDO_LENGTH(object.shape, meta.diminfo) from map_entities object, user_sdo_geom_metadata meta" +
                    "where (object.id = '" + id + "' and meta.column_name = 'SHAPE' )";
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
