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

    // stores object from DBO
    public int insertOffer(OffersDBO object) {
        int id = dbManager.maxId("estates");
        try (Statement stmt = connection.createStatement()) {
            String sqlString = "insert into estates (id, name, description, price, type, transaction) values(" +
                    "" + id + ", '" + object.getName() + "', '" + object.getDescription() + "', " + object.getPrice() + ", '" + object.getType() + "', '" + object.getTransaction() + "')";
            stmt.executeUpdate(sqlString);
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
                    "select * from estates where id = " + id);
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
    public List<Integer> getOffers(String type, String name) {
        List<Integer> idList = new ArrayList<Integer>();
        try (Statement stmt = connection.createStatement()) {
            String sqlString = "";
            if (type.isEmpty() & name.isEmpty()) {
                sqlString = "select estates.id from estates ";
            } else if (!type.isEmpty() & name.isEmpty()) {
                sqlString = "select estates.id from estates " +
                        "where estates.type = '" + type + "' ";
            } else if (type.isEmpty() & !name.isEmpty()) {
                sqlString = "select estates.id from estates " +
                        "where estates.name = '" + name + "' ";
            } else {
                sqlString = "select estates.id from estates " +
                        "where estates.type = '" + type + "' AND estates.name = '" + name + "' ";
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

    // deletes offer from table
    public void deleteOffer(int id) {
        try (Statement stmt = connection.createStatement()) {
            String sqlString = "delete from estates where id = " + id;
            stmt.executeUpdate(sqlString);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
