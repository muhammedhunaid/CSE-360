package asu.cse360project.DatabaseHelpers;


import java.sql.*;
import java.util.ArrayList;

import asu.cse360project.Message;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * 
 * <p> MessageHelper. </p>
 * 
 * <p> Description: A class to handle database operations for messages. </p>
 * 
 * <p> Copyright: Tu35 © 2024 </p>
 * 
 * @version 1.00	2024-11-17 Using Singleton class to create and get database connection 
 * 
 */

public class MessageHelper{

    private Connection connection = null; // Connection to the database
    private Statement statement = null; // Statement for executing SQL queries

    public MessageHelper(Connection connection, Statement statement){
        this.connection = connection;
        this.statement = statement;
    }
    
    // Method to create tables in the database
	public void createTables() throws SQLException {
		
		String msgTable = 
				"CREATE TABLE IF NOT EXISTS messages ("
                + "message_id INT AUTO_INCREMENT PRIMARY KEY,"
				+ "user_id INT NOT NULL," // Primary key with auto-incrementing ID
				+ "type VARCHAR(255), " 
				+ "username VARCHAR(255), "
				+ "text TEXT DEFAULT '');"; 
		statement.execute(msgTable); // Execute the SQL command to create the table

		String searchesTable = 
				"CREATE TABLE IF NOT EXISTS searches ("
                + "search_id INT AUTO_INCREMENT PRIMARY KEY,"
				+ "user_id INT NOT NULL, " // Primary key with auto-incrementing ID
				+ "search TEXT NOT NULL);";
		statement.execute(searchesTable); // Execute the SQL command to create the table
	}

	public void addSearch(String search, int user_id) {
		String sql = "INSERT INTO searches (user_id, search) VALUES (?, ?)";

		try (PreparedStatement insertStmt = connection.prepareStatement(sql)) { // Create prepared statement to prevent SQL injection
            // Set values for the prepared statement
            insertStmt.setInt(1, user_id);  // Set username
            insertStmt.setString(2, search);      // Set message text
			
            // Execute the insert and get the number of rows affected
            int rowsInserted = insertStmt.executeUpdate(); 
            if (rowsInserted > 0) {
                System.out.println("Message inserted successfully."); // Print success message
            } else {
                System.out.println("Failed to insert message."); // Print failure message
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Print the stack trace for SQL exceptions
        }
	}

    public boolean newMsg(String text, String user, String type, int user_id) {
        String insertQuery = "INSERT INTO messages (username, text, type, user_id) VALUES (?, ?, ?, ?)"; // SQL query for inserting a new user

        try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) { // Create prepared statement to prevent SQL injection
            // Set values for the prepared statement
            insertStmt.setString(1, user);  // Set username
            insertStmt.setString(2, text);      // Set message text
            insertStmt.setString(3, type);      // Set message text
            insertStmt.setInt(4, user_id);      // Set user_id

            // Execute the insert and get the number of rows affected
            int rowsInserted = insertStmt.executeUpdate(); 
            if (rowsInserted > 0) {
                System.out.println("Message inserted successfully."); // Print success message
                return false;
            } else {
                System.out.println("Failed to insert message."); // Print failure message
                return false;
            }
        } catch (SQLException e) {
            return false;
        }
    }
    
    public ObservableList<Message> getAllMsg(String mtype) throws SQLException {    	
        // List to store all messages
        ObservableList<Message> messages = FXCollections.observableArrayList();
    	
		// SQL query to select all users
		String sql = "SELECT * FROM messages WHERE type = ?;"; 
		PreparedStatement stmt = connection.prepareStatement(sql); // Create a statement object
		stmt.setString(1, mtype);
		ResultSet rs = stmt.executeQuery(); // Execute the query
	
		// Iterate through the result set and print message details
		while (rs.next()) { 
			String username = rs.getString("username"); // Get username
			String text = rs.getString("text"); // Get text of the message
			String type = rs.getString("type"); // Get message type
            int user_id = rs.getInt("user_id"); // Get message type
	
			/* Display user details 
			System.out.print("ID: " + id); 
			System.out.print(", uname: " + username); 
			System.out.print(", type: " + type);
			System.out.println(", text: " + text); 
			*/			
			messages.add(new Message(username, type, text, user_id));
		}

		return messages;		
    }

    public ArrayList<String> getUserSearches(int user_id) throws SQLException {
        ArrayList<String> searches = new ArrayList<>();

        String sql = "SELECT * FROM searches WHERE user_id = ?;"; 
        try(PreparedStatement stmt = connection.prepareStatement(sql);)
        {
            stmt.setInt(1, user_id);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                searches.add(rs.getString("search"));             
            }

        } catch(Exception e) {
            return null;
        }

        return searches;
    }

}
