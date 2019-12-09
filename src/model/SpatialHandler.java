package model;

import oracle.jdbc.OraclePreparedStatement;
import oracle.spatial.geometry.JGeometry;

import java.sql.*;
import java.util.ArrayList;
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

}
