package asu.cse360project;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

// Controller class for setting up a user account in the application
public class SetupAccountController implements Initializable {

    // Current user retrieved from the application context
    User current_user = App.getAppUser();

    // FXML fields for user input and error messages
    @FXML private TextField email_field; // Input field for the user's email address
    @FXML private Text email_error; // Text element to display email validation errors
    @FXML private TextField first_name_field; // Input field for the user's first name
    @FXML private Text first_name_error; // Text element to display first name validation errors
    @FXML private TextField last_name_field; // Input field for the user's last name
    @FXML private Text last_name_error; // Text element to display last name validation errors
    @FXML private TextField middle_name_field; // Input field for the user's middle name
    @FXML private Text middle_name_error; // Text element to display middle name validation errors
    @FXML private PasswordField pref_name_field; // Input field for the user's preferred name
    @FXML private Text pref_name_error; // Text element to display preferred name validation errors

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        // Disable all error labels on initialization to prevent display of errors
        Utils.disableNode(email_error);
        Utils.disableNode(first_name_error);
        Utils.disableNode(middle_name_error);
        Utils.disableNode(last_name_error);
        Utils.disableNode(pref_name_error);
    }

    @FXML
    void finish(ActionEvent event) throws IOException, SQLException {
        // Flag to determine if a preferred name is provided by the user
        boolean choose_pref_name = false;

        // Retrieve user input from the text fields
        String email = email_field.getText();
        String first_name = first_name_field.getText();
        String middle_name = middle_name_field.getText();
        String last_name = last_name_field.getText();
        String pref_name = pref_name_field.getText();
        
        // Validate the input fields
        String validate_email = Utils.validateEmail(email);
        String validate_first_name = Utils.validateName(first_name);
        String validate_middle_name = Utils.validateName(middle_name);
        String validate_last_name = Utils.validateName(last_name);
        String validate_pref_name = ""; // Initialize preferred name validation string
        
        // Check if the preferred name field is not empty
        if (!pref_name_field.getText().equals("")) {
            choose_pref_name = true; // Set flag to true if preferred name is provided
            pref_name = pref_name_field.getText();
            validate_pref_name = Utils.validateName(pref_name); // Validate the preferred name
        }

        // Validate each input field and store results
        boolean valid_email = Utils.isValid(email_error, validate_email);
        boolean valid_first = Utils.isValid(first_name_error, validate_first_name);
        boolean valid_middle = Utils.isValid(middle_name_error, validate_middle_name);
        boolean valid_last = Utils.isValid(last_name_error, validate_last_name);
        boolean valid_pref_name = true; // Assume preferred name is valid initially
        
        // Validate preferred name only if it was provided
        if (choose_pref_name) {
            valid_pref_name = Utils.isValid(pref_name_error, validate_pref_name);
        }

        // Check if all validations passed
        if (valid_email && valid_first && valid_middle && valid_last && 
            ((choose_pref_name && valid_pref_name) || !choose_pref_name)) {
            // If valid, finalize account setup in the database
            App.databaseHelper.finishAccountSetup(current_user.getUsername(), first_name, middle_name, last_name, pref_name, email);
            
            // Navigate to role selection or dashboard based on user's roles
            if (App.getAppUser().hasMultipleRoles()) {
                App.setRoot("select_role"); // Redirect to role selection if user has multiple roles
            } else {
                current_user.setLoginRole(current_user.getRole()); // Set user's login role
                App.setRoot("dashboard"); // Redirect to dashboard
            }
        }
    }
}
