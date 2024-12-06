package asu.cse360project.DatabaseHelpers;
import asu.cse360project.Singleton;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import asu.cse360project.EncryptionHelpers.EncryptionHelper;

/*******
 * <p> DatabaseHelper. </p>
 * 
 * <p> Description: A controller class for managing users in the JavaFX application. </p>
 * 
 * <p> Copyright: Tu35 2024 </p>
 * 
 * @author Tu35
 * 
 * @version 1.00	2024-10-30 Initial version with basic user management database queries
 * @version 2.00	2024-11-01 Added database connection and initialization for new features
 * @version 3.00	2024-11-15 Enhanced database connections and added encryption/decryption
 * 
 */

public class DatabaseHelper {
    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "org.h2.Driver";   // H2 database JDBC driver
    static final String DB_URL = "jdbc:h2:~/cse360ProjectDatabase";  // H2 database connection URL

    // Database credentials
    static final String USER = "sa"; // Default username for H2
    static final String PASS = ""; // Default password for H2 (empty)

    private Connection connection = null; // Connection to the database
    private Statement statement = null; // Statement for executing SQL queries
    // PreparedStatement pstmt; // Uncomment if needed for additional prepared statements

	private UserHelper user_helper;
	private GroupArticlesHelper groups_articles_helper;
	private MessageHelper msg_helper;

    //Declare the encryptionHelper object whcih will help us encrypt and decrypt objects
    private EncryptionHelper encryptionHelper;

    // Method to establish a connection to the database
    public void connectToDatabase() throws SQLException, Exception {
        try {
            Class.forName(JDBC_DRIVER); // Load the JDBC driver for H2
            System.out.println("Connecting to database..."); // Print connection status
            connection = DriverManager.getConnection(DB_URL, USER, PASS); // Establish the connection
            statement = connection.createStatement(); // Create a statement for executing SQL commands

			user_helper = new UserHelper(connection, statement);
            //initialize the encryptionhelper object which will be used in encryption and decryption
            encryptionHelper = new EncryptionHelper();
			groups_articles_helper = new GroupArticlesHelper(connection, statement, encryptionHelper);
			msg_helper = new MessageHelper(connection, statement);

			createTables();  // Call method to create necessary tables if they don't already exist
            
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC Driver not found: " + e.getMessage()); // Handle the case where the driver isn't found
        }
    }

	public UserHelper getUser_helper() {
		return user_helper;
	}

	public GroupArticlesHelper getGroupArticlesHelper() {
		return groups_articles_helper;		
	}
	
	public MessageHelper getMessageHelper() {
		return msg_helper;
	}

    // Method to check if the database is empty
    public boolean isDatabaseEmpty() throws SQLException {
        String query = "SELECT COUNT(*) AS count FROM cse360users"; // SQL query to count the number of users
        ResultSet resultSet = statement.executeQuery(query); // Execute the query and store the result
        if (resultSet.next()) { // Move to the first row of the result
            return resultSet.getInt("count") == 0; // Return true if there are no users (count == 0)
        }
        return true; // Return true if the result set is empty
    }

	// Method to check if the database is empty
    public void clearDatabase() throws SQLException {
        String query = "DROP ALL OBJECTS"; // SQL query to clear database
        statement.executeUpdate(query); // Execute the query and store the result
    }

		
	public void closeConnection() {
		try { 
			if (statement != null) statement.close(); // Close the statement if it exists
		} catch (SQLException se2) { 
			se2.printStackTrace(); // Print stack trace if closing fails
		} 
		try { 
			if (connection != null) connection.close(); // Close the connection if it exists
		} catch (SQLException se) { 
			se.printStackTrace(); // Print stack trace if closing fails
		} 
	}

	// Method to create tables in the database
	public void createTables() throws SQLException {
		// SQL command to create the tables if it doesn't exist
		user_helper.createTables();
		groups_articles_helper.createAllTables();
		msg_helper.createTables();
	}

}
