package asu.cse360project;

//Class representing a group in the application
public class Group {
	private String groupname;
	private long[] article_list;
	
    // Constructor to initialize a User with specific details
    public Group(String name, long[] list) {
        this.groupname = name;
        this.article_list = list;
    }

    // Default constructor initializes fields to empty strings
    public Group() {
    	this.groupname = "Group";
    	long[] dum = {0L};
    	this.article_list = dum;
    }
    
    public String getName() {
    	return this.groupname;
    }
}