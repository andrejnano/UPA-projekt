package model;

import oracle.jdbc.OracleResultSet;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class OffersHandler {

    private static OffersHandler instance = null;
    private Connection connection;
    private DatabaseManager dbManager;

    public OffersHandler() {
        instance = this;
        this.dbManager = DatabaseManager.getInstance();
        this.connection = dbManager.getConnection();
    }

    public static OffersHandler getInstance() { return instance; }

    // stores OffersDBO object consisting of offer informations into db
    public int insertOffer(OffersDBO object) {
        int id = dbManager.getNextId("estates");
        try (Statement stmt = connection.createStatement()) {
            String sqlString = "insert into estates (id, name, description, price, type, transaction, spatialId) values(" +
                    "" + id + ", '" + object.getName() + "', '" + object.getDescription() + "', " + object.getPrice() + ", '" + object.getType() + "', '" + object.getTransaction() + "', " + object.getSpatialId() + ")";
            stmt.executeUpdate(sqlString);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }

    // loads informations about offer into OffersDBO from database
    public OffersDBO loadOffer(int id) {
        OffersDBO object = new OffersDBO();
        try (Statement stmt = connection.createStatement()) {
            ResultSet rset = stmt.executeQuery(
                    "select * from offers where id = " + id);
            if (rset.next()) {
                object.setId(id);
                object.setName(rset.getString("name"));
                object.setDescription(rset.getString("description"));
                object.setPrice(rset.getInt("price"));
                object.setType(rset.getString("type"));
                object.setTransaction(rset.getString("transaction"));
                object.setSpatialId(rset.getInt("spatialId"));
            }
            rset.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return object;
    }

    // returns list of ids of offers of specified type
    //
    // select by type   - getOffers("type", "")
    // select by name   - getOffers("", "name")
    // combine select   - getOffers("type", "name")
    // select all       - getOffers("", "")
    public List<Integer> getOffers(String type, String name) {
        List<Integer> idList = new ArrayList<Integer>();
        try (Statement stmt = connection.createStatement()) {
            String sqlString = "";
            if (type.isEmpty() & name.isEmpty()) {
                sqlString = "select offers.id from offers ";
            } else if (!type.isEmpty() & name.isEmpty()) {
                sqlString = "select offers.id from offers " +
                        "where offers.type = '" + type + "' ";
            } else if (type.isEmpty() & !name.isEmpty()) {
                sqlString = "select offers.id from offers " +
                        "where offers.name = '" + name + "' ";
            } else {
                sqlString = "select offers.id from offers " +
                        "where offers.type = '" + type + "' AND offers.name = '" + name + "' ";
            }
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

    // returns id of offer corresponding with spatial object
    public int getOfferByObject(int spatialId) {
        int id = 0;
        try (Statement stmt = connection.createStatement()) {
            String sqlString = "select offers.id from offers " +
            "where offers.spatialId = '" + spatialId + "' ";
            OracleResultSet rset = (OracleResultSet) stmt.executeQuery(sqlString);
            while (rset.next()) {
                id = rset.getInt(1);
            }
            rset.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }

    // deletes offer from table
    public void deleteOffer(int id) {
        try (Statement stmt = connection.createStatement()) {
            String sqlString = "delete from offers where id = " + id;
            stmt.executeUpdate(sqlString);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<OffersDBO> getAllOffers() {
        ArrayList<OffersDBO> offers = new ArrayList<>();
        try (Statement stmt = connection.createStatement()) {
            ResultSet rSet = stmt.executeQuery(
                    "select * from estates");

            while (rSet.next()) {
                OffersDBO object = new OffersDBO();
                object.setId(rSet.getInt("id"));
                object.setName(rSet.getString("name"));
                object.setDescription(rSet.getString("description"));
                object.setPrice(rSet.getInt("price"));
                object.setType(rSet.getString("type"));
                object.setTransaction(rSet.getString("transaction"));
                offers.add(object);
            }
            rSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return offers;
    }
}
