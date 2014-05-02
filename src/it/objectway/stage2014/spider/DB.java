package it.objectway.stage2014.spider;

import java.sql.*;

/**
 * Created by stageusr on 30/04/2014.
 */
public class DB {
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";

    Connection conn = null;
    Statement stmt = null;
    ResultSet rs = null;
    String table_name = "counter_items";

    public DB(final String DB_HOST, final String DB_NAME, final String USER, final String PASS){
        final String DB_URL = "jdbc:mysql://" + DB_HOST + "/" + DB_NAME;
        try {
            // STEP 2: Register JDBC driver
            Class.forName(JDBC_DRIVER);

            // STEP 3: Open a connection
            System.out.println("Connecting to database..." + DB_URL);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            // STEP 4: Execute a query
            System.out.println("Creating statement...");
            stmt = conn.createStatement();

            System.out.printf("CREATE TABLE IF NOT EXISTS " + table_name + "(" +
                    "\tid INT NOT NULL AUTO_INCREMENT PRIMARY KEY,\n" +
                    "\twebsite VARCHAR(255) UNIQUE,\n" +
                    "\tcounterText INT,\n" +
                    "\tcounterImage INT,\n" +
                    ");");
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS " + table_name + "(" +
                    "\tid INT NOT NULL AUTO_INCREMENT PRIMARY KEY,\n" +
                    "\twebsite VARCHAR(255) UNIQUE,\n" +
                    "\tcounterText INT,\n" +
                    "\tcounterImage INT\n" +
                    ");");

        } catch (SQLException se) {
            // Handle errors for JDBC
            se.printStackTrace();
        } catch (Exception e) {
            // Handle errors for Class.forName
            e.printStackTrace();
        }
    }

    public void insertOrUpadate(String website, int counterText, int counterImage){
        try {
            stmt.executeUpdate("REPLACE INTO " + table_name + " (website, counterText, counterImage) VALUES ('"+website+"', "+counterText+", "+counterImage+")");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected void finalize() throws Throwable {
        super.finalize();
        // finally block used to close resources
        try {
            // STEP 6: Clean-up environment
            if (rs != null)
                rs.close();
            if (stmt != null)
                stmt.close();
        } catch (SQLException se2) {
        }// nothing we can do
        try {
            if (conn != null)
                conn.close();
        } catch (SQLException se) {
            se.printStackTrace();
        }// end finally try
        System.out.println("Goodbye!");
    }
}