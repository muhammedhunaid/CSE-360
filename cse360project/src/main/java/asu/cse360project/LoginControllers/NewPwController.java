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
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;

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
        // Get input from both password fields
        String pw = password.getText();
        String confirmPw = re_password.getText();

        // Clear previous error messages
        Utils.disableNode(password_error);
        Utils.disableNode(re_password_error);

        // First validate the password
        String validate_pw = Utils.validatePassword(pw);
        boolean valid_pw = Utils.isValid(password_error, validate_pw);

        // Then check if passwords match
        boolean same = pw.equals(confirmPw);
        if (!same) {
            Utils.setText(re_password_error, "Passwords don't match", Color.RED);
            return;
        }

        // If password is valid and matches, update it in the database
        if (valid_pw && same) {
            try {
                // Get the current user's details before resetting password
                User userDetails = data.user_db.getUser(curr_user.getUsername());
                
                // Reset the password
                data.user_db.resetPassword(curr_user.getUsername(), pw);
                
                // Update the user details to ensure they're preserved
                if (userDetails != null) {
                    data.user_db.finishAccountSetup(
                        curr_user.getUsername(),
                        userDetails.getFirst_name(),
                        userDetails.getMiddle_name(),
                        userDetails.getLast_name(),
                        userDetails.getPref_name(),
                        userDetails.getEmail()
                    );
                }
                
                Utils.showSuccessFeedback(password_error, "Password updated successfully!");
                
                // Wait 1 second before redirecting to login
                PauseTransition pause = new PauseTransition(Duration.seconds(1));
                pause.setOnFinished(e -> {
                    try {
                        Utils.setRoot("LoginScenes/login");
                        data.setAppUser(null);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                });
                pause.play();
            } catch (SQLException e) {
                Utils.setText(password_error, "Error updating password: " + e.getMessage(), Color.RED);
            }
        }
    }
}
