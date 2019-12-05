package model;

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
            String fileData = "";
            String lineData = "";
            while ((lineData = reader.readLine()) != null) {
                String[] parts = lineData.split("--" , 2);
                fileData += parts[0];
            }
            toQuery = Arrays.asList(fileData.split(";"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (Statement stmt = connection.createStatement()) {
            for (String query : toQuery) {
                try {
                    stmt.executeUpdate(query);
                } catch(SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // CanvasShape
    // AppShape
    // JGeometry

}
