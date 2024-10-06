package asu.cse360project;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class NewPwController implements Initializable {

    @FXML
    private Text password_error;

    @FXML
    private TextField password;

    @FXML
    private Text re_password_error;

    @FXML
    private PasswordField re_password;

    
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        Utils.disableNode(password_error);
        Utils.disableNode(re_password_error);
    }

    @FXML
    void Finish(ActionEvent event) {
        String validate_pw = Utils.validatePassword(password.getText());

        boolean valid_pw = Utils.isValid(password_error, validate_pw);
        boolean same = false;

        if(password.getText().compareTo(re_password.getText()) == 0)
        {
            Utils.disableNode(re_password_error);
            same = true;
        }else{
            Utils.setText(re_password_error, "Passwords dont match.", Color.RED);
            same = false;
        }

        if(valid_pw && same)
        {
            //add new pw into db
        }

    }



}
