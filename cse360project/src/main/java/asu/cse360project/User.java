package asu.cse360project;

import java.io.Serializable;

/**
 * Represents a user in the application with their associated details and roles.
 * This class implements Serializable for persistence and Comparable for sorting.
 * Users can have different roles (admin, instructor, student) and login states.
 * 
 * @author Tu35
 * @version 2.00 2024-10-30 Updated for phase 2
 * @version 1.00 2024-10-03 Initial version
 */
public class User implements Serializable, Comparable<User> {
    /** Username for authentication */
    private String username;
    
    /** Unique identifier for the user */
    private int id;
    
    /** User's role in the system (admin, instructor, student) */
    private String role;
    
    /** User's first name */
    private String first_name;
    
    /** User's middle name */
    private String middle_name;
    
    /** User's last name */
    private String last_name;
    
    /** User's preferred name for display */
    private String pref_name;
    
    /** User's email address */
    private String email;
    
    /** Indicator for password reset status */
    private String password_reset;
    
    /** Role the user is currently logged in as */
    private String login_role = "";

    /**
     * Constructs a new User with complete details.
     * 
     * @param username The user's username for authentication
     * @param first_name User's first name
     * @param middle_name User's middle name
     * @param last_name User's last name
     * @param pref_name User's preferred name
     * @param email User's email address
     * @param role User's role in the system
     * @param password_reset Password reset status
     * @param id Unique identifier for the user
     */
    public User(String username, String first_name, String middle_name, String last_name, String pref_name, String email, String role, String password_reset, int id) {
        this.username = username;
        this.first_name = first_name;
        this.middle_name = middle_name;
        this.last_name = last_name;
        this.pref_name = pref_name;
        this.email = email;
        this.role = role;
        this.password_reset = password_reset;
        this.id = id;
    }

    /**
     * Default constructor that initializes a User with empty values.
     * Used when creating a new user before setting their details.
     */
    public User() {
        this.username = "";
        this.first_name = "";
        this.middle_name = "";
        this.last_name = "";
        this.pref_name = "";
        this.email = "";
        this.role = "";
        this.password_reset = "";
    }

    /**
     * Gets the user's username.
     * @return The username used for authentication
     */
    public String getUsername() {
        return username;
    }
    
    /**
     * Sets the user's username.
     * @param name The new username to set
     */
    public void setUsername(String name) {
        username = name;
    }

    /**
     * Gets the user's role.
     * @return The user's role (admin, instructor, student)
     */
    public String getRole() {
        return role;
    }

    /**
     * Gets the password reset status.
     * @return The password reset status indicator
     */
    public String getPwReset() {
        return password_reset;
    }

    /**
     * Sets the user's role.
     * @param role The new role to assign to the user
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * Gets the user's first name.
     * @return The user's first name
     */
    public String getFirst_name() {
        return first_name;
    }

    /**
     * Sets the user's first name.
     * @param first_name The new first name to set
     */
    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    /**
     * Gets the user's unique identifier.
     * @return The user's ID
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the user's unique identifier.
     * @param id The new ID to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the user's middle name.
     * @return The user's middle name
     */
    public String getMiddle_name() {
        return middle_name;
    }

    /**
     * Sets the user's middle name.
     * @param middle_name The new middle name to set
     */
    public void setMiddle_name(String middle_name) {
        this.middle_name = middle_name;
    }

    /**
     * Gets the user's last name.
     * @return The user's last name
     */
    public String getLast_name() {
        return last_name;
    }

    /**
     * Sets the user's last name.
     * @param last_name The new last name to set
     */
    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    /**
     * Gets the user's preferred name.
     * @return The user's preferred name
     */
    public String getPref_name() {
        return pref_name;
    }

    /**
     * Sets the user's preferred name.
     * @param pref_name The new preferred name to set
     */
    public void setPref_name(String pref_name) {
        this.pref_name = pref_name;
    }

    /**
     * Gets the user's email address.
     * @return The user's email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the user's email address.
     * @param email The new email address to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Checks if the user has an admin role.
     * @return True if the user has an admin role, false otherwise
     */
    public boolean isAdmin() {
        return role.contains("admin");
    }

    /**
     * Checks if the user has an instructor role.
     * @return True if the user has an instructor role, false otherwise
     */
    public boolean isInstructor() {
        return role.contains("instructor");
    }

    /**
     * Checks if the user has a student role.
     * @return True if the user has a student role, false otherwise
     */
    public boolean isStudent() {
        return role.contains("student");
    }

    /**
     * Checks if the user has only a student role.
     * @return True if the user has only a student role, false otherwise
     */
    public boolean isOnlyStudent() {
        return role.equals("student");
    }

    /**
     * Checks if the user needs to reset their password.
     * @return True if the user needs to reset their password, false otherwise
     */
    public boolean need_password_reset() {
        return !password_reset.isEmpty();
    }

    /**
     * Sets the user's login role.
     * @param input_role The new login role to set
     */
    public void setLoginRole(String input_role) {
        login_role = input_role;
    }

    /**
     * Gets the user's login role.
     * @return The user's current login role
     */
    public String getLoginRole() {
        return login_role;
    }

    /**
     * Resets the user's login role to an empty string.
     */
    public void resetLoginRole() {
        login_role = "";
    }

    /**
     * Checks if the user has multiple roles.
     * @return True if the user has multiple roles, false otherwise
     */
    public boolean hasMultipleRoles() {
        return role.contains(",");
    }

    /**
     * Returns a string representation of the User object.
     * @return A formatted string with the user's details
     */
    @Override
    public String toString() {
        return "User{" +
                "first name=" + first_name +
                ", middle name=" + middle_name +
                ", last name=" + last_name +
                ", preferred name=" + pref_name +
                ", email=" + email +
                ", username='" + username + '\'' +
                ", role='" + role + '\'' +
                '}';
    }

    /**
     * Checks if the object is equal to the User object.
     * @param o The object to compare with
     * @return True if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        User u = (User)o;
        
        if(u == null){
            return false;
        }

        if(this.username.equals(u.getUsername())) {
            return true;
        }
        return false;
    }

    /**
     * Compares the User object with another User object.
     * @param o The User object to compare with
     * @return A negative integer, zero, or a positive integer as this object is less than, equal to, or greater than the specified object
     */
    @Override
    public int compareTo(User o) {
        return this.getUsername().compareTo(o.getUsername());
    }
}
