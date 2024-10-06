package phase1.gui.DashboardControllers;

import java.security.SecureRandom;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import phase1.gui.Utils;

public class AddController {

    @FXML
    private RadioButton admin_button;

    @FXML
    private Text code_label;

    @FXML
    private RadioButton instructor_button;

    @FXML
    private RadioButton student_button;

    @FXML
    void add(ActionEvent event) {
        boolean admin = admin_button.selectedProperty().getValue();
        boolean instructur = instructor_button.selectedProperty().getValue();
        boolean student = student_button.selectedProperty().getValue();    

        String invite_code = Utils.generateInviteCode(10);

        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirm");
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Please confirm your action");
        alert.setContentText("Are you sure you want to create a new user");
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                Utils.setText(code_label, "Invite Code: " + invite_code, Color.BLACK);
                //create new db entry
                //add invite code to entry
            } else {
                Utils.setText(code_label, "No User Created", Color.BLACK);
            }
        });
    }

}


