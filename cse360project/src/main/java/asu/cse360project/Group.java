package asu.cse360project;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Represents a group of articles in the application with associated users and permissions.
 * Each group has a unique identifier, name, and lists of users with different access levels.
 * Groups can be marked as special for specific handling in the application.
 * This class implements Serializable to support object persistence.
 *
 * @author Tu35
 * @version 1.00 2024-10-30 Initial version for managing article groups
 */
public class Group implements Serializable {
    /** Name of the group */
    private String name;
    
    /** Unique identifier for the group */
    private Integer id;
    
    /** Flag indicating if this is a special group with additional privileges */
    private boolean special;
    
    /** List of users with administrative privileges for this group */
    private ArrayList<User> admin_users;
    
    /** List of users with view-only privileges for this group */
    private ArrayList<User> viewer_users;

    /**
     * Constructs a new Group with complete details.
     *
     * @param name Name of the group
     * @param id Unique identifier for the group
     * @param special Whether this is a special group
     * @param admin_users List of users with administrative privileges
     * @param viewer_users List of users with view-only privileges
     */
    public Group(String name, Integer id, boolean special, ArrayList<User> admin_users, ArrayList<User> viewer_users) {
        this.name = name;
        this.id = id;
        this.special = special;
        this.admin_users = admin_users;
        this.viewer_users = viewer_users;
    }

    /**
     * Constructs a minimal Group with only essential details.
     * Admin and viewer lists will be initialized when needed.
     *
     * @param name Name of the group
     * @param id Unique identifier for the group
     */
    public Group(String name, int id) {
    	this.name = name;
    	this.id = id;
    }

    /**
     * Sets the special status of the group.
     * @param special New special status to set
     */
    public void setSpecial(boolean special)
    {
        this.special = special;
    }

    /**
     * Gets the special status of the group.
     * @return True if this is a special group, false otherwise
     */
    public boolean getSpecial()
    {
        return special;
    }

    /**
     * Sets the name of the group.
     * @param name New name for the group
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Gets the name of the group.
     * @return The group's name
     */
    public String getName() {
    	return this.name;
    }
    
    /**
     * Gets the unique identifier of the group.
     * @return The group's ID
     */
    public Integer getId() {
    	return this.id;
    }

    /**
     * Gets the list of users with administrative privileges.
     * @return List of admin users
     */
    public ArrayList<User> getAdmin_users() {
        return admin_users;
    }

    /**
     * Gets the list of users with view-only privileges.
     * @return List of viewer users
     */
    public ArrayList<User> getViewer_users() {
        return viewer_users;
    }

    /**
     * Sets the list of users with administrative privileges.
     * @param admin_users New list of admin users
     */
    public void setAdmin_users(ArrayList<User> admin_users) {
        this.admin_users = admin_users;
    }

    /**
     * Sets the list of users with view-only privileges.
     * @param viewer_users New list of viewer users
     */
    public void setViewer_users(ArrayList<User> viewer_users) {
        this.viewer_users = viewer_users;
    }

    /**
     * Checks if a user has administrative privileges for this group.
     * @param user User to check
     * @return True if the user is an admin of this group, false otherwise
     */
    public boolean isAdmin(User user) {
        if(admin_users != null && admin_users.contains(user)) {
            return true;
        }
        return false;
    }

    /**
     * Checks if a user is the sole administrator of this group.
     * @param user User to check
     * @return True if the user is the only admin of this group, false otherwise
     */
    public boolean isOnlyAdmin(User user) {
        if(admin_users != null && admin_users.size() == 1 && admin_users.contains(user) ) {
            return true;
        }
        return false;
    }

    /**
     * Returns a string representation of the Group object.
     * @return A formatted string containing the group's ID and name
     */
    @Override
    public String toString() {
        return "Group{" +
               "id=" + id +
               ", name='" + name + '\'' +
               '}';
    }
}
