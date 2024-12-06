package asu.cse360project.DatabaseHelpers;
import asu.cse360project.Singleton;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import asu.cse360project.EncryptionHelpers.EncryptionHelper;

/**
 * Core database management class that handles database connections and initialization.
 * This class serves as the primary interface for database operations, managing:
 * - Database connection establishment and closure
 * - Table creation and initialization
 * - Helper class instantiation for specific database operations
 * - Basic database state queries
 * 
 * The class uses H2 database for data persistence and provides access to specialized
 * helper classes for user, group, article, and message operations.
 *
 * @author Tu35
 * @version 3.00 2024-11-15 Enhanced database connections and added encryption/decryption
 * @version 2.00 2024-11-01 Added database connection and initialization for new features
 * @version 1.00 2024-10-30 Initial version with basic user management
 */
public class DatabaseHelper {
    /** H2 database JDBC driver class name */
    static final String JDBC_DRIVER = "org.h2.Driver";
    
    /** H2 database connection URL */
    static final String DB_URL = "jdbc:h2:~/cse360ProjectDatabase";
    
    /** Default H2 database username */
    static final String USER = "sa";
    
    /** Default H2 database password (empty) */
    static final String PASS = "";

    /** Active database connection */
    private Connection connection = null;
    
    /** SQL statement for executing queries */
    private Statement statement = null;

    /** Helper for user-related database operations */
    private UserHelper user_helper;
    
    /** Helper for group and article related database operations */
    private GroupArticlesHelper groups_articles_helper;
    
    /** Helper for message-related database operations */
    private MessageHelper msg_helper;

    /** Helper for encryption/decryption operations */
    private EncryptionHelper encryptionHelper;

    /**
     * Establishes a connection to the H2 database and initializes helper classes.
     * This method:
     * 1. Loads the JDBC driver
     * 2. Creates a database connection
     * 3. Initializes helper classes
     * 4. Creates necessary database tables
     *
     * @throws SQLException If a database access error occurs
     * @throws Exception If the JDBC driver cannot be loaded
     */
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

    /**
     * Gets the user helper instance for user-related database operations.
     * @return UserHelper instance
     */
    public UserHelper getUser_helper() {
		return user_helper;
	}

    /**
     * Gets the group and articles helper instance for related database operations.
     * @return GroupArticlesHelper instance
     */
    public GroupArticlesHelper getGroupArticlesHelper() {
		return groups_articles_helper;		
	}
	
    /**
     * Gets the message helper instance for message-related database operations.
     * @return MessageHelper instance
     */
    public MessageHelper getMessageHelper() {
		return msg_helper;
	}

    /**
     * Checks if the database has any users.
     * This is typically used to determine if initial setup is required.
     *
     * @return true if no users exist in the database, false otherwise
     * @throws SQLException If a database access error occurs
     */
    public boolean isDatabaseEmpty() throws SQLException {
        String query = "SELECT COUNT(*) AS count FROM cse360users"; // SQL query to count the number of users
        ResultSet resultSet = statement.executeQuery(query); // Execute the query and store the result
        if (resultSet.next()) { // Move to the first row of the result
            return resultSet.getInt("count") == 0; // Return true if there are no users (count == 0)
        }
        return true; // Return true if the result set is empty
    }

    /**
     * Clears all data from the database by dropping all objects.
     * This is a destructive operation that cannot be undone.
     *
     * @throws SQLException If a database access error occurs
     */
    public void clearDatabase() throws SQLException {
        String query = "DROP ALL OBJECTS"; // SQL query to clear database
        statement.executeUpdate(query); // Execute the query and store the result
    }

		
    /**
     * Closes the database connection and associated resources.
     * This method should be called when the database connection is no longer needed
     * to free up system resources.
     */
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

    /**
     * Creates all necessary database tables if they don't exist.
     * This includes tables for:
     * - Users
     * - Articles
     * - Groups
     * - Messages
     * - Relationships between entities
     *
     * @throws SQLException If a database access error occurs
     */
    public void createTables() throws SQLException {
		// SQL command to create the tables if it doesn't exist
		user_helper.createTables();
		groups_articles_helper.createAllTables();
		msg_helper.createTables();
	}

}
