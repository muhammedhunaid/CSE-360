package asu.cse360project;

import java.io.Serializable;
import java.util.ArrayList;

//Class representing a group in the application
public class Group implements Serializable{

    /*******
         * <p> Group. </p>
         * 
         * <p> Description: Class representing a group of articles in the application. Every Group has an integer id which is matched with the database, and a String name. </p>
         * 
         * <p> Copyright: Tu35 © 2024 </p>
         * 
         * @author Tu35
         * 
         * @version 1.00	2024-10-30 Added Group class to manage storing groups of articles
         * 
     */
	
	private String name;
	private Integer id;
    private boolean special;
    private ArrayList<User> admin_users; //list of privaleged users
    private ArrayList<User> viewer_users; //list of non-privaleged users
	
    // Constructor to initialize a User with specific details
    public Group(String name, Integer id, boolean special, ArrayList<User> admin_users, ArrayList<User> viewer_users) {
        this.name = name;
        this.id = id;
        this.special = special;
        this.admin_users = admin_users;
        this.viewer_users = viewer_users;
    }

    // Default constructor initializes fields to empty strings
    public Group(String name, int id) {
    	this.name = name;
    	this.id = id;
    }

    public void setSpecial(boolean special)
    {
        this.special = special;
    }

    public boolean getSpecial()
    {
        return special;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public String getName() {
    	return this.name;
    }
    
    public Integer getId() {
    	return this.id;
    }

    public ArrayList<User> getAdmin_users() {
        return admin_users;
    }

    public ArrayList<User> getViewer_users() {
        return viewer_users;
    }

    public void setAdmin_users(ArrayList<User> admin_users) {
        this.admin_users = admin_users;
    }

    public void setViewer_users(ArrayList<User> viewer_users) {
        this.viewer_users = viewer_users;
    }

    public boolean isAdmin(User user) {
        if(admin_users != null && admin_users.contains(user)) {
            return true;
        }
        return false;
    }

    public boolean isOnlyAdmin(User user) {
        if(admin_users != null && admin_users.size() == 1 && admin_users.contains(user) ) {
            return true;
        }
        return false;
    }

    public String toString() {
        return "Group{" +
               "id=" + id +
               ", name='" + name + '\'' +
               '}';
    }
}
