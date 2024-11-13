package asu.cse360project.DashboardControllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import asu.cse360project.Singleton;
import asu.cse360project.User;
import asu.cse360project.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class DashboardController implements Initializable {
    // Retrieve the current user from the application context
    Singleton data = Singleton.getInstance();
    User user = data.getAppUser();
    boolean editing_article = false;

    @FXML private StackPane contentArea; // Area to display the content of different scenes
    @FXML private Label top_label; // Label to display user information at the top
    @FXML private VBox admin_controlls; // VBox to contain admin-specific controls

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        // Set the top label with the user's role and username
        Utils.setLabel(top_label, user.getLoginRole() + ": " + user.getUsername(), Color.BLACK);
        
        // Disable admin controls if the logged-in user is not an admin
        if (!user.getLoginRole().equals("admin")) {
            Utils.disableNode(admin_controlls);
        }
        
        data.content_area = contentArea;
    }

    @FXML
    void manageUsers(ActionEvent event) throws IOException {
        // Load the manage users scene when the corresponding action is triggered
        Utils.setContentArea("manage_users");
    }

    @FXML
    void manageArticles(ActionEvent event) throws IOException {
        // Load the manage users scene when the corresponding action is triggered
        Utils.setContentArea("group_dashboard");
    }

    @FXML
    void modifySAG(ActionEvent event) throws IOException {
        // Load the manage users scene when the corresponding action is triggered
        Utils.setContentArea("modify_SAG");
    }

    @FXML
    void logout(ActionEvent event) {
        // Create a confirmation dialog for logging out
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirm");
        alert.setHeaderText("Please confirm your action");
        alert.setContentText("Are you sure you want to logout");
        
        // Show the dialog and wait for user response
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // If the user confirms, reset the app user and load the login scene
                try {
                    data.setAppUser(null); // Clear the current user
                    Utils.setRoot("LoginScenes/login"); // Load the login scene
                } catch (IOException e) {
                    e.printStackTrace(); // Print stack trace in case of an error
                }
            }
        });
    }
}


