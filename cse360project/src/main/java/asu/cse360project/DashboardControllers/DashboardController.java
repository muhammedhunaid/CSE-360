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
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class DashboardController implements Initializable {
    // Retrieve the current user from the application context
    Singleton data = Singleton.getInstance();
    User user = data.getAppUser();
    boolean editing_article = false;

    @FXML private StackPane contentArea; // Area to display the content of different scenes
    @FXML private Label top_label; // Label to display user information at the top
    @FXML private Button send_message_btn;
    @FXML private Button view_message_btn;
    @FXML private Button manage_users_btn;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        // Set the top label with the user's role and username
        Utils.setLabel(top_label, user.getLoginRole() + ": " + user.getUsername(), Color.BLACK);
        
        if (user.getLoginRole().equals("student")) {
            Utils.disableNode(view_message_btn);
            Utils.disableNode(manage_users_btn);
        }else{
            Utils.disableNode(send_message_btn);
        }
        
        data.content_area = contentArea;

        addTooltips();

    }

    void addTooltips() {
        // Create tooltips
        Tooltip sendTooltip = new Tooltip("Click to send a message to your instructor or peers.");
        Tooltip viewTooltip = new Tooltip("Click to view messages you have received.");
        Tooltip manageTooltip = new Tooltip("Click to manage users in the system (Admin only).");

        // Attach tooltips to buttons
        send_message_btn.setTooltip(sendTooltip);
        view_message_btn.setTooltip(viewTooltip);
        manage_users_btn.setTooltip(manageTooltip);
    }


    @FXML
    void manageUsers(ActionEvent event) throws IOException {
        // Load the manage users scene when the corresponding action is triggered
        Utils.setContentArea(data.content_area,"manage_users");
    }

    @FXML
    void viewArticles(ActionEvent event) throws IOException {
        // Load the manage users scene when the corresponding action is triggered
        Utils.setContentArea(data.content_area,"search_articles");
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
    
    /**
     * Handles the send message action.
     * @param event The action event.
     * @throws IOException If an I/O exception occurs.
     */
    @FXML
    void sendMessage(ActionEvent event) throws IOException {
    	Utils.setContentArea(data.content_area, "messaging");
    }
    
    
    /**
     * Handles the view message action.
     * @param event The action event.
     * @throws IOException If an I/O exception occurs.
     */
    @FXML
    void viewMessages(ActionEvent event) throws IOException {
    	Utils.setContentArea(data.content_area, "manage_msg");
    }
}


