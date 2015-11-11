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
        System.out.print("Command: ");

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
    
    /**
     * 
     */
    public void AddDonation() {
        Scanner keyboard = new Scanner(System.in);
        int donorId = 0;
        int i = 0;
        int j = 0;
        
        // Loop used to repeat console query for donor's name (3 parts)
        while(i == 0) {
            int[] fNameIdList = new int[25];
            int[] lNameIdList = new int[25];
            // PART 1) Loop asks user for donor's first name and checks donor table for
            // the existance of a donor with such a name.
            while (j == 0) {
                System.out.print("Please enter in the donor's first name: ");
                String firstName = keyboard.nextLine();
                fNameIdList = checkExists("donors", "firstName", firstName);
                if(fNameIdList[0] == -1){
                    System.err.println("The first name " + firstName +
                        " does not exist in the donors table. Please try again...");
                } else {
                    j = 1;
                }
            }
            // PART 2) Loop asks user for donor's last name and checks donor table for
            // the existance of a donor with such a name.
            while (j == 1){
                System.out.print("Please enter in the donor's last name: ");
                String lastName = keyboard.nextLine();
                lNameIdList = checkExists("donors", "lastName", lastName);
                if(lNameIdList[0] == -1){
                    System.err.println("The last name " + lastName +
                        " does not exist in the donors table. Please try again...");
                } else {
                    j = 0;
                }
            }
            // PART 3) Loop checks the id's recieved from part 1 and 2 to verify
            // whether or not the first and last name match together on the same
            // tuple.
            for(int k = 0; k < fNameIdList.length; k++){
                for(int l = 0; l < lNameIdList.length; l++){
                    if((fNameIdList[k] == lNameIdList[l]) && fNameIdList[k] != -1) {
                        i = 1;
                        donorId = fNameIdList[k];
                    }
                }
            }
            if (i == 0) {
                System.err.println("The inputted combination of first and "
                    + "last name does not exist in the donors table. Please try again...");
            }
        }
        
        // Loop repeats a console query and table lookup for a matching company
        // name until lookup succeeds.
        while (i == 1) {
            System.out.print("Please enter in the matching company's name: ");
            String company = keyboard.nextLine();
            int[] companyIdList = checkExists("matchingCompanies", "name", company);
            if(companyIdList[0] == -1){
                System.err.println("The company name " + company +
                    " does not exist in the matchingCompanies table. Please try again...");
            } else {
                i = 0;
            }
        }
        
        // Loop repeats a console query and conditional for a donation amount 
        // checking until donation meets the criteria of being > 0.
        while (i == 0) {
            System.out.print("Please enter in the amount of the donation: ");
            int amount = keyboard.nextInt();
            if(amount <= 0){
                System.err.println("The donation amount cannot be negative. Please try again...");
            } else {
                i = 1;
            }
        }
        
        
    }
    
    /**
     * This method is for checking to see whether or not a particular MYSQL table
     * contains a tuple that matches the following criteria. Author: Alec Nunez.
     * @param table This is the name of the MYSQL table that is being searched.
     * @param item This is the name of the MYSQL field that is being searched for.
     * @param value This is the string that the fields are being compared to.
     * @return Returns an array of int, contains all id values that meet desired
     * criteria; default value set to be -1.
     */
    private int[] checkExists(String table, String item, String value) {
        int[] idList = new int[25];
        for(int i = 0; i < idList.length; i++){
            idList[i] = -1;
        }
        int index = 0;
        
        Connection conn = null;
        Statement stmt = null;
        try {
            //STEP 2: Register JDBC driver
            Class.forName("com.mysql.jdbc.Driver");

            //STEP 3: Open a connection
            //System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            conn.setAutoCommit(false);
            
            //STEP 4: Execute a query
            //System.out.println("Creating statement...");
            stmt = conn.createStatement();
            String sql;
            sql = "SELECT * FROM " + table +
                    " where " + item + " = '" + value + "'";
            ResultSet rs = stmt.executeQuery(sql);
            
            //STEP 5: Extract data from result set
            while (rs.next()) {
                //Retrieve by column name
                idList[index] = rs.getInt(1);
                index++;
            }
            
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
        
        return idList;
    }

}//ConnectToDatabase
