package asu.cse360project.LoginControllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import asu.cse360project.Singleton;
import asu.cse360project.User;
import asu.cse360project.Utils;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.RadioButton;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;

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
        boolean check_admin = false;
        boolean check_instructor = false;
        boolean check_student = false;
        currUser.resetLoginRole();
        
        if (admin) {
            check_admin = admin_button.selectedProperty().getValue();
            if (check_admin) {
                currUser.setLoginRole("admin");
            }
        }

        if (instructor) {
            check_instructor = instructor_button.selectedProperty().getValue();
            if (check_instructor) {
                currUser.setLoginRole("instructor");
            }
        }

        if (student) {
            check_student = student_button.selectedProperty().getValue();
            if (check_student) {
                currUser.setLoginRole("student");
            }
        }
        
        if (data.getAppUser().getLoginRole().isEmpty()) {
            Utils.showErrorFeedback(error_text, "Please select a role");
            return;
        }

        if ((check_admin ? 1 : 0) + (check_instructor ? 1 : 0) + (check_student ? 1 : 0) > 1) {
            Utils.showErrorFeedback(error_text, "Please select only one role");
            return;
        }

        Utils.showSuccessFeedback(error_text, "Role selected successfully!");
        PauseTransition pause = new PauseTransition(Duration.seconds(1));
        pause.setOnFinished(e -> {
            try {
                Utils.setRoot("DashboardScenes/dashboard");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        pause.play();
    }
}
