package asu.cse360project.DashboardControllers;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import asu.cse360project.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;


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

    Singleton data;
    final ToggleGroup group = new ToggleGroup();
    Message selectedMessage = null;

    @FXML private RadioButton general_btn;
    @FXML private RadioButton specific_btn;

    @FXML private TableView<Message> table;
    @FXML private TableColumn<Message, String> username_col;
    @FXML private TableColumn<Message, String> type_col;
    @FXML private TableColumn<Message, String> text_col;
    String type = "General";

    //User searches table
    @FXML private ListView<String> user_searches_table;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        data = Singleton.getInstance();

        general_btn.setToggleGroup(group);
		general_btn.setSelected(true);
		specific_btn.setToggleGroup(group);
        
        // Bind table columns to Message properties
        username_col.setCellValueFactory(new PropertyValueFactory<>("username"));
        type_col.setCellValueFactory(new PropertyValueFactory<>("type"));
        text_col.setCellValueFactory(new PropertyValueFactory<>("text"));

        try {
            setTable(type);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
            	selectedMessage = newSelection;
                try {
                    setUserSearchesTable();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }            
    
    public void setTable(String type) throws SQLException {
        table.setItems(data.message_db.getAllMsg(type));
    }

    @FXML //display general messages
    void General(ActionEvent event) throws SQLException {
        setTable("General");
    }

    @FXML //display specific messages
    void Specific(ActionEvent event) throws SQLException {
        setTable("Specific");
    }
                
    private void setUserSearchesTable() throws SQLException {
        ObservableList<String> searches = FXCollections.observableList(data.message_db.getUserSearches(selectedMessage.getUser_id()));
        user_searches_table.setItems(searches);
    }
}

	
	
	
	

