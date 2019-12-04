package model;

import oracle.jdbc.pool.OracleDataSource;
import java.sql.Connection;
import java.sql.SQLException;

// this should be a singleton service, handling all oracle database operations, connection setup, etc.
public class DatabaseManager {
    private OracleDataSource ods;
    private Connection connection;

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

    public Connection getConnection() {
        return connection;
    }

    // TODO: initialize database with root level init.sql script
    // This will probably require SQL parser
}
