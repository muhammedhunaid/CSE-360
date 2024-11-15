package asu.cse360project.DatabaseHelpers;


import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import asu.cse360project.Message;
import asu.cse360project.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

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
				+ "id INT AUTO_INCREMENT PRIMARY KEY, " // Primary key with auto-incrementing ID
				+ "type VARCHAR(255), " 
				+ "username VARCHAR(255), "
				+ "text TEXT DEFAULT '')"; 
		statement.execute(msgTable); // Execute the SQL command to create the table
	}

    public void newMsg(String text, String user, String type) {
        String insertQuery = "INSERT INTO messages (username, text, type) VALUES (?, ?, ?)"; // SQL query for inserting a new user

        try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) { // Create prepared statement to prevent SQL injection
            // Set values for the prepared statement
            insertStmt.setString(1, user);  // Set username
            insertStmt.setString(2, text);      // Set message text
            insertStmt.setString(3, type);      // Set message text

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
    
    public ObservableList<String[]> getAllMsg() throws SQLException {
    	
    	String message[] = {"user", "type", "text"};
    	
        // List to store all messages
        ObservableList<String[]> messages = FXCollections.observableArrayList();

    	
		// SQL query to select all users
		String sql = "SELECT * FROM messages"; 
		Statement stmt = connection.createStatement(); // Create a statement object
		ResultSet rs = stmt.executeQuery(sql); // Execute the query
	
		// Iterate through the result set and print message details
		while (rs.next()) { 
			int id = rs.getInt("id"); // Get ID
			String username = rs.getString("username"); // Get username
			String text = rs.getString("text"); // Get text of the message
			String type = rs.getString("type"); // Get message type
	
			// Display user details
			System.out.print("ID: " + id); 
			System.out.print(", uname: " + username); 
			System.out.print(", type: " + type);
			System.out.println(", text: " + text);
			
			message[0] = username;
			message[1] = type;
			message[2] = text;
			
			messages.add(message.clone());
			
		}
		
		for (String[] row : messages) {
            System.out.println("Row data: " + row[0] );
        }
		
		System.out.println("---");
		
		return messages;
		
    }

}
