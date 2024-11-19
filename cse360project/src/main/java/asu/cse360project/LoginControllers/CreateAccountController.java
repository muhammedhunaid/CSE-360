package asu.cse360project.LoginControllers;

import asu.cse360project.Utils;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import asu.cse360project.Singleton;
import asu.cse360project.User;

import java.io.IOException;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;

// Controller class for creating a user account, implementing Initializable for setup
public class CreateAccountController implements Initializable{

    // Current user instance retrieved from the application
    Singleton data = Singleton.getInstance();
    User currentUser = data.getAppUser();

    // FXML elements for user input and error messages
    @FXML private TextField password_field;     // Field for password input
    @FXML private Text password_error;          // Text element for password error message
    @FXML private PasswordField re_password_field; // Field for re-entering password
    @FXML private Label re_password_error;      // Label for re-enter password error message
    @FXML private TextField username_field;     // Field for username input
    @FXML private Text username_error;          // Text element for username error message
    @FXML private Label title;                   // Label for displaying titles and messages

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        // Remove error messages from the scene on initialization
        Utils.disableNode(password_error);
        Utils.disableNode(re_password_error);
        Utils.disableNode(username_error);

        // Check if no users are set up at startup; if true, prompt for Admin setup
        if(data.setting_up_admin) {
            Utils.setLabel(title, "Setting up Admin", Color.BLACK);
        }
    }

    @FXML   // Method to handle the join button action for creating accounts
    void join(ActionEvent event) throws IOException, InterruptedException, SQLException {
        // Get input from text fields and validate the inputs

        // Retrieve username and password input from fields
        String username = username_field.getText();
        String password = password_field.getText();

        // Validate password and username inputs
        String validate_pw = Utils.validatePassword(password);
        String validate_uname = Utils.validateUsername(username);

        // Check if the password and username are valid; true if valid, false otherwise
        boolean valid_pw = Utils.isValid(password_error, validate_pw);
        boolean valid_uname = Utils.isValid(username_error, validate_uname);
        boolean same = false; // Flag to check if passwords match

        // Check if the entered password and re-entered password match
        if(password_field.getText().compareTo(re_password_field.getText()) == 0) {
            same = true; // Set flag to true if passwords match
        } else {
            // Display error if passwords do not match
            Utils.setLabel(re_password_error, "Passwords don't match.", Color.RED);
            same = false; // Set flag to false
        }

        // Proceed if username and password are valid and match
        if(valid_pw && valid_uname && same) {
            // Add user to database based on whether setting up admin or regular user
            if(data.setting_up_admin) {
                data.user_db.addUser(username, password, "admin"); // Add as admin
                data.setting_up_admin = false; // Switch off admin setup
            } else {
                data.user_db.register(currentUser.getUsername(), username, password); // Register user
                User user = data.user_db.getUser(username);
                data.group_articles_db.addUsertoGeneralGroups(user);
            }          
            
            // Temporarily display "Account created" message before changing to login screen
            Utils.setLabel(title, "Account Created", Color.GREEN);
            PauseTransition pause = new PauseTransition();
            pause.setDuration(Duration.seconds(2)); // Pause for 2 seconds
            pause.setOnFinished(ActionEvent -> {
                try {
                    Utils.setRoot("LoginScenes/login"); // Switch to the login screen after the pause
                } catch (IOException e) {
                    e.printStackTrace(); // Print stack trace if an exception occurs
                }
            });
            pause.play(); // Start the pause transition
        }
    }
}
