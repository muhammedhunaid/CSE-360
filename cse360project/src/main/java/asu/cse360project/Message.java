package asu.cse360project;

/**
 * Represents a message in the application's communication system.
 * Messages can be of different types (e.g., text messages, notifications)
 * and contain information about both the content and the sender.
 * This class provides methods to manage message content and metadata.
 *
 * @author Tu35
 * @version 1.00 2024-11-17 Initial version with core messaging functionality
 */
public class Message {
    /** Username of the message sender */
    private String username;
    
    /** Type of message (e.g., text message, notification) */
    private String type;
    
    /** Content/body of the message */
    private String text;
    
    /** Unique identifier of the sender */
    private int user_id;

    /**
     * Constructs a new Message with complete details.
     * 
     * @param username Username of the message sender
     * @param type Type of message (e.g., "text", "notification")
     * @param text Content/body of the message
     * @param user_id Unique identifier of the sender
     */
    public Message(String username, String type, String text, int user_id) {
        this.username = username;
        this.type = type;
        this.text = text;
        this.user_id = user_id;
    }

    /**
     * Gets the username of the message sender.
     * @return The sender's username
     */
    public String getUsername() { 
        return username; 
    }

    /**
     * Sets the username of the message sender.
     * @param username New username to set
     */
    public void setUsername(String username) { 
        this.username = username; 
    }

    /**
     * Gets the type of message.
     * @return The message type (e.g., "text", "notification")
     */
    public String getType() { 
        return type; 
    }

    /**
     * Sets the type of message.
     * @param type New message type to set
     */
    public void setType(String type) { 
        this.type = type; 
    }

    /**
     * Gets the content/body of the message.
     * @return The message content
     */
    public String getText() { 
        return text; 
    }

    /**
     * Sets the content/body of the message.
     * @param text New message content to set
     */
    public void setText(String text) { 
        this.text = text; 
    }

    /**
     * Gets the unique identifier of the message sender.
     * @return The sender's user ID
     */
    public int getUser_id() { 
        return user_id; 
    }

    /**
     * Sets the unique identifier of the message sender.
     * @param user_id New user ID to set
     */
    public void setUser_id(int user_id) { 
        this.user_id = user_id; 
    }

    /**
     * Returns a string representation of the Message object.
     * Includes username, type, and content of the message.
     * 
     * @return A formatted string containing message details
     */
    @Override
    public String toString() {
        return username + ", " + type + ", " + text;
    }
}