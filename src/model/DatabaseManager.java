package model;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
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
    public SimpleBooleanProperty isConnectedStatus;
    private OracleDataSource ods;
    private Connection connection;
    private boolean connected = false;
    public SimpleStringProperty connectionStatusString;

    public DatabaseManager() {
        instance = this;
        connectionStatusString = new SimpleStringProperty("Not connected");
        isConnectedStatus = new SimpleBooleanProperty(false);
    }

    public static DatabaseManager getInstance() { return instance; }

    public Connection getConnection() { return connection; }
    public boolean isConnected() { return connected; }



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
            this.connected = true;
            new MultimediaHandler();
            new OffersHandler();
            new SpatialHandler();
            updateStatusBar("Connected");
            isConnectedStatus.set(true);
        } catch (SQLException sqlException) {
            System.err.println("SQLException: " + sqlException.getMessage());
            updateStatusBar("SQLException: " + sqlException.getMessage());
            isConnectedStatus.set(false);
        }
    }

    // close connection to database
    public void disconnect() {
        try {
            connection.close();
            updateStatusBar("Not connected");
            isConnectedStatus.set(false);
        } catch (SQLException sqlException) {
            System.err.println("SQLException: " + sqlException.getMessage());
            updateStatusBar("SQLException: " + sqlException.getMessage());
        }
        isConnectedStatus.set(false);
        this.connected = false;
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

    // returns next unused id for specified table
    public int getNextId(String table)
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

    public void updateStatusBar(String statusString) {
        connectionStatusString.setValue(statusString);
    }

}
