package connecttodatabase;

/**
 *
 * @author Marietta E. Cameron
 * Henry Henderson's copy
 * Reports on question 1.
 */
import java.sql.*;
import java.util.Scanner;

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
    public static void main(String[] args) 
    {
        ConnectToDatabase instance = new ConnectToDatabase();
        instance.PresentMenu();
    }
    
    public void PresentMenu()
    {
        System.out.println("Welcome to the donor database editor. Please type an option number below, followed by the Enter key.");
        System.out.println(" 1 - ADD DONOR");
        System.out.println(" 2 - ADD COMPANY");
        System.out.println(" 3 - ADD DONATION");
        System.out.println(" 4 - QUIT");
        System.out.println();
        System.out.println("Command: ");

        Scanner keyboard = new Scanner(System.in);
        int response = keyboard.nextInt();
        
        switch (response)
        {
            case 1: // Add donor
                AddDonor();
                break;
            case 3:
                AddDonation();
                break;
            case 4: // Quit
                System.out.println("Bye!");
            default:
                System.out.println("Unrecognized option.");
                PresentMenu();
                break;
        }
    }

    public void AddDonor()
    {
        Scanner keyboard = new Scanner(System.in);
        System.out.println("First name:");
        String firstName = keyboard.next();
        System.out.println("Last name:");
        String lastName = keyboard.next();
        System.out.println("Address:");
        String address = keyboard.next();
        System.out.println("City:");
        String city = keyboard.next();
        System.out.println("State initials:");
        String stateInitials = keyboard.next();
        System.out.println("Zip code:");
        String zipCode = keyboard.next();

        Connection conn = null;
        Statement stmt = null;
        PreparedStatement pstmt = null;
        int maxDonorID = 0;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            stmt = conn.createStatement();
            String sql;
            sql = "SELECT MAX(donorID) as maxDonorID FROM donors";
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                maxDonorID = rs.getInt("maxDonorID");
            }
            
            sql = "LOCK TABLES donors WRITE";
            stmt.executeUpdate(sql);

            String sqlInsert = "INSERT INTO donors (donorID,lastName,firstName,address,city,state,zip) VALUES (?,?,?,?,?,?)";
            pstmt = conn.prepareStatement(sqlInsert,Statement.RETURN_GENERATED_KEYS);

            pstmt.setInt(++maxDonorID, 1);
            pstmt.setString(2, lastName);
            pstmt.setString(3, firstName);
            pstmt.setString(4, address);
            pstmt.setString(5, city);
            pstmt.setString(6, stateInitials);
            pstmt.setString(7, zipCode);
            pstmt.executeUpdate();

            sql = "UNLOCK TABLES";
            stmt.executeUpdate(sql);

            rs.close();
            stmt.close();
            pstmt.close();
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
        PresentMenu();
    }
    
    public void AddDonation() {
        Scanner keyboard = new Scanner(System.in);
        
        System.out.print("Please enter in the donor's first name: ");
        String firstName = keyboard.next();
        System.out.print("Please enter in the donor's last name: ");
        String lastName = keyboard.next();
        System.out.print("Please enter in the matching company's name: ");
        String company = keyboard.next();
        
        Connection conn = null;
        Statement stmt = null;
        try {
            //STEP 2: Register JDBC driver
            Class.forName("com.mysql.jdbc.Driver");

            //STEP 3: Open a connection
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            conn.setAutoCommit(false);
            
            
            //STEP 4: Execute a query
            System.out.println("Creating statement...");
            stmt = conn.createStatement();
            String sql;
            sql = "LOCK TABLES donors WRITE";
            stmt.executeQuery(sql);
            sql = "SELECT * FROM donors";
            ResultSet rs = stmt.executeQuery(sql);
            sql = "UNLOCK TABLES";
            stmt.executeQuery(sql);
            
            conn.commit();
            //STEP 6: Clean-up environment
            //rs.close();
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
    }

}//ConnectToDatabase
