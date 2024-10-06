package asu.cse360project;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class SetupAccountController implements Initializable {

    @FXML
    private TextField email;

    @FXML
    private Text email_error;

    @FXML
    private TextField first_name;

    @FXML
    private Text first_name_error;

    @FXML
    private TextField last_name;

    @FXML
    private Text last_name_error;

    @FXML
    private TextField middle_name;

    @FXML
    private Text middle_name_error;

    @FXML
    private PasswordField pref_name;

    @FXML
    private Text pref_name_error;

    
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        Utils.disableNode(email_error);
        Utils.disableNode(first_name_error);
        Utils.disableNode(middle_name_error);
        Utils.disableNode(last_name_error);
        Utils.disableNode(pref_name_error);
    }

    @FXML
    void finish(ActionEvent event) throws IOException {
        boolean choose_pref_name = false;
        String validate_email = Utils.validateEmail(email.getText());
        String validate_first_name = Utils.validateName(first_name.getText());
        String validate_middle_name = Utils.validateName(middle_name.getText());
        String validate_last_name = Utils.validateName(last_name.getText());
        String validate_pref_name = "";
        if(!pref_name.getText().equals(""))
        {
            choose_pref_name = true;
            validate_pref_name= Utils.validateName(pref_name.getText());
        }

        boolean valid_email = Utils.isValid(email_error, validate_email);
        boolean valid_first = Utils.isValid(first_name_error, validate_first_name);
        boolean valid_middle = Utils.isValid(middle_name_error, validate_middle_name);
        boolean valid_last = Utils.isValid(last_name_error, validate_last_name);
        boolean valid_pref_name = true;
        if(choose_pref_name)
        {
            valid_pref_name = Utils.isValid(pref_name_error, validate_pref_name);
        }


        if(valid_email && valid_first && valid_middle && valid_last && ((choose_pref_name && valid_pref_name) || !choose_pref_name))
        {
            //add info in db
            if(choose_pref_name)
            {
                //add prefferred name in db
            }
            
            if(App.user.hadMultipleRoles())
            {
                App.setRoot("select_role");
            }else{
                App.setRoot("dashboard");
            }

        }
    }
}
