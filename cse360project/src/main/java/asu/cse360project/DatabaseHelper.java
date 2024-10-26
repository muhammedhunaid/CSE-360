package asu.cse360project;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Base64;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.PreparedStatement;

import javafx.collections.ObservableList;

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

	//Declare the encryptionHelper object whcih will help us encrypt and decrypt objects
	//having troubles with importing bouncy castle - manas
//	private EncryptionHelper encryptionHelper;

    // Method to establish a connection to the database
    public void connectToDatabase() throws SQLException {
        try {
            Class.forName(JDBC_DRIVER); // Load the JDBC driver for H2
            System.out.println("Connecting to database..."); // Print connection status
            connection = DriverManager.getConnection(DB_URL, USER, PASS); // Establish the connection
            statement = connection.createStatement(); // Create a statement for executing SQL commands
            createTables();  // Call method to create necessary tables if they don't already exist

			//calling the method to create article tables
			createArticleTable();
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC Driver not found: " + e.getMessage()); // Handle the case where the driver isn't found
        }
    }

    // Method to create tables in the database
    private void createTables() throws SQLException {
        // SQL command to create the cse360users table if it doesn't exist
        String userTable = 
                "CREATE TABLE IF NOT EXISTS cse360users ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, " // Primary key with auto-incrementing ID
                + "username VARCHAR(255) UNIQUE, " // Unique username
                + "password VARCHAR(255), " // Password for user authentication
                + "role VARCHAR(30), " // User role (e.g., admin, student)
                + "first VARCHAR(255), " // User's first name
                + "middle VARCHAR(255), " // User's middle name
                + "last VARCHAR(255), " // User's last name
                + "preffered VARCHAR(255), " // User's preferred name
                + "email VARCHAR(255) UNIQUE, " // Unique email address for user
                + "otp_expires DATETIME)"; // Expiration date for one-time passwords (OTP)
        statement.execute(userTable); // Execute the SQL command to create the table
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

    // Method to insert a new user into the database
    public void insertUser(String invite_code, String role) {
        String insertQuery = "INSERT INTO cse360users (username, role) VALUES (?, ?)"; // SQL query for inserting a new user

        try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) { // Create prepared statement to prevent SQL injection
            // Set values for the prepared statement
            insertStmt.setString(1, invite_code);  // Set username
            insertStmt.setString(2, role);      // Set user role

            // Execute the insert and get the number of rows affected
            int rowsInserted = insertStmt.executeUpdate(); 
            if (rowsInserted > 0) {
                System.out.println("User inserted successfully."); // Print success message
            } else {
                System.out.println("Failed to insert user."); // Print failure message
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Print the stack trace for SQL exceptions
        }
    }

    // Method to delete a user from the database
    public void deleteUser(String username) {
        String deleteQuery = "DELETE FROM cse360users WHERE username = ?"; // SQL query for deleting a user

        try (PreparedStatement deleteStmt = connection.prepareStatement(deleteQuery)) { // Create prepared statement
            // Set the username parameter for the DELETE query
            deleteStmt.setString(1, username);

            // Execute the delete and get the number of rows affected
            int rowsDeleted = deleteStmt.executeUpdate(); 
            if (rowsDeleted > 0) {
                System.out.println("User deleted successfully."); // Print success message
            } else {
                System.out.println("No user found with the given username."); // Print message if user not found
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Print the stack trace for SQL exceptions
        }
    }

    // Method to add a new user with username, password, and role
    public void addUser(String username, String password, String role) throws SQLException {
        String insertQuery = "INSERT INTO cse360users (username, role, password) VALUES (?, ?, ?)"; // SQL query for adding a user

        try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) { // Create prepared statement
            // Set values for the prepared statement
            insertStmt.setString(1, username);  // Set username
            insertStmt.setString(2, role);      // Set role
            insertStmt.setString(3, password);  // Set password

            // Execute the insert and get the number of rows affected
            int rowsInserted = insertStmt.executeUpdate(); 
            if (rowsInserted > 0) {
                System.out.println("User inserted successfully."); // Print success message
            } else {
                System.out.println("Failed to insert user."); // Print failure message
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Print the stack trace for SQL exceptions
        }
    }

    // Method to finalize account setup with additional user details
    public void finishAccountSetup(String username, String first_name, String middle_name, String last_name, String pref_name, String email) throws SQLException {
        String updateUserQuery = "UPDATE cse360users SET first = ?, last = ?, middle = ?, email = ?, preffered = ? WHERE username = ?"; // SQL update query
        try (PreparedStatement pstmt = connection.prepareStatement(updateUserQuery)) { // Create prepared statement
            // Set user details in the prepared statement
            pstmt.setString(1, first_name);
            pstmt.setString(2, last_name);
            pstmt.setString(3, middle_name);
            pstmt.setString(4, email);
            pstmt.setString(5, pref_name);
            pstmt.setString(6, username); // Set username for the WHERE clause
            pstmt.executeUpdate(); // Execute the update
        }
    }

    // Method to reset the OTP and password for a user
    public void resetOTPPassword(String username, String password) throws SQLException {
        String updateUserQuery = "UPDATE cse360users SET otp_expires = ?, password = ? WHERE username = ?"; // SQL update query
        try (PreparedStatement pstmt = connection.prepareStatement(updateUserQuery)) { // Create prepared statement
            // Set expiration time for OTP and new password
            pstmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now().plusDays(3))); // Set OTP expiration to 3 days from now
            pstmt.setString(2, password); // Set new password
            pstmt.setString(3, username); // Set username for the WHERE clause
            pstmt.executeUpdate(); // Execute the update
        }
    }

    // Method to change the role of a user
    public void changeRole(String username, String role) throws SQLException {
        String updateUserQuery = "UPDATE cse360users SET role = ? WHERE username = ?"; // SQL update query
        try (PreparedStatement pstmt = connection.prepareStatement(updateUserQuery)) { // Create prepared statement
            pstmt.setString(1, role); // Set new role
            pstmt.setString(2, username); // Set username for the WHERE clause
            pstmt.executeUpdate(); // Execute the update
        }
    }

    // Method for user login validation
    public User login(String username, String password) throws SQLException {
        String query = "SELECT * FROM cse360users WHERE username = ? AND password = ?"; // SQL query to find the user
        try (PreparedStatement pstmt = connection.prepareStatement(query)) { // Create prepared statement
            pstmt.setString(1, username); // Set username parameter
            pstmt.setString(2, password); // Set password parameter
            try (ResultSet rs = pstmt.executeQuery()) { // Execute the query and store results
                if(rs.next()) { // If user exists
                    return getUser(username); // Return user object with user details
                } else {
                    return null; // Return null if login fails
                }
            }
        }
    }

	public User checkInviteCode(String invite_code) throws SQLException {
		// SQL query to check if a user exists with the given invite code (username)
		String query = "SELECT * FROM cse360users WHERE username = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, invite_code); // Set the invite_code as the username in the query
			try (ResultSet rs = pstmt.executeQuery()) {
				// If a user is found and their password is null (indicating they haven't set it up)
				if (rs.next() && rs.getString("password") == null) {
					return getUser(invite_code); // Return the user object
				} else {
					return null; // Return null if user doesn't exist or password is set
				}
			}
		}
	}
	
	public User getUser(String username) throws SQLException {
		// SQL query to retrieve user details based on username
		String query = "SELECT role, first, otp_expires FROM cse360users WHERE username = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username); // Set the username in the query
			try (ResultSet rs = pstmt.executeQuery()) {
				// If user exists, retrieve their details
				if (rs.next()) {
					String role = rs.getString("role"); // Get user's role
					String firstName = rs.getString("first"); // Get user's first name
					Timestamp password_reset = rs.getTimestamp("otp_expires"); // Get OTP expiration time
					String pw_reset_string = "";
					// Convert password_reset to string if it's not null
					if (password_reset != null) {
						pw_reset_string = password_reset.toString();
					}
					// Return a User object with the retrieved details
					return new User(username, firstName, role, pw_reset_string);
				} else {
					System.out.println("User does not exist."); // Log if user is not found
					return null; // Return null if user doesn't exist
				}
			}
		}
	}
	
	public boolean isOtpExpired(String username) throws SQLException {
		// SQL query to get the OTP expiration time for a user
		String query = "SELECT otp_expires FROM cse360users WHERE username = ?";
		
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username); // Set the username in the query
			ResultSet rs = pstmt.executeQuery();
			
			if (rs.next()) {
				// Get the otp_expires timestamp from the database
				Timestamp otpExpires = rs.getTimestamp("otp_expires");
				
				// Get the current date and time
				LocalDateTime currentTime = LocalDateTime.now();
				
				// Compare otp_expires with the current time
				if (otpExpires != null) {
					return otpExpires.toLocalDateTime().isBefore(currentTime); // Return true if OTP is expired
				} else {
					// If otp_expires is null, consider it as expired
					return true;
				}
			}
		}
		
		// If the user is not found, consider the OTP as expired
		return true;
	}
	
	public boolean doesUserExist(String email) {
		// SQL query to count users with the given email
		String query = "SELECT COUNT(*) FROM cse360users WHERE email = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, email); // Set the email in the query
			ResultSet rs = pstmt.executeQuery();
			
			if (rs.next()) {
				// If the count is greater than 0, the user exists
				return rs.getInt(1) > 0;
			}
		} catch (SQLException e) {
			e.printStackTrace(); // Print the stack trace if there's a SQL error
		}
		return false; // If an error occurs, assume user doesn't exist
	}
	
	public void displayUsersByAdmin() throws SQLException {
		// SQL query to select all users
		String sql = "SELECT * FROM cse360users"; 
		Statement stmt = connection.createStatement(); // Create a statement object
		ResultSet rs = stmt.executeQuery(sql); // Execute the query
	
		// Iterate through the result set and print user details
		while (rs.next()) { 
			int id = rs.getInt("id"); // Get user's ID
			String username = rs.getString("username"); // Get username
			String role = rs.getString("role"); // Get user's role
			String pw = rs.getString("password"); // Get user's password (for debugging, should not print in production)
			String otp = rs.getString("otp_expires"); // Get OTP expiration time
	
			// Display user details
			System.out.print("ID: " + id); 
			System.out.print(", uname: " + username); 
			System.out.print(", role: " + role); 
			System.out.println(", pw: " + pw); 
			System.out.println(", otp: " + otp); 
		} 
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
	
	public void register(String invite_code, String username, String password) throws SQLException {
		// SQL query to update a user's username and password based on invite code
		String updateUserQuery = "UPDATE cse360users SET username = ?, password = ? WHERE username = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(updateUserQuery)) {
			pstmt.setString(1, username); // Set new username
			pstmt.setString(2, password); // Set new password
			pstmt.setString(3, invite_code); // Set invite code as the username to match
	
			// Execute the update
			int rowsUpdated = pstmt.executeUpdate();
			if (rowsUpdated > 0) {
				System.out.println("User registered successfully."); // Log success
			} else {
				System.out.println("User not registered successfully. No rows updated."); // Log failure
			}
		} catch (SQLException e) {
			System.out.println("SQL error: " + e.getMessage()); // Log SQL error
			throw e; // Rethrow the exception if necessary
		}
	}
	
	public void resetPassword(String username, String password) throws SQLException {
		// SQL query to update a user's password
		String updateUserQuery = "UPDATE cse360users SET password = ?, otp_expires = ? WHERE username = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(updateUserQuery)) {
			pstmt.setString(1, password); // Set new password
			pstmt.setTimestamp(2, null); // Set otp_expires to null
			pstmt.setString(3, username); // Set username for the update
			pstmt.executeUpdate(); // Execute the update
		} catch (SQLException e) {
			System.out.println("SQL error: " + e.getMessage()); // Log SQL error
			throw e; // Rethrow the exception if necessary
		}
	}

	//Suggestions: don't return the ObservableList because all the changes performed here will be visible in the main method. - Manas
	//the only parameter is the ObservableList containing Users. We do not need to return the ObservableList, because all the changes performed here
	//will be reflected in main method or any other function which has a reference to the all_users obseravableList.
	public ObservableList<User> ListUsers(ObservableList<User> all_Users) throws SQLException {
		// SQL query to select all users
		String sql = "SELECT * FROM cse360users"; 
		Statement stmt = connection.createStatement(); // Create a statement object
		ResultSet rs = stmt.executeQuery(sql); // Execute the query
	
		// Iterate through the result set and add users to the provided list
		while (rs.next()) { 
			String username = rs.getString("username"); // Get username
			all_Users.add(getUser(username)); // Add user object to the list
		} 
	
		return all_Users; // Return the list of users
	}


	//Methods to deal with articles:

	//method which will create a table articles in the database to store article fields.
	private void createArticleTable() throws SQLException {

		//sql command for creating a table in the database
		//the table name is articles and it is created only if it is not present before
		//schema for the table:
		//id: which is int and increments everytime when new record is inserted. it also the primary key in the database
		//title: which is text , it is stored in encrypted form
		//authors: which is text , it is stored in encrypted form
		//abstract: which is text , it is stored in encrypted form
		//keywords: which is text , it is stored in encrypted form
		//references: which is text , it is stored in encrypted form

		String sql = "CREATE TABLE IF NOT EXISTS articles (" +
				"id INT AUTO_INCREMENT PRIMARY KEY, " +
				"title TEXT, " +
				"authors TEXT, " +
				"abstract TEXT, " +
				"keywords TEXT, " +
				"body TEXT, " +
				"references TEXT)";

		//executing the table creation query
		statement.execute(sql);
	}

	//Method to insert a new article into the database with encrypted fields for title, authors, abstract, keywords, body, and references
	public void createArticle(Article newArticle) throws Exception {

		String title = newArticle.getTitle();
		String authors = newArticle.getAuthors();
		String abstractInfo = newArticle.getAbstractText();
		String keywords = newArticle.getKeywords();
		String body = newArticle.getBody();
		String references = newArticle.getReferences();

		//sql command for inserting the article into the table
		String insertArticle = "INSERT INTO articles (title, authors, abstract, keywords, body, references) VALUES (?, ?, ?, ?, ?, ?)";

		//defining the Initialization Vector for the encryptions

//		byte[] IV = EncryptionUtils.getInitializationVector("team35".toCharArray());

		//adding the sql insert statements
		try (PreparedStatement pstmt = connection.prepareStatement(insertArticle)) {

			//encrypt the title, authors, abstract, body, referenes and then push them into the table
			pstmt.setString(1, title);
			pstmt.setString(2, authors);
			pstmt.setString(3, abstractInfo);
			pstmt.setString(4, keywords);
			pstmt.setString(5, body);
			pstmt.setString(6, references);

			//executing sql statement for insertion of the article
			pstmt.executeUpdate();

			//clear the article from the system memory after storing the information in DB
			newArticle.clearArticle();

		} catch (Error e) {
			System.out.println("error: " + e.getMessage()); // Log SQL error
			throw e; // Rethrow the exception if necessary
		}
	}

	//Method to list the articles into the database after decrypting all the article fields for title, authors, abstract, keywords, body, and references
	//the only parameter is the ObservableList containing Articles. We do not need to return the ObservableList, because all the changes performed here
	//will be reflected in main method or any other function which has a reference to the all_articles obseravableList.
	public void listArticles(ObservableList<Article> all_articles) throws SQLException, Exception {

		//sql command for select all the data from the articles table
		String sql = "SELECT * FROM articles";

		//defining the same Initialization Vector for the decryptions
//		byte[] iv = EncryptionUtils.getInitializationVector("team35".toCharArray());

		// sql query to fetch all the articles
		try (Statement stmt = connection.createStatement();

			 //reading and storing the record one by one
			 ResultSet record = stmt.executeQuery(sql)) {

			//loop through each and every record while extracting the title, authors, abstract, keywords, body, references
			while (record.next()) {

				//retrieving and decrypting each field from the result set
				String title = record.getString("title");
//				char[] decryptTitle = EncryptionUtils.toCharArray(encryptionHelper.decrypt(Base64.getDecoder().decode(title), iv));

				String authors = record.getString("authors");
//				char[] decryptAuthors = EncryptionUtils.toCharArray(encryptionHelper.decrypt(Base64.getDecoder().decode(authors), iv));

				String abstractText = record.getString("abstract");
//				char[] decryptAbstract = EncryptionUtils.toCharArray(encryptionHelper.decrypt(Base64.getDecoder().decode(abstractText), iv));

				String keywords = record.getString("keywords");
//				char[] decryptKeywords = EncryptionUtils.toCharArray(encryptionHelper.decrypt(Base64.getDecoder().decode(keywords), iv));

				String body = record.getString("body");
//				char[] decryptBody = EncryptionUtils.toCharArray(encryptionHelper.decrypt(Base64.getDecoder().decode(body), iv));

				String references = record.getString("references");
//				char[] decryptReferences = EncryptionUtils.toCharArray(encryptionHelper.decrypt(Base64.getDecoder().decode(references), iv));


				all_articles.add(new Article(title, authors, abstractText, keywords, body, references));


				//only for debugging purposes
				//feel free to remove them if you think the code is working properly
				// The article ID can be displayed to the user
				System.out.println("\nArticle ID: " + record.getInt("id"));

				//remove these print statements in actual production
				System.out.println("Title Encrypted: " + title);
//				System.out.print("Title Decrypted: ");
//				EncryptionUtils.printCharArray(decryptTitle);

				System.out.println("\n\nAuthors Encrypted: " + authors);
//				System.out.print("Author Decrypted: ");
//				EncryptionUtils.printCharArray(decryptAuthors);

				System.out.println("\n\nAbstract Encrypted: " + abstractText);
//				System.out.print("Abstract Decrypted: ");
//				EncryptionUtils.printCharArray(decryptAbstract);

				System.out.println("\n\nKeywords Encrypted: " + keywords);
//				System.out.print("Keywords Decrypted: ");
//				EncryptionUtils.printCharArray(decryptKeywords);

				System.out.println("\n\nBody Encrypted: " + body);
//				System.out.print("Body Decrypted: ");
//				EncryptionUtils.printCharArray(decryptBody);

				System.out.println("\n\nReferences Encrypted: " + references);
//				System.out.print("References Decrypted: ");
//				EncryptionUtils.printCharArray(decryptReferences);

				System.out.println("\n--------------------------------------------\n");

			}
		}
	}

	public void updateArticle(int articleId, String title, String authors, String abstractInfo, String keywords, String body, String references) throws SQLException {

		// Start building the SQL update query with only non-null values
		StringBuilder updateArticle = new StringBuilder("UPDATE articles SET ");
		boolean first = true; // Boolean flag to track if we're adding the first field

		// Check if 'title' is non-null, add it to the SQL update statement
		if (title != null) {
			updateArticle.append("title = ?"); // Add 'title' to the SET clause
			first = false; // Set 'first' to false, indicating first field has been added
		}

		// Check if 'authors' is non-null, add it to the SQL update statement
		if (authors != null) {
			// If not the first field, add a comma for separation
			if (!first) {
				updateArticle.append(", ");
			}
			updateArticle.append("authors = ?"); // Add 'authors' to the SET clause
			first = false; // Set 'first' to false
		}

		// Check if 'abstractInfo' is non-null, add it to the SQL update statement
		if (abstractInfo != null) {
			// If not the first field, add a comma for separation
			if (!first) {
				updateArticle.append(", ");
			}
			updateArticle.append("abstract = ?"); // Add 'abstract' to the SET clause
			first = false; // Set 'first' to false
		}

		// Check if 'keywords' is non-null, add it to the SQL update statement
		if (keywords != null) {
			// If not the first field, add a comma for separation
			if (!first) {
				updateArticle.append(", ");
			}
			updateArticle.append("keywords = ?"); // Add 'keywords' to the SET clause
			first = false; // Set 'first' to false
		}

		// Check if 'body' is non-null, add it to the SQL update statement
		if (body != null) {
			// If not the first field, add a comma for separation
			if (!first) {
				updateArticle.append(", ");
			}
			updateArticle.append("body = ?"); // Add 'body' to the SET clause
			first = false; // Set 'first' to false
		}

		// Check if 'references' is non-null, add it to the SQL update statement
		if (references != null) {
			// If not the first field, add a comma for separation
			if (!first) {
				updateArticle.append(", ");
			}
			updateArticle.append("references = ?"); // Add 'references' to the SET clause
		}

		// Finalize the query by adding the WHERE clause to specify the article by id
		updateArticle.append(" WHERE id = ?");

		// Prepare the SQL statement for execution with the dynamically created query
		try (PreparedStatement pstmt = connection.prepareStatement(updateArticle.toString())) {

			// Set only the non-null parameters in the prepared statement, in the correct order
			int index = 1; // Initialize index for parameter positions

			// Set title if non-null, increment index
			if (title != null) {
				pstmt.setString(index++, title);
			}

			// Set authors if non-null, increment index
			if (authors != null) {
				pstmt.setString(index++, authors);
			}

			// Set abstract if non-null, increment index
			if (abstractInfo != null) {
				pstmt.setString(index++, abstractInfo);
			}

			// Set keywords if non-null, increment index
			if (keywords != null) {
				pstmt.setString(index++, keywords);
			}

			// Set body if non-null, increment index
			if (body != null) {
				pstmt.setString(index++, body);
			}

			// Set references if non-null, increment index
			if (references != null) {
				pstmt.setString(index++, references);
			}

			// Set the article ID for the WHERE clause at the final position
			pstmt.setInt(index, articleId);

			// Execute the update statement in the database and get the number of rows affected
			int rowsUpdated = pstmt.executeUpdate();

			// Check if the update affected any rows, indicating a successful update
			if (rowsUpdated > 0) {
				System.out.println("Number of articles updated : " + rowsUpdated);
			} else {
				System.out.println("No article found with the given ID.");
			}

		} catch (SQLException e) {
			System.out.println("Error updating article: " + e.getMessage()); // Print error if SQL exception occurs
			throw e;
		}
	}


	//Method to delete the articles from the database with the help of their ID
	public void deleteArticle(int id) throws SQLException {

		//sql command for delete the article from the articles table after finding the id
		String deleteSQL = "DELETE FROM articles WHERE id = ?";

		//sql query to delete the particular article
		try (PreparedStatement pstmt = connection.prepareStatement(deleteSQL)) {

			//the ID of the article to delete
			pstmt.setInt(1, id);

			//executing the delete operation
			pstmt.executeUpdate();


		}
	}

	//Method to backup the articles into a secondary storage location where all the article fields for title, authors, abstract, keywords, body, and references are encrypted
	public void backupArticles(String backupFilePath) {
		try (Statement stmt = connection.createStatement();

			 //Executing the query to retrieve all rows from the table
			 ResultSet record = stmt.executeQuery("SELECT * FROM articles");

			 //intializing bufferedwriter to write the data to the user given backup file
			 BufferedWriter writer = new BufferedWriter(new FileWriter(backupFilePath))) {

			//loop through each and every record while extracting the title, authors, abstract, keywords, body, references
			while (record.next()) {

				// Writing each encrypted field to the file
				writer.write(record.getString("title") + "\n");
				writer.write(record.getString("authors") + "\n");
				writer.write(record.getString("abstract") + "\n");
				writer.write(record.getString("keywords") + "\n");
				writer.write(record.getString("body") + "\n");
				writer.write(record.getString("references") + "\n");

				//using a delimiter character for identifying each and every article record
				writer.write("---\n");
			}

			//tell the user that backup is done successfully
			System.out.println("Backup completed successfully to " + backupFilePath);
		}

		//catch block to catch any errors
		catch (Exception e) {

			//show the error to the user
			System.err.println("Error during backup: " + e.getMessage());
		}
	}

	//Method to restore the articles into the database where all the article fields for title, authors, abstract, keywords, body, and references are encrypted
	public void restoreArticles(String backupFilePath) {
		try (Statement stmt = connection.createStatement()) {

			//sql query to delete all current articles from the table
			stmt.executeUpdate("DELETE FROM articles");

			//try block to catch any errors
			try (BufferedReader reader = new BufferedReader(new FileReader(backupFilePath))) {

				//declare line to store the information on each line
				String line;

				//loop through each and every record while extracting the title, authors, abstract, keywords, body, references
				while ((line = reader.readLine()) != null) {

					//intialize varibales to store the title, authors, abstract, keywords, body, references
					String title = line;
					String authors = reader.readLine();
					String abstractText = reader.readLine();
					String keywords = reader.readLine();
					String body = reader.readLine();
					String references = reader.readLine();

					//I am using this line to skip the delimiter character which I used earlier
					reader.readLine();

					//Inserting the read data into the database again

					//sql query to insert the data read from backup file
					String insertArticle = "INSERT INTO articles (title, authors, abstract, keywords, body, references) VALUES (?, ?, ?, ?, ?, ?)";

					//adding the sql insert statements
					try (PreparedStatement pstmt = connection.prepareStatement(insertArticle)) {

						//push the title, authors, abstract, body, referenes into the table
						pstmt.setString(1, title);
						pstmt.setString(2, authors);
						pstmt.setString(3, abstractText);
						pstmt.setString(4, keywords);
						pstmt.setString(5, body);
						pstmt.setString(6, references);

						//executing sql statement for insertion of the article
						pstmt.executeUpdate();
					}
				}

				//tell the user that the database has been restored
				System.out.println("Restore completed successfully from " + backupFilePath);
			}
		}

		//catch block to catch any errors
		catch (Exception e) {

			//tell the user about the error
			System.err.println("Error during restore: " + e.getMessage());
		}
	}
}	
