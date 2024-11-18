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
public class MessageController implements Initializable {

	final ToggleGroup group = new ToggleGroup();
	@FXML private RadioButton gen_btn, spc_btn;
	@FXML private TextField message_box;
	@FXML private Label top_label; // Label to display user information at the top
    
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
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
        	System.out.println("General Message");
        }
        else {
        	type = "Specific";
			System.out.println("Specific Message");
        }
        
        Singleton data = Singleton.getInstance();
        User user = data.getAppUser();
        
        data.message_db.newMsg(message_value, user.getUsername(), type, user.getId());
	}
	
}
