package asu.cse360project;

import asu.cse360project.DatabaseHelpers.*;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;

public class Singleton {
    // Private static instance variable
    private static Singleton instance;
   
    public DatabaseHelper db;
    public UserHelper user_db;
    public GroupArticlesHelper group_articles_db;

    public Scene scene;
    public StackPane content_area;

    public boolean setting_up_admin = false;
    public boolean editing_article = false;
    public Article article;

    public User app_user;
    public Group edit_group = null;

    // Public method to provide access to the instance
    public static synchronized Singleton getInstance() {
        if (instance == null) {
            instance = new Singleton();
        }
        return instance;
    }

    public void setDbHelpers(DatabaseHelper db)
    {
        this.db = db;
        this.user_db = db.getUser_helper();
        this.group_articles_db = db.getGroupArticlesHelper();
    }
    
    public void setAppUser(User app_user) {
        this.app_user = app_user;
    } 

    public User getAppUser() {
        return app_user;
    } 

    public UserHelper getUserHelper() {
        return user_db;
    }
}