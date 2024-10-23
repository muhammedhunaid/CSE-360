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

/**
 * JavaFX App: JavaFX driver class that initializes and starts the application
 */
public class App extends Application {

    Singleton data = Singleton.getInstance();
    // The main JavaFX scene that will be displayed
    private static Scene scene;
    
    // Instance of the DatabaseHelper class to handle database operations
    public static final DatabaseHelper databaseHelper = new DatabaseHelper();

    @Override
    // Initializes the primary stage and sets the login scene
    public void start(Stage stage) throws IOException, SQLException {

        // Load the login screen from the FXML file and set the scene
        Parent root = FXMLLoader.load(App.class.getResource("LoginScenes/login.fxml"));
        scene = new Scene(root, 700, 700);
        stage.setScene(scene);
        stage.show();


        data.db = databaseHelper;
        data.scene = scene;

        // Connect to the database
        databaseHelper.connectToDatabase();  
        
        // Check if the database is empty (no users). If empty, initiate admin setup
        if (App.databaseHelper.isDatabaseEmpty()) {
            System.out.println("In-Memory Database is empty");
            data.setting_up_admin = true;
            Utils.setRoot("create_account"); // Load the account creation screen for the first admin
        }
        
        // Display all in db for debugging purposes
        databaseHelper.displayUsersByAdmin();

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