package phase1.gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class LoginController implements Initializable{

    @FXML
    private TextField invite_code_textfield;

    @FXML
    private TextField password_textfield;

    @FXML
    private TextField username_textfield;

    @FXML
    private Text login_error;

    
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        Utils.disableNode(login_error);
    }

    @FXML
    private void login(ActionEvent event) throws IOException {
        String username = username_textfield.getText().toString();
        String password = password_textfield.getText().toString();

        boolean one_time_flag = true;
        boolean username_in_db = false;
        boolean pw_in_db = false;
        boolean email_in_db = true;

        //query using db///
        //check uname
        if(username_in_db) 
        {
            if(one_time_flag)
            {
                App.setRoot("new_pw");
            }
            
            if(pw_in_db && !email_in_db)
            {
                App.setRoot("setup_account");
            }

            App.setRoot("dashboard");
        }

        Utils.setText(login_error, "Username or Password not found", Color.RED);
    }

    @FXML
    private void join(ActionEvent event) throws IOException {
        String invite_code = invite_code_textfield.getText().toString();

        //query using db
        //check for invited code
        boolean invite_code_in_db = true;
        if(invite_code_in_db)
        {
            App.setRoot("create_account");
        }

        Utils.setText(login_error, "Invite Code not found", Color.RED);
    }
}
