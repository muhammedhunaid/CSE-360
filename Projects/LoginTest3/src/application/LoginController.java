package application;

import javafx.fxml.FXML;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class LoginController {
	@FXML
	private Label topLabel;
	
    @FXML
    private Label InviteError;
    
    @FXML
    private TextField InviteField;
    
    private boolean InviteText = false;
    
	// Event Listener on Button.onAction
	@FXML
	public void LogIn(ActionEvent event) {
		topLabel.setText("bruh");
	}
	// Event Listener on Button.onAction
	@FXML
	public void Join(ActionEvent event) throws IOException
	{

		if(InviteField.getText().matches("Hello"))
		{
			if(InviteText)
			{
				InviteError.setManaged(false);
				InviteError.setVisible(false);
				InviteText = false;
			}
			Utills.SwitchScene(event, "AccountSetup.fxml");

		}else {
			InviteError.setManaged(true);
			InviteError.setVisible(true);
			InviteText = true;
		}
		
	}
}
