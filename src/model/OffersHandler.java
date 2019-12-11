package model;

import oracle.jdbc.OracleResultSet;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class OffersHandler {
    private Connection connection;
    private DatabaseManager dbManager;

    public OffersHandler() {
        this.dbManager = DatabaseManager.getInstance();
        this.connection = dbManager.getConnection();
    }

    // stores object from DBO
    int insertOffer(OffersDBO object) {
        int id = dbManager.maxId("offers");
        try {
            try (Statement stmt = connection.createStatement()) {
                String sqlString = "insert into offers (id, name, description, price, type, transaction) values(" +
                        "" + id + ", '" + object.getName() + "', '" + object.getDescription() + "', " + object.getPrice() + ", '" + object.getType() + "', '" + object.getTransaction() + "')";
                stmt.executeUpdate(sqlString);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }

    // loads object from database
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
            }
            rset.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return object;
    }

    // returns list of ids of offers of specified type
    public List<Integer> getOffers(String type) {
        List<Integer> idList = new ArrayList<Integer>();
        try (Statement stmt = connection.createStatement()) {
            String sqlString = "";
            if (type.equals("All")) {
                sqlString = "select offers.id from offers";
            }
            else {
                sqlString = "select offers.id from offers" +
                        "where offers.type = '" + type + "' ";
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
}
