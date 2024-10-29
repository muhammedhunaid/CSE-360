package asu.cse360project;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.sql.SQLException;
import asu.cse360project.DatabaseHelpers.DatabaseHelper;

/**
 * JavaFX App: JavaFX driver class that initializes and starts the application
 */
public class App extends Application {

    // The main JavaFX scene that will be displayed
    private static Scene scene;
    
    // Instance of the DatabaseHelper class to handle database operations
    private static final DatabaseHelper databaseHelper = new DatabaseHelper();
    Singleton data;

    @Override
    // Initializes the primary stage and sets the login scene
    public void start(Stage stage) throws IOException, SQLException {

        // Load the login screen from the FXML file and set the scene
        Parent root = FXMLLoader.load(App.class.getResource("LoginScenes/login.fxml"));
        scene = new Scene(root, 1000, 1000);
        stage.setScene(scene);
        stage.show();

        // Connect to the database
        databaseHelper.connectToDatabase();  

        data = Singleton.getInstance();  
        data.setDbHelpers(databaseHelper);   
        data.scene = scene;

        // Check if the database is empty (no users). If empty, initiate admin setup
        if (App.databaseHelper.isDatabaseEmpty()) {
            System.out.println("In-Memory Database is empty");
            data.setting_up_admin = true;
            Utils.setRoot("LoginScenes/create_account"); // Load the account creation screen for the first admin
        }

        
        User a = new User();
        a.setLoginRole("admin");
        data.setAppUser(a);
        Utils.setRoot("DashboardScenes/dashboard");

        // Display all in db for debugging purposes
        databaseHelper.getUser_helper().displayUsersByAdmin();

        // Handle the event when the application is closed, ensuring the database connection is closed
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                databaseHelper.closeConnection();  // Close database connection on application exit
                System.out.println("Closing db");
            }
        });
    }

    // Main method to launch the JavaFX application
    public static void main(String[] args) {
        launch();  // Launches the JavaFX application
    }
}