/*******
 * <p> NewPwController. </p>
 * 
 * <p> Description: A controller class for the scene where users can create a new password in the JavaFX application. </p>
 * 
 * <p> Copyright: Tu35 2024 </p>
 * 
 * @author Tu35
 * 
 * @version 1.00	2024-10-30 Initial version with basic password reset functionality
 * @version 2.00	2024-11-01 Added password validation and error handling
 * 
 */
package asu.cse360project.LoginControllers;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import asu.cse360project.Singleton;
import asu.cse360project.User;
import asu.cse360project.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

// Controller class for the scene where users can create a new password
public class NewPwController implements Initializable {

    Singleton data = Singleton.getInstance();
    // Current user retrieved from the application
    private User curr_user = data.getAppUser();

    // FXML elements for displaying error messages and user inputs
    @FXML private Text password_error; // Text element to show password validation errors
    @FXML private TextField password; // Input field for the new password
    @FXML private Text re_password_error; // Text element to show re-password validation errors
    @FXML private PasswordField re_password; // Input field for re-entering the new password

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        // Disable error labels on initialization
        Utils.disableNode(password_error);
        Utils.disableNode(re_password_error);
    }

    @FXML
    void Finish(ActionEvent event) throws SQLException, IOException {
        // Get input from the password field
        String pw = password.getText().toString();
        // Validate the password using utility function
        String validate_pw = Utils.validatePassword(pw);

        // Set true if the password and username are valid; otherwise, display an error
        boolean valid_pw = Utils.isValid(password_error, validate_pw);
        boolean same = false; // Variable to check if the passwords match

        // Check if the entered passwords match
        if (password.getText().compareTo(re_password.getText()) == 0) {
            // Disable the re-password error label if passwords match
            Utils.disableNode(re_password_error);
            same = true; // Set same to true if passwords match
        } else {
            // Display error message if passwords do not match
            Utils.setText(re_password_error, "Passwords don't match.", Color.RED);
            same = false; // Set same to false since passwords don't match
            return; // Exit the method if passwords don't match
        }

        // If the password is valid and both entries match, reset the password in the database
        if (valid_pw && same) {
            data.user_db.resetPassword(curr_user.getUsername(), pw); // Reset password in the database
            Utils.setRoot("LoginScenes/login"); // Navigate to the login scene
            data.setAppUser(null); // Clear the current user from the application context
        }
    }
}
