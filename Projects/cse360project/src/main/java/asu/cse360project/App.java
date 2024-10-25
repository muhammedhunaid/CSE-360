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

    // The main JavaFX scene that will be displayed
    private static Scene scene;
    
    // Represents the current user of the application
    private static User app_user = new User();
    
    // Flag to indicate if the application is setting up the first admin account
    public static boolean setting_up_admin = false;
    
    // Instance of the DatabaseHelper class to handle database operations
    public static final DatabaseHelper databaseHelper = new DatabaseHelper();

    @Override
    // Initializes the primary stage and sets the login scene
    public void start(Stage stage) throws IOException, SQLException {
        // Load the login screen from the FXML file and set the scene
        Parent root = FXMLLoader.load(App.class.getResource("login.fxml"));
        scene = new Scene(root, 700, 700);
        stage.setScene(scene);
        stage.show();

        // Connect to the database
        databaseHelper.connectToDatabase();  
        
        // Check if the database is empty (no users). If empty, initiate admin setup
        if (App.databaseHelper.isDatabaseEmpty()) {
            System.out.println("In-Memory Database is empty");
            setting_up_admin = true;
            setRoot("create_account"); // Load the account creation screen for the first admin
        }
        
        // Display users that have admin roles for debugging purposes
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

    // Sets the app_user with the given User object
    static void setAppUser(User user) {
        app_user = user;
    }

    // Returns the current user of the application
    static User getAppUser() {
        return app_user;
    }

    // Changes the scene to the specified FXML file
    static void setRoot(String fxml) throws IOException {
        FXMLLoader loader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        Parent root = loader.load();
        scene.setRoot(root);  // Set the root of the scene to the newly loaded FXML
    }

    // Changes the scene to the specified FXML file and passes a user to the new scene's controller
    static void setRoot(String fxml, String user) throws IOException {
        FXMLLoader loader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        Parent root = loader.load();
        
        // Get the controller of the loaded FXML and set the user
        SceneController controller = loader.getController();                            
        controller.setUser(user);                                                      
        scene.setRoot(root);  // Set the root of the scene to the newly loaded FXML
    }

    // Main method to launch the JavaFX application
    public static void main(String[] args) {
        launch();  // Launches the JavaFX application
    }
}