package asu.cse360project.LoginControllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import asu.cse360project.Singleton;
import asu.cse360project.User;
import asu.cse360project.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.RadioButton;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

// Controller class for allowing logged-in users to select a role for signing in
public class SelectRoleController implements Initializable {

    Singleton data = Singleton.getInstance();
    // Current user retrieved from the application context
    User currUser = data.getAppUser();

    // FXML elements for the role selection buttons and error message display
    @FXML private RadioButton admin_button; // Radio button for selecting the Admin role
    @FXML private RadioButton instructor_button; // Radio button for selecting the Instructor role
    @FXML private RadioButton student_button; // Radio button for selecting the Student role
    @FXML private Text error_text; // Text element to display error messages

    // Flags to track if each role selection is available
    boolean admin = true; 
    boolean instructor = true; 
    boolean student = true; 

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        // Disable the role selection buttons based on the current user's roles
        if (!currUser.isAdmin()) {
            Utils.disableNode(admin_button); // Disable admin button if user is already an admin
            admin = false; // Mark admin role as unavailable
        }

        if (!currUser.isInstructor()) {
            Utils.disableNode(instructor_button); // Disable instructor button if user is already an instructor
            instructor = false; // Mark instructor role as unavailable
        }

        if (!currUser.isStudent()) {
            Utils.disableNode(student_button); // Disable student button if user is already a student
            student = false; // Mark student role as unavailable
        }

        // Disable the error text label on initialization
        Utils.disableNode(error_text);
    }

    @FXML
    void select(ActionEvent event) throws IOException {
        // Initialize flags to track selected roles
        boolean check_admin = false;
        boolean check_instructor = false;
        boolean check_student = false;
        currUser.resetLoginRole(); // Reset the user's login role before selecting a new one
        
        // Check if the admin role is selectable and update login role if selected
        if (admin) {
            check_admin = admin_button.selectedProperty().getValue(); // Get the selected state of the admin button
            if (check_admin) {
                currUser.setLoginRole("admin"); // Set the user's login role to admin
            }
        }

        // Check if the instructor role is selectable and update login role if selected
        if (instructor) {
            check_instructor = instructor_button.selectedProperty().getValue(); // Get the selected state of the instructor button
            if (check_instructor) {
                currUser.setLoginRole("instructor"); // Set the user's login role to instructor
            }
        }

        // Check if the student role is selectable and update login role if selected
        if (student) {
            check_student = student_button.selectedProperty().getValue(); // Get the selected state of the student button
            if (check_student) {
                currUser.setLoginRole("student"); // Set the user's login role to student
            }
        }
        
        // Check if no role was selected
        if (data.getAppUser().getLoginRole() == "") {
            Utils.setText(error_text, "No Role Selected", Color.RED); // Display error message for no role selection
            return; // Exit the method if no role is selected
        }

        // Check if multiple roles were selected
        if ((check_admin ? 1 : 0) + (check_instructor ? 1 : 0) + (check_student ? 1 : 0) > 1) {
            Utils.setText(error_text, "Multiple Roles Selected", Color.RED); // Display error message for multiple selections
            return; // Exit the method if multiple roles are selected
        }

        // Navigate to the dashboard if a single role was successfully selected
        Utils.setRoot("DashboardScenes/dashboard");
    }
}
