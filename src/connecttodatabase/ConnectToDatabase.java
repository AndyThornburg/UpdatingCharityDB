package connecttodatabase;

/**
 *
 * @author Marietta E. Cameron
 * Henry Henderson's copy
 * Reports on question 1.
 */
import java.sql.*;

public class ConnectToDatabase {

    // JDBC driver name and database URL

    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://montreat.cs.unca.edu:3306/charitydb";

    //  Database credentials
    static final String USER = "CSCI343";
    static final String PASS = "DBMS9154";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Connection conn = null;
        Statement stmt = null;
        try {
            //STEP 2: Register JDBC driver
            Class.forName("com.mysql.jdbc.Driver");

            //STEP 3: Open a connection
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            //STEP 4: Execute a query
            System.out.println("Creating statement...");
            stmt = conn.createStatement();
            String sql;
            sql = "SELECT SUM(amount) AS donations, lastName, firstName FROM donations\n" +
                    "	LEFT JOIN donors USING(donorID)\n" +
                    "	GROUP BY donorID\n" +
                    "	ORDER BY donations, lastName, firstName;";
            ResultSet rs = stmt.executeQuery(sql);

            //STEP 5: Extract data from result set
            while (rs.next()) {
                //Retrieve by column name
                int  donations = rs.getInt("donations");
                
                String last = rs.getString("lastName");
                String first = rs.getString("firstName");
                

                //Display values
                System.out.printf("Total donations: %d\n", donations);
                System.out.printf("Name: %s, %s\n", last, first);
                System.out.println();
            }
            //STEP 6: Clean-up environment
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
        } catch (Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
        } finally {
            //finally block used to close resources
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException se2) {
            }// nothing we can do
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }//end finally try
        }//end try
      
    }//main

}//ConnectToDatabase
