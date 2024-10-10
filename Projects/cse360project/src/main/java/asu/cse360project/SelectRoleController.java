package asu.cse360project;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.RadioButton;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SelectRoleController implements Initializable {

    @FXML
    private RadioButton admin_button;

    @FXML
    private RadioButton instructor_button;

    @FXML
    private RadioButton student_button;

    @FXML
    private Text error_text;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        if (!App.user.isAdmin()) {
            Utils.disableNode(admin_button);
        }

        if (!App.user.isInstructor()) {
            Utils.disableNode(instructor_button);
        }

        if (!App.user.isStudent()) {
            Utils.disableNode(student_button);
        }

        Utils.disableNode(error_text);
    }

    @FXML
    //
    void select(ActionEvent event) throws IOException {
        App.user.resetLoginRole();

        if (admin_button.isSelected()) {
            App.user.setLoginRole("admin");
        } else if (instructor_button.isSelected()) {
            App.user.setLoginRole("instructor");
        } else if (student_button.isSelected()) {
            App.user.setLoginRole("student");
        }

        if (App.user.getLoginRole().isEmpty()) {
            Utils.setText(error_text, "No Role Selected", Color.RED);
            return;
        }

        App.setRoot("dashboard");
    }
}