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
        Utils.setLabel(top_label, user.getLoginRole() + ": " + user.getUsername(), Color.BLACK);

        if(user.getLoginRole() != "admin")
        {
            Utils.disableNode(admin_controlls);
        }
        /*
        try {
            setContentArea("manage_user");
        } catch (IOException e) {
            e.printStackTrace();
        }
            */
    }

    @FXML
    void manageUsers(ActionEvent event) throws IOException {
        setContentArea("manage_users");
    }

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

    private void setContentArea(String fxml) throws IOException {
        Parent fxmlLoader = FXMLLoader.load(DashboardController.class.getResource("DashboardScenes/" + fxml + ".fxml"));
		contentArea.getChildren().removeAll();
		contentArea.getChildren().setAll(fxmlLoader);
    }
}


