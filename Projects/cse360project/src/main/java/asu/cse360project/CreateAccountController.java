package asu.cse360project;

import java.net.URL;
import java.util.ResourceBundle;
import java.io.IOException;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;


public class CreateAccountController extends SceneController implements Initializable{

    @FXML
    private TextField password;

    @FXML
    private Text password_error;

    @FXML
    private PasswordField re_password;

    @FXML
    private Label re_password_error;

    @FXML
    private TextField username;

    @FXML
    private Text username_error;

    @FXML
    private Label title;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        //remove error text from scene
        Utils.disableNode(password_error);
        Utils.disableNode(re_password_error);
        Utils.disableNode(username_error);
    }

    @FXML
    void join(ActionEvent event) throws IOException, InterruptedException{
        //get input from text fields and validate
        String validate_pw = Utils.validatePassword(password.getText());
        String validate_uname = Utils.validateUsername(username.getText());

        //set true if password and username valid otherwise display error
        boolean valid_pw = Utils.isValid(password_error, validate_pw);
        boolean valid_uname = Utils.isValid(username_error, validate_uname);
        boolean same = false;

        //check if password field inputs are the same
        if(password.getText().compareTo(re_password.getText()) == 0)
        {
            same = true;
        }else{
            Utils.setLabel(re_password_error, "Passwords dont match.", Color.RED);
            same = false;
        }

        if(valid_pw && valid_uname && same)
        {
            //add user to database
            Utils.setLabel(title, "Account Created", Color.GREEN);
            
            //temporarily display "Account created before changing to login screen"
            PauseTransition pause = new PauseTransition();
            pause.setDuration(Duration.seconds(2));
            pause.setOnFinished(ActionEvent -> {
                try {
                    App.setRoot("login");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            pause.play();

            
        }
    }
}