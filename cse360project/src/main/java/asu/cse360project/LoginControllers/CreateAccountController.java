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
        String username = username_field.getText();
        String password = password_field.getText();

        if (username.isEmpty() || password.isEmpty() || re_password_field.getText().isEmpty()) {
            Utils.showErrorFeedback(username_error, "Please fill in all fields");
            return;
        }

        String validate_pw = Utils.validatePassword(password);
        String validate_uname = Utils.validateUsername(username);

        boolean valid_pw = Utils.isValid(password_error, validate_pw);
        boolean valid_uname = Utils.isValid(username_error, validate_uname);
        boolean same = false;

        if(password_field.getText().compareTo(re_password_field.getText()) == 0) {
            same = true;
            Utils.disableNode(re_password_error);
        } else {
            Utils.showErrorFeedback(re_password_error, "Passwords don't match");
            return;
        }

        if(valid_pw && valid_uname && same) {
            try {
                if(data.setting_up_admin) {
                    data.user_db.addUser(username, password, "admin");
                    data.setting_up_admin = false;
                } else {
                    data.user_db.register(currentUser.getUsername(), username, password);
                    User user = data.user_db.getUser(username);
                    data.group_articles_db.addUsertoGeneralGroups(user);
                }          
                
                Utils.showSuccessFeedback(title, "Account created successfully!");
                PauseTransition pause = new PauseTransition(Duration.seconds(2));
                pause.setOnFinished(e -> {
                    try {
                        Utils.setRoot("LoginScenes/login");
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                });
                pause.play();
            } catch (Exception e) {
                Utils.showErrorFeedback(username_error, "Error creating account: " + e.getMessage());
            }
        }
    }
}
