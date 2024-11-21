package asu.cse360project.DashboardControllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import asu.cse360project.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

/**
 * <p> DashboardController. </p>
 * 
 * <p> Description: A class that is responsible for handling user actions on the dashboard scene. </p>
 * 
 * <p> Copyright: Tu35 © 2024 </p>
 * 
 * @author Tu35
 * 
 * @version 1.00	2024-10-30 JavaFX controller class for admin dashboard scene
 * @version 2.00	2024-11-01 Added Articles and user dashboard
 * @version 3.00	2024-10-30 Added messaging buttons
 * 
 *  */

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


