/*******
 * <p> LoginController. </p>
 * 
 * <p> Description: A controller class for handling user login and account setup in the JavaFX application. </p>
 * 
 * <p> Copyright: Tu35 2024 </p>
 * 
 * @author Tu35
 * 
 * @version 1.00	2024-10-30 Initial version with basic login and account setup functionalities
 * @version 2.00	2024-11-01 Added password reset and OTP validation features
 * @version 3.00	2024-11-15 Enhanced user role management and navigation logic
 * 
 */
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

        // Check if fields are empty
        if (username.isEmpty() || password.isEmpty()) {
            Utils.showErrorFeedback(login_error, "Please fill in all fields");
            return;
        }

        // Attempt to log in using the provided username and password
        User user = data.getUserHelper().validateUser(username, password);
        if (user == null) {
            Utils.showErrorFeedback(login_error, "Invalid username or password");
            return;
        }

        // Show success message before transitioning
        Utils.showSuccessFeedback(login_error, "Login successful!");

        // Set the current user in the application
        data.setAppUser(user);

        // Check if the user needs to reset their password
        if (user.need_password_reset()) {
            Boolean otp_expired = data.user_db.isOtpExpired(user.getUsername());
            if (otp_expired) {
                // Display error message if OTP has expired
                Utils.setText(login_error, "One time password has expired. Please contact administrator.", Color.RED);
                return;
            } else {
                // If OTP is valid, prompt the user to set a new password
                Utils.setRoot("LoginScenes/new_pw");
                return;
            }
        }
        
        // If the user hasn't set up their account information completely
        if (user.getFirst_name() == null || user.getLast_name() == null || 
            user.getEmail() == null || user.getFirst_name().isEmpty() || 
            user.getLast_name().isEmpty() || user.getEmail().isEmpty()) {
            // Navigate to the account setup screen
            Utils.setRoot("LoginScenes/setup_account");
            return;
        }

        // If the user has multiple roles (Admin, Instructor, Student)
        if (user.hasMultipleRoles()) {
            // Navigate to role selection screen
            Utils.setRoot("LoginScenes/select_role");
            return;
        }

        // If the user has a single role, set it and navigate to the dashboard
        user.setLoginRole(user.getRole());        
        Utils.setRoot("DashboardScenes/dashboard");
        return;
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
