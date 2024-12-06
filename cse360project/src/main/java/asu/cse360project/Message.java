package asu.cse360project;


/*******
 * <p> Message. </p>
 * 
 * <p> Description: A class to represent a message in the application. </p>
 * 
 * <p> Copyright: Tu35 © 2024 </p>
 * 
 * @version 1.00	2024-11-17 Added data fields and methods to the class
 * 
 */

public class Message {
    
    // The username of the sender of the message
    private String username;
    
    // The type of the message (e.g., text message, notification, etc.)
    private String type;
    
    // The content of the message (e.g., the text or body of the message)
    private String text;
    
    // The user ID of the sender (unique identifier for the user)
    private int user_id;

    /**
     * Constructs a new Message object with the specified details.
     * 
     * @param username The username of the user sending the message
     * @param type The type of the message (e.g., "text", "notification")
     * @param text The content or body of the message
     * @param user_id The unique user ID of the sender
     */
    public Message(String username, String type, String text, int user_id) {
        this.username = username;
        this.type = type;
        this.text = text;
        this.user_id = user_id;
    }

    /**
     * Retrieves the username of the sender.
     * 
     * @return The username of the sender
     */
    public String getUsername() { 
        return username; 
    }

    /**
     * Sets the username of the sender.
     * 
     * @param username The new username of the sender
     */
    public void setUsername(String username) { 
        this.username = username; 
    }

    /**
     * Retrieves the type of the message.
     * 
     * @return The type of the message (e.g., "text", "notification")
     */
    public String getType() { 
        return type; 
    }

    /**
     * Sets the type of the message.
     * 
     * @param type The new type of the message (e.g., "text", "notification")
     */
    public void setType(String type) { 
        this.type = type; 
    }

    /**
     * Retrieves the content or body of the message.
     * 
     * @return The content of the message
     */
    public String getText() { 
        return text; 
    }

    /**
     * Sets the content or body of the message.
     * 
     * @param text The new content of the message
     */
    public void setText(String text) { 
        this.text = text; 
    }

    /**
     * Retrieves the user ID of the sender.
     * 
     * @return The user ID of the sender
     */
    public int getUser_id() { 
        return user_id; 
    }

    /**
     * Sets the user ID of the sender.
     * 
     * @param user_id The new user ID of the sender
     */
    public void setUser_id(int user_id) { 
        this.user_id = user_id; 
    }

    /**
     * Provides a string representation of the Message object.     * 
     * @return A string representation of the message in the format: 
     *         "<username>, <type>, <text>"
     */
    @Override
    public String toString() {
        return username + ", " + type + ", " + text;
    }
}