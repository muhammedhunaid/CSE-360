package asu.cse360project;

/**
 
<p> App Class. </p>
<p> Description: This class launches the JavaFX Screen, initiates the first user
with Admin role, and redirects to the login page  </p>
<p> Copyright: Tu35 Team © 2024 </p>
@version 1.00        2024-10-09 Phase 1 implementation of the user management controller
@author Tu35 Team
*/

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;
    public static User user = new User();

    @Override
    public void start(Stage stage) throws IOException {
         // Reset the database at startup
        Database.resetDatabase();

        FXMLLoader loader = new FXMLLoader(App.class.getResource("dashboard.fxml"));
        Parent root = loader.load();
        scene = new Scene(root, 500, 500);
        stage.setScene(scene);
        stage.show();
    }
    //Set the root of the scene to the fxml file
    static void setRoot(String fxml) throws IOException {
        FXMLLoader loader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        Parent root = loader.load();
        scene.setRoot(root); //JavaFX shows one scene at a time
    }
    //Set the root of the scene to the fxml file
    static void setRoot(String fxml, User user) throws IOException {
        FXMLLoader loader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        Parent root = loader.load();
        SceneController controller = loader.getController(); 
        controller.setUser(user);
        scene.setRoot(root);
    }    
    //Launch the application
    public static void main(String[] args) {
        launch();
    }

}