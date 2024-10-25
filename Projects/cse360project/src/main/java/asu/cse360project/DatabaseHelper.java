package asu.cse360project;

import java.sql.*;
import java.time.LocalDateTime;

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

    // Method to establish a connection to the database
    public void connectToDatabase() throws SQLException {
        try {
            Class.forName(JDBC_DRIVER); // Load the JDBC driver for H2
            System.out.println("Connecting to database..."); // Print connection status
            connection = DriverManager.getConnection(DB_URL, USER, PASS); // Establish the connection
            statement = connection.createStatement(); // Create a statement for executing SQL commands
            createTables();  // Call method to create necessary tables if they don't already exist
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
	
	public Group getGroup(String group_name) throws SQLException{
		String query = "SELECT name, article_list FROM article_groups WHERE name = ?";
		
		long[] curr_list = {4678L, 8367L, 156L, 823L, 2539L};
//		
//		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
//			pstmt.setString(1, group_name); // Set the group's name in the query
//			try (ResultSet rs = pstmt.executeQuery()) {
//				// If user exists, retrieve their details
//				if (rs.next()) {
//					Array list = rs.getArray("article_list");
//					
//				}
//			}
//		}
		
		
		
		return new Group(group_name, curr_list);
		
//		return null;
	}
	
    // Method to insert a new user into the database
    public void insertGroup(String group_name, long[] raw_list) throws SQLException {
    	Long[] class_list = new Long[raw_list.length];
    	int i = 0;
    	
    	for(long article : raw_list) {
    		class_list[i++] = article;
    	}
    	
    	System.out.println(class_list.toString());
    	
    	Array list = connection.createArrayOf("long", class_list);
    	
        String insertQuery = "INSERT INTO article_groups (group_name, article_list) VALUES (?, ?)"; // SQL query for inserting a new user

        try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) { // Create prepared statement to prevent SQL injection
            // Set values for the prepared statement
            insertStmt.setString(1, group_name);  // Set name
            insertStmt.setArray(2, list);

            // Execute the insert and get the number of rows affected
            int rowsInserted = insertStmt.executeUpdate(); 
            if (rowsInserted > 0) {
                System.out.println("Group inserted successfully."); // Print success message
            } else {
                System.out.println("Failed to insert group."); // Print failure message
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Print the stack trace for SQL exceptions
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
	
	public ObservableList<Group> ListGroups(ObservableList<Group> all_Groups) throws SQLException {
		// SQL query to select all users
//		String sql = "SELECT * FROM article_groups"; 
//		Statement stmt = connection.createStatement(); // Create a statement object
//		ResultSet rs = stmt.executeQuery(sql); // Execute the query
//	
//		// Iterate through the result set and add groups to the provided list
//		while (rs.next()) { 
//			String groupname = rs.getString("group_name"); // Get username
//			all_Groups.add(getGroup(groupname)); // Add user object to the list
//		} 
		
		for (int i = 0; i < 10; i++) {
			String name = "Group ";
			name += i;
			all_Groups.add(getGroup(name));
		}
		
		return all_Groups; // Return the list of users
	}
}	
