package connecttodatabase;

/*
* Updating Charity DB assignments
* Authors: Henry Henderson, Andy Thornburg, Alec Nunez
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
            case 2:
                AddCompany();
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

    /**
     * This method requests input from the user to add a new
     * donor to the charity database. It returns the added results
     * on the console when it successfully completes.
     */
    public void AddDonor()
    {
        Scanner keyboard = new Scanner(System.in);
        System.out.println("First name:");
        String firstName = keyboard.nextLine();
        System.out.println("Last name:");
        String lastName = keyboard.nextLine();
        System.out.println("Address:");
        String address = keyboard.nextLine();
        System.out.println("City:");
        String city = keyboard.nextLine();
        System.out.println("State initials:");
        String stateInitials = keyboard.nextLine();
        System.out.println("Zip code:");
        String zipCode = keyboard.nextLine();
        System.out.println();

        Connection conn = null;
        Statement stmt = null;
        PreparedStatement pstmt = null;
        int maxDonorID = 0;

        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            conn.setAutoCommit(false);

            stmt = conn.createStatement();
            String sql;
            sql = "LOCK TABLES donors WRITE";
            stmt.executeQuery(sql);
            sql = "SELECT MAX(donorID) as maxDonorID FROM donors";
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next())
                maxDonorID = rs.getInt("maxDonorID");

            String sqlInsert = "INSERT INTO donors (donorID,lastName,firstName,address,city,state,zip) VALUES (?,?,?,?,?,?,?)";
            pstmt = conn.prepareStatement(sqlInsert,Statement.RETURN_GENERATED_KEYS);

            pstmt.setInt(1, ++maxDonorID);
            pstmt.setString(2, lastName);
            pstmt.setString(3, firstName);
            pstmt.setString(4, address);
            pstmt.setString(5, city);
            pstmt.setString(6, stateInitials);
            pstmt.setString(7, zipCode);
            pstmt.executeUpdate();

            System.out.println();
            System.out.println("New donor added successfully:");
            System.out.println(firstName + " " + lastName);
            System.out.println(address);
            System.out.println(city + ", " + stateInitials + " " + zipCode);
            System.out.println();

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
    public void AddCompany()
    {
        Scanner keyboard = new Scanner(System.in);
        System.out.println("Company Name = ");
        String companyName = keyboard.nextLine();
        System.out.println("Address = ");
        String address = keyboard.nextLine();
        System.out.println("City = ");
        String city = keyboard.nextLine();
        System.out.println("State = ");
        String State = keyboard.nextLine();
        System.out.println("Zip = ");
        String zip = keyboard.nextLine();
        System.out.println("Max Percent = ");
        String maxPercent = keyboard.nextLine();
        System.out.println("Minimum Match = ");
        String minMatch = keyboard.nextLine();
        System.out.println("Maximum Match = ");
        String maxMatch = keyboard.nextLine();
        
        Connection conn = null;
        Statement stmt = null;
        PreparedStatement pstmt = null;
        int companyID = 0;
        
//        For add Company, prompt user for name, address, city, state, zip, 
//                matchPercent(must be between 1% and 200%), minMatch(must be over 0), 
//                maxMatch(max be at least 1)  If Company name is already on table do not add.  
//                Assign an unused companyId
        
    }

    /**
     * This method completes the third programming assignment by querying the user
     * for the donor's first and last name, company name and donation amount then
     * invokes checkExists to determine the required information to write a new entry
     * to the donations table. Author: Alec Nunez.
     */
    public void AddDonation() {
        Scanner keyboard = new Scanner(System.in);
        int donorID = 0;
        int companyID = 0;
        double amount = 0;
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
                        donorID = fNameIdList[k];
                    } else if(fNameIdList[k] == -1){    // This step terminates the loop after seeing a -1 in fNameIdList
                        k = fNameIdList.length - 1;
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
                companyID = companyIdList[0];
            }
        }

        // Loop repeats a console query and conditional for a donation amount
        // checking until donation meets the criteria of being > 0.
        while (i == 0) {
            System.out.print("Please enter in the amount of the donation: ");
            amount = keyboard.nextDouble();
            if(amount <= 0){
                System.err.println("The donation amount cannot be negative. Please try again...");
            } else {
                i = 1;
            }
        }

        Connection conn = null;
        Statement stmt = null;
        Savepoint saveData = null;

        try {
            //STEP 2: Register JDBC driver
            Class.forName("com.mysql.jdbc.Driver");

            //STEP 3: Open a connection
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            conn.setAutoCommit(false);
            saveData = conn.setSavepoint();

            //STEP 4: Execute a query
            stmt = conn.createStatement();
            String sql;
            sql = "LOCK TABLES donations WRITE";
            stmt.executeQuery(sql);
            sql = "SELECT MAX(donationNumber) AS max FROM donations";
            ResultSet rs = stmt.executeQuery(sql);

            //STEP 5: Extract data from result set
            rs.next();
            int donationNumber = rs.getInt("max");
            donationNumber++;

            //STEP 6: Modify Table
            sql = "INSERT INTO donations "
                    + "VALUES(" + donationNumber + ", "
                                + donorID + ", "
                                + companyID + ", "
                                + amount + ")";
            stmt.execute(sql);
            System.out.println("The following entry was entered into the donations table:");
            System.out.println("(" + donationNumber + ", "
                                + donorID + ", "
                                + companyID + ", "
                                + amount + ")");

            //STEP 7: Clean-up environment
            sql = "UNLOCK TABLES";
            stmt.executeQuery(sql);
            conn.commit();
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
                    if(!conn.isClosed()) {
                        conn.rollback(saveData);
                    }
                    conn.close();
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }//end finally try
        }//end try
    }

    /**
     * This helper method is for checking to see whether or not a particular MYSQL table
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
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            conn.setAutoCommit(false);

            //STEP 4: Execute a query
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

            //STEP 6: Clean-up environment
            conn.commit();
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

        return idList;
    }

}//ConnectToDatabase
