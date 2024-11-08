package asu.cse360project;

//Class representing a group in the application
public class Group {

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
	
    // Constructor to initialize a User with specific details
    public Group(String name, Integer id) {
        this.name = name;
        this.id = id;
        this.special = false;
    }

    // Default constructor initializes fields to empty strings
    public Group() {
    	this.name = "Group";
    	this.id = -1;
    }
    
    public String getName() {
    	return this.name;
    }
    
    public Integer getId() {
    	return this.id;
    }

    public Boolean getSpecial() {
    	return this.special;
    }

    public void setSpecial(boolean special) {
        this.special = special;
    }

    public String toString() {
        return "Group{" +
               "id=" + id +
               ", name='" + name + '\'' +
               '}';
    }
}
