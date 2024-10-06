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

public class SelectRoleController implements Initializable {

    @FXML
    private RadioButton admin_button;

    @FXML
    private RadioButton instructor_button;

    @FXML
    private RadioButton student_button;

    @FXML
    private Text error_text;

    boolean admin = true;
    boolean instructor = true;
    boolean student = true;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        if(!App.user.isAdmin())
        {
            Utils.disableNode(admin_button);
            admin = false;
        }

        if(!App.user.isInstructor())
        {
            Utils.disableNode(instructor_button);
            instructor = false;
        }

        if(!App.user.isStudent())
        {
            Utils.disableNode(student_button);
            student = false;
        }

        Utils.disableNode(error_text);
    }

    @FXML
    void select(ActionEvent event) throws IOException {
        boolean check_admin = false;
        boolean check_instructor = false;
        boolean check_student = false;
        App.user.resetLoginRole();
        
        if(admin)
        {
            check_admin = admin_button.selectedProperty().getValue();
            if(check_admin)
            {
                App.user.setLoginRole("admin");
            }
        }

        if(instructor)
        {
            check_instructor = instructor_button.selectedProperty().getValue();
            if(check_instructor)
            {
                App.user.setLoginRole("instructor");
            }
        }

        if(student)
        {
            check_student = student_button.selectedProperty().getValue();
            if(check_student)
            {
                App.user.setLoginRole("student");
            }
        }

        System.out.println(App.user.getLoginRole());
        if(App.user.getLoginRole() == "")
        {
            Utils.setText(error_text, "No Role Selected", Color.RED);
            return;
        }

        if((check_admin ? 1 : 0) + (check_instructor ? 1 : 0) + (check_student ? 1 : 0) > 1)
        {
            Utils.setText(error_text, "Multiple Roles Selected", Color.RED);
            return;
        }

        App.setRoot("dashboard");
    }
}
