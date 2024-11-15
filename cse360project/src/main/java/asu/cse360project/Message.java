package asu.cse360project;

import javafx.beans.property.SimpleStringProperty;

//Class representing a group in the application
public class Message {
    private final SimpleStringProperty username;
    private final SimpleStringProperty type;
    private final SimpleStringProperty text;

    public Message(String username, String type, String text) {
        this.username = new SimpleStringProperty(username);
        this.type = new SimpleStringProperty(type);
        this.text = new SimpleStringProperty(text);
    }

    public String getUsername() { return username.get(); }
    public void setUsername(String username) { this.username.set(username); }

    public String getType() { return type.get(); }
    public void setType(String type) { this.type.set(type); }

    public String getText() { return text.get(); }
    public void setText(String text) { this.text.set(text); }
    
    public String toString() {
		return username + ", " + type + ", " + text ;
    }
}
