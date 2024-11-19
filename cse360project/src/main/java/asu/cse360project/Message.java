package asu.cse360project;


//Class representing a group in the application
public class Message {
    private String username;
    private String type;
    private String text;
    private int user_id;

    public Message(String username, String type, String text, int user_id) {
        this.username = username;
        this.type = type;
        this.text = text;
        this.user_id = user_id;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public int getUser_id() { return user_id; }
    public void setUser_id(int user_id) { this.user_id = user_id; }
    
    public String toString() {
		return username + ", " + type + ", " + text ;
    }
}
