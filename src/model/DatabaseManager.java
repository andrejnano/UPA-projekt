package model;

import oracle.jdbc.OracleResultSet;
import oracle.jdbc.pool.OracleDataSource;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.io.IOException;

// this should be a singleton service, handling all oracle database operations, connection setup, etc.
public class DatabaseManager {

    private static DatabaseManager instance = null;
    private OracleDataSource ods;
    private Connection connection;

    public DatabaseManager() {
        instance = this;
    }

    public static DatabaseManager getInstance() { return instance; }

    public Connection getConnection() { return connection; }

    // Configure OracleDataSource to a Database Server¶
    public void setup(String host, String port, String serviceName, String user, String password) throws SQLException {
        // construct full oracle db URL
        // e.g. : "jdbc:oracle:thin:@//gort.fit.vutbr.cz:1521/orclpdb"
        String url = "jdbc:oracle:thin:@//" + host + ":" + port + "/" + serviceName;
        ods = new OracleDataSource();
        ods.setURL(url);
        ods.setUser(user);
        ods.setPassword(password);

        System.out.println("Database source url: " + url);
        System.out.println("Username: " + user);
        System.out.print("Password: ");
        for(int i=0; i<password.length(); i++) {
            System.out.print("*");
        }
        System.out.println(" ");
    }

    // start new connection to database
    public void connect() {
        try {
            connection = ods.getConnection();
            System.out.println("Connected to DB.");
        } catch (SQLException sqlException) {
            System.err.println("SQLException: " + sqlException.getMessage());
        }
        new MultimediaHandler();
        new OffersHandler();
    }

    // close connection to database
    public void disconnect() {
        try {
            connection.close();
        } catch (SQLException sqlException) {
            System.err.println("SQLException: " + sqlException.getMessage());
        }
    }

    // input: file in sql, stores file into sql db
    public void loadDbFromFile(String filename) {
        List<String> toQuery = new ArrayList<String>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            StringBuilder fileData = new StringBuilder();
            String lineData = "";
            while ((lineData = reader.readLine()) != null) {
                String[] parts = lineData.split("--" , 2);
                fileData.append(parts[0]);
            }
            toQuery = Arrays.asList(fileData.toString().split(";"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (Statement stmt = connection.createStatement()) {
            for (String query : toQuery) {
                try {
                    stmt.executeUpdate(query);
                } catch(SQLException e) {
                    System.err.println("ERROR: " + e.getMessage());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // returns highest id in table
    public int maxId(String table)
    {
        int maxId = 0;
        try (Statement stmt = connection.createStatement()) {
            String sqlString = "select max(id) max_id from " + table;
            OracleResultSet rset = (OracleResultSet) stmt.executeQuery(sqlString);
            if (rset.next()) {
                maxId = (int) rset.getInt("max_id");
            }
            rset.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return maxId+1;
    }

    // CanvasShape
    // AppShape
    // JGeometry

}
