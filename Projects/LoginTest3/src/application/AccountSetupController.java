package application;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class AccountSetupController {
	
    @FXML
    void GoBack(ActionEvent event) throws IOException {
    	Utills.SwitchScene(event, "Login.fxml");
    }
}
