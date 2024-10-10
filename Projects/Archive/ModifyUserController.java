package asu.cse360project.DashboardControllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import asu.cse360project.User;
import asu.cse360project.Utils;

public class ModifyUserController implements Initializable{

    User user;
    boolean user_found = false;
    private Alert alert;

    @FXML
    private RadioButton admin_btn;

    @FXML
    private VBox change_role_box;

    @FXML
    private Button change_role_btn;

    @FXML
    private Button delete_btn;

    @FXML
    private Text display_text;

    @FXML
    private RadioButton instructor_btn;

    @FXML
    private Button reset_pw_btn;

    @FXML
    private Button search_button;

    @FXML
    private TextField search_user;

    @FXML
    private RadioButton student_btn;

    @FXML
    private Text user_label;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        Utils.disableNode(user_label);
        Utils.disableNode(display_text);
        Utils.disableNode(change_role_box);

        alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirm");
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Please confirm your action");
    }

    @FXML
    void changeRole(ActionEvent event) {
        if(user_found)
        {
            Utils.enableNode(change_role_box); 
        }
    }

    @FXML
    void confirm_role(ActionEvent event) {
        boolean admin = admin_btn.selectedProperty().getValue();
        boolean instructer = instructor_btn.selectedProperty().getValue();
        boolean student = student_btn.selectedProperty().getValue();

        if(!admin && !instructer && !student)
        {
            Utils.setText(display_text, "No Role Selected", Color.BLACK);
        }else{
            alert.setContentText("Are you sure you want to change a users role");
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                        Utils.setText(display_text, "User Roles Changed", Color.BLACK);
                        //create new db entry
                        //add invite code to entry
                } else {
                    Utils.setText(display_text, "No User Roles Changed", Color.BLACK);
                }
            });
        }

        Utils.disableNode(change_role_box);
    }

    @FXML
    void delete(ActionEvent event) {
        if(user_found)
        {
            alert.setContentText("Are you sure you want to delete user");
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    //remove user from db
                    Utils.setText(display_text, "User Removed", Color.BLACK);
                } else {
                    Utils.setText(display_text, "User not Removed", Color.BLACK);
                }
            });    
        }
    }

    @FXML
    void resetPassword(ActionEvent event) {
        if(user_found)
        {
            String one_time_pw = Utils.generateInviteCode(10);
            alert.setContentText("Are you sure you want to reset user");
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    //but one_time_pw in pw and set flag true;
                    Utils.setText(display_text, "One time Password: " + one_time_pw, Color.BLACK);
                } else {
                    Utils.setText(display_text, "No User reset", Color.BLACK);
                }
            });    
        }
    }

    @FXML
    void searchUser(ActionEvent event) {
        String user = search_user.getText().toString();

        boolean user_in_db = true;
        //check if in db

        if(user_in_db)
        {
            user_found = true;
            Utils.setText(user_label, "Modifying User: " + user, Color.BLACK);
        }else{
            Utils.setText(user_label, "User Does Not Exsit", Color.BLACK);
        }
    }

}
