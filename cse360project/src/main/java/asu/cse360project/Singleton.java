package asu.cse360project;

import javafx.scene.Scene;
import javafx.scene.layout.StackPane;

public class Singleton {
    // Private static instance variable
    private static Singleton instance;
    public DatabaseHelper db;
    public Scene scene;
    public boolean setting_up_admin = false;
    public User app_user;
    public boolean editing_article = false;
    public StackPane content_area;

    private Singleton() {
        // Initialization code
    }

    // Public method to provide access to the instance
    public static synchronized Singleton getInstance() {
        if (instance == null) {
            instance = new Singleton();
        }
        return instance;
    }
    
    public void setAppUser(User app_user) {
        this.app_user = app_user;
    } 

    public User getAppUser() {
        return app_user;
    } 
}