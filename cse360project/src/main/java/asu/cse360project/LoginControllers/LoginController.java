package asu.cse360project.LoginControllers;

import asu.cse360project.Utils;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;


import asu.cse360project.Singleton;
import asu.cse360project.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

/**
 * Controller class for the Login screen.
 * Handles user login and account creation via an invite code.
 */
public class LoginController implements Initializable {

    Singleton data = Singleton.getInstance();

    // TextFields for user input: invite code, password, and username
    @FXML private TextField invite_code_textfield;
    @FXML private TextField password_textfield;
    @FXML private TextField username_textfield;

    // Text element to display login errors or messages
    @FXML private Text login_error;

    @Override
    // Initialize the login screen, disabling the error message initially
    public void initialize(URL arg0, ResourceBundle arg1) {
        Utils.disableNode(login_error);  // Hide the error message text
    }

    /**
     * Handles the login button action.
     * Checks the username and password, validates the user, and navigates to the appropriate screen.
     */
    @FXML
    private void login(ActionEvent event) throws IOException, SQLException {
        // Get input values for username and password
        String username = username_textfield.getText().toString();
        String password = password_textfield.getText().toString();

        // Attempt to log in using the provided username and password
        User curr_user = data.user_db.login(username, password);

        // If a valid user is found
        if (curr_user != null) {
            // Set the current user in the application
            data.setAppUser(curr_user);

            // Check if the user needs to reset their password
            if (curr_user.need_password_reset()) {   
                // Check if the one-time password (OTP) has expired
                Boolean otp_expired = data.user_db.isOtpExpired(curr_user.getUsername());
                if (otp_expired) {   
                    // Display error message if OTP has expired
                    Utils.setText(login_error, "One time password has expired," + curr_user.getPwReset(), Color.RED);
                    return;
                } else {
                    // If OTP is still valid, prompt the user to set a new password
                    Utils.setRoot("LoginScenes/new_pw");
                    return;
                }
            }
            
            // If the user hasn't set up their account information (like first name)
            if (curr_user.getFirst_name() == null) {
                // Navigate to the account setup screen
                Utils.setRoot("LoginScenes/setup_account");
                return;
            }

            // If the user has multiple roles (Admin, Instructor, Student)
            if (curr_user.hasMultipleRoles()) {
                // Navigate to role selection screen
                Utils.setRoot("LoginScenes/select_role");
                return;
            }

            // If the user has a single role, set it and navigate to the dashboard
            curr_user.setLoginRole(curr_user.getRole());             
            Utils.setRoot("DashboardScenes/dashboard");
            return;

        } else {
            // If no user is found, display an error message
            Utils.setText(login_error, "Username or Password not found", Color.RED);
        }
    }

    /**
     * Handles the join button action.
     * Validates the invite code and navigates the user to the account creation screen.
     */
    @FXML
    private void join(ActionEvent event) throws IOException, SQLException {
        // Get the invite code entered by the user
        String invite_code = invite_code_textfield.getText().toString();

        // Check if the invite code is valid
        User new_User = data.user_db.checkInviteCode(invite_code);

        // If a valid invite code is found, navigate to the account creation screen
        if (new_User != null) {
            data.setAppUser(new_User);
            Utils.setRoot("LoginScenes/create_account");
        } else {
            // If the invite code is invalid, display an error message
            Utils.setText(login_error, "Invite Code not found", Color.RED);
        }
    }
}