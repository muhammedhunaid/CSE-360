package asu.cse360project;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

//
public class DashboardController extends SceneController implements Initializable{
    User user = App.user;

    @FXML
    private StackPane contentArea;

    @FXML
    private Label top_label;

    @FXML
    private VBox admin_controlls;

    
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        // Set the label with the user's role and username
        //Utils.setLabel(top_label, user.getLoginRole() + ": " + user.getUsername(), Color.BLACK);

        // Query the database to determine the user's role
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT role_admin, role_student, role_instructor FROM users WHERE username = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, user.getUsername());
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                boolean isAdmin = rs.getBoolean("role_admin");

                // Disable admin controls if the user is not an admin
                if (!isAdmin) {
                    Utils.disableNode(admin_controlls);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Method to manage users
    @FXML
    void manageUsers(ActionEvent event) throws IOException {
        setContentArea("manage_users");
    }

    //Method to logout
    @FXML
    void logout(ActionEvent event) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirm");
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Please confirm your action");
        alert.setContentText("Are you sure you want logout");
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    App.setRoot("login");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }
    //
    private void setContentArea(String fxml) throws IOException {
        Parent fxmlLoader = FXMLLoader.load(DashboardController.class.getResource("DashboardScenes/" + fxml + ".fxml"));
		contentArea.getChildren().removeAll();
		contentArea.getChildren().setAll(fxmlLoader);
    }
}


