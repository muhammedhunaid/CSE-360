package asu.cse360project;

import java.util.ArrayList;

import asu.cse360project.DatabaseHelpers.*;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;

/**
 * Singleton class to manage application-wide state and provide access to database helpers.
 * 
 * This class is designed to follow the Singleton pattern, ensuring only one instance of the class exists.
 * It holds references to database helpers for users and group articles, as well as the current application user.
 * Additionally, it manages the state of the application, including the setup of admin roles and article editing.
 * 
 * @author Tu35
 * @version 1.00 2024-10-30 Created to manage application state and database access
 */
public class Singleton {
    // Private static instance variable
    private static Singleton instance;
   
    // Database helpers for managing user and group article data
    public DatabaseHelper db;
    public UserHelper user_db;
    public GroupArticlesHelper group_articles_db;

    // Scene and content area for managing the graphical user interface
    public Scene scene;
    public StackPane content_area;

    // Flags to track the state of the application
    public boolean setting_up_admin = false; // Indicates if the admin role is being set up
    public boolean editing_article = false; // Indicates if an article is being edited
    public Article article; // The article currently being edited

    // The user currently logged into the application
    public User app_user;
    // List of groups the user is allowed to edit
    public ArrayList<Integer> edit_group = null;

    //Group id
    public int SAGGroupId = 0;

    /**
     * Public method to provide access to the instance of the Singleton class.
     * 
     * This method ensures that only one instance of the Singleton class exists.
     * If an instance does not exist, it creates a new one. Otherwise, it returns the existing instance.
     * 
     * @return The instance of the Singleton class.
     */
    public static synchronized Singleton getInstance() {
        if (instance == null) {
            instance = new Singleton();
        }
        return instance;
    }

    /**
     * Sets the database helpers for the Singleton instance.
     * 
     * This method updates the database helpers for users and group articles based on the provided DatabaseHelper instance.
     * 
     * @param db The DatabaseHelper instance to use for setting up database helpers.
     */
    public void setDbHelpers(DatabaseHelper db)
    {
        this.db = db;
        this.user_db = db.getUser_helper();
        this.group_articles_db = db.getGroupArticlesHelper();
    }
    
    /**
     * Sets the current application user.
     * 
     * This method updates the app_user field with the provided User instance.
     * 
     * @param app_user The User instance to set as the current application user.
     */
    public void setAppUser(User app_user) {
        this.app_user = app_user;
    } 

    /**
     * Returns the current application user.
     * 
     * This method retrieves and returns the current User instance associated with the application.
     * 
     * @return The current User instance.
     */
    public User getAppUser() {
        return app_user;
    } 

    /**
     * Returns the UserHelper instance for managing user data.
     * 
     * This method retrieves and returns the UserHelper instance associated with the Singleton instance.
     * 
     * @return The UserHelper instance.
     */
    public UserHelper getUserHelper() {
        return user_db;
    }
}