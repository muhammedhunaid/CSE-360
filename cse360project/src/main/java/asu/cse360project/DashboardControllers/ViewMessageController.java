package asu.cse360project.DashboardControllers;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.ResourceBundle;

import asu.cse360project.Article;
import asu.cse360project.Message;
import asu.cse360project.Singleton;
import asu.cse360project.User;
import asu.cse360project.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import asu.cse360project.DatabaseHelpers.MessageHelper;

/**
 * Controller class for managing messages in the system.
 * Provides functionality to view messages
 */
//public class ViewMessageController implements Initializable {
//
//    @FXML private TableView<String[]> table;
//	
//	@Override
//	public void initialize(URL location, ResourceBundle resources) {
//		Singleton data = Singleton.getInstance();
//		ObservableList<String[]> messages = null;
//		try {
//			messages = data.message_db.getAllMsg();
//			
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		for(String[] i : messages)
//			System.out.println(i.toString());
//		table.setItems(messages);
//		
//		
//	}
	
public class ViewMessageController implements Initializable {

    @FXML private TableView<Message> table;
    @FXML private TableColumn<Message, String> username_col;
    @FXML private TableColumn<Message, String> type_col;
    @FXML private TableColumn<Message, String> text_col;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Singleton data = Singleton.getInstance();
        
        // Bind table columns to Message properties
        username_col.setCellValueFactory(new PropertyValueFactory<>("username"));
        type_col.setCellValueFactory(new PropertyValueFactory<>("type"));
        text_col.setCellValueFactory(new PropertyValueFactory<>("text"));

        ObservableList<Message> messages = FXCollections.observableArrayList();

        try {
            for (String[] i : data.message_db.getAllMsg()) {
            	System.out.println(i[0] + i[1] + i[2]);
                messages.add(new Message(i[0], i[1], i[2])); // Assuming i[1] = username, i[2] = type, i[3] = text
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        for (Message row : messages) {
            System.out.println("Row data: " + row.toString());
        }

        table.setItems(messages);
    }
}

	
	
	
	

