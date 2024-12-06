package asu.cse360project.DashboardControllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import asu.cse360project.Singleton;
import asu.cse360project.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;


/**
 * Controller class for managing articles in the system.
 * Provides functionality to add, remove, view, and update articles.
 */

/**
 * <p> MessageController. </p>
 * 
 * <p> Description: A class that is responsible for handling user actions on the message scene. </p>
 * 
 * <p> Copyright: Tu35 © 2024 </p>
 * 
 * @author Tu35
 * @version 1.00	2024-10-30 JavaFX controller class for message scene
 * 
 * 
 */

public class MessageController implements Initializable {

	final ToggleGroup group = new ToggleGroup();
	@FXML private RadioButton gen_btn, spc_btn;
	@FXML private TextField message_box;
	@FXML private Label top_label; // Label to display user information at the top
	Singleton data;
    
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		data = Singleton.getInstance();
		gen_btn.setToggleGroup(group);
		gen_btn.setSelected(true);
		spc_btn.setToggleGroup(group);
	}
	
	
	@FXML
    void sendToDB(ActionEvent event) throws IOException {
		
        boolean gen = gen_btn.selectedProperty().getValue();
        //boolean spc = spc_btn.selectedProperty().getValue();
        
        String message_value = message_box.getText().toString();
        System.out.println(message_value);
		String type;
		
		
        if(gen) {
        	type = "General";
			assert message_value != null;
        	System.out.println("General Message");

        }
        else {
        	type = "Specific";
			assert message_value != null;
			System.out.println("Specific Message");
        }
        
        User user = data.getAppUser();
		if(!message_value.isEmpty())
		{     
			data.message_db.newMsg(message_value, user.getUsername(), type, user.getId());
		}

	}
	
}
