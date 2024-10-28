package asu.cse360project;

//Class representing a group in the application
public class Group {
	private String name;
	private Integer id;
	
    // Constructor to initialize a User with specific details
    public Group(String name, Integer id) {
        this.name = name;
        this.id = id;
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

    public String toString() {
        return "Group{" +
               "id=" + id +
               ", name='" + name + '\'' +
               '}';
    }
}
