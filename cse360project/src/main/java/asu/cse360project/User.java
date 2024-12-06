package asu.cse360project;

import java.io.Serializable;

// Class representing a user in the application
public class User implements Comparable<User>, Serializable{
    /**
     * 
     * <p> Description: This class provides methods to get and set user details, check user roles, and manage login roles. User class represents a user with specific details such as username, role, first name, and password reset status </p>
     * 
     * <p> Copyright: Tu35 2024 </p>
     * 
     * @author Tu35
     * 
     * @version 2.00 2024-10-30 Updated for phase 2
     * @version 1.00 2024-10-03 Created for phase 1
     * 
     * 
     */
    
    private String username; // User's username
    private int id; //User's id
    private String role; // User's role (e.g., admin, instructor, student)
    private String first_name; // User's first name
    private String middle_name; // User's middle name
    private String last_name; // User's last name
    private String pref_name; // User's preferred name
    private String email; // User's email
    private String password_reset; // Indicator for password reset status
    private String login_role = ""; // Role the user is currently logged in as

    // Constructor to initialize a User with specific details
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

    // Default constructor initializes fields to empty strings
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

    // Getter for the username
    public String getUsername() {
        return username;
    }
    
    // Setter for the username
    public void setUsername(String name) {
        username = name;
    }

    // Getter for the role
    public String getRole() {
        return role;
    }

    // Getter for the password reset status
    public String getPwReset() {
        return password_reset;
    }

    // Setter for the role
    public void setRole(String role) {
        this.role = role;
    }

    // Getter for the first name
    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMiddle_name() {
        return middle_name;
    }

    public void setMiddle_name(String middle_name) {
        this.middle_name = middle_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getPref_name() {
        return pref_name;
    }

    public void setPref_name(String pref_name) {
        this.pref_name = pref_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // Method to check if the user has an admin role
    public boolean isAdmin() {
        return role.contains("admin"); // Returns true if the role includes "admin"
    }

    // Method to check if the user has an instructor role
    public boolean isInstructor() {
        return role.contains("instructor"); // Returns true if the role includes "instructor"
    }

    // Method to check if the user has a student role
    public boolean isStudent() {
        return role.contains("student"); // Returns true if the role includes "student"
    }

    public boolean isOnlyStudent() {
        return role.equals("student");
    }

    // Method to determine if the user needs to reset their password
    public boolean need_password_reset() {
        return !password_reset.isEmpty(); // Returns true if the password_reset field is not empty
    }

    // Setter for the user's login role
    public void setLoginRole(String input_role) {
        login_role = input_role; // Sets the login role to the input role
    }

    // Getter for the user's login role
    public String getLoginRole() {
        return login_role; // Returns the current login role
    }

    // Method to reset the user's login role to an empty string
    public void resetLoginRole() {
        login_role = ""; // Resets the login role
    }

    // Method to check if the user has multiple roles
    public boolean hasMultipleRoles() {
        return role.contains(","); // Returns true if the role contains a comma, indicating multiple roles
    }

    // Override of the toString method to provide a string representation of the User object
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
                '}'; // Returns a formatted string with the user's first name, username, and role
    }

    @Override
    public int compareTo(User o) {
        if(o.getId() == this.getId())
        {
            return 1;
        }
        return 0;
    }
}
