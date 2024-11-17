package asu.cse360project.DashboardControllers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

import asu.cse360project.Group;
import asu.cse360project.Singleton;
import asu.cse360project.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.text.Text;

/**
 * Controller class for managing users in the system.
 * Provides functionality to add, remove, search, change roles, and reset passwords for users.
 */
public class GroupController implements Initializable {

    Singleton data = Singleton.getInstance();
    Alert alert;
    ObservableList<Group> all_Groups; // List to hold all group names
    ObservableList<String> all_backups; // List to hold all group names
    ArrayList<Integer> selected_groups = new ArrayList<>();

    ArrayList<Group> a = new ArrayList<>();
    
    private Group selectedGroup = null;
    private String selectedBackup = null;
	
    // FXML elements for the UI
    @FXML private TableColumn<Group, String> groupname_col;
    @FXML private TableView<Group> table;
    @FXML private ListView<String> backups_list;

    @FXML private Button search_button;
    @FXML private TextField search_group;
    @FXML private TableColumn<Group, String> article_ids_col;
    @FXML private Text groups_selected_txt;

    // Initialization method to set up the UI and load user data
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {;

        // Initialize alert for confirmation dialogs
        alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirm");
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Please confirm your action");

        // Set up table columns to display user information
        groupname_col.setCellValueFactory(new PropertyValueFactory<>("name"));
        article_ids_col.setCellValueFactory(new PropertyValueFactory<>("id"));

        // Load all users from the database
       setGroupTable();
       setBackupsTable();

        table.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                if (selectedGroup != null) {
                    try {
                        selected_groups.clear();
                        selected_groups.add(selectedGroup.getId());
                        view(new ActionEvent());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println("Double-clicked on: ");
                }
            }
        });

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
            	selectedGroup = newSelection;
                //Utils.disableNode(error_label);
            }
        });

        backups_list.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
            	selectedBackup = newSelection;
                //Utils.disableNode(error_label);
            }
        });
    }

    private void setBackupsTable() {
        String list = data.user_db.getUserBackups(data.getAppUser().getUsername());
        if(list != "") {
            String[] backups_array  = list.split(",");
            all_backups = FXCollections.observableArrayList(backups_array);
            backups_list.setItems(all_backups);
        }
    }

    private void setGroupTable()
    {
        try {
        	all_Groups = data.group_articles_db.getAllGroups();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        table.setItems(all_Groups);
    }
    
    // Method to search for a user in the table by username
    @FXML
    void searchGroup(ActionEvent event) throws SQLException {
        String grpname = search_group.getText().toString();
        for (Group grp : all_Groups) {
            if (grp.getName().equals(grpname)) {
                table.getSelectionModel().select(grp); // Highlight the row if user is found
                table.scrollTo(grp); // Scroll to the row
                //Utils.setLabel(error_label, "Group Found", Color.GREEN); // Show success message
                return;
            } else {
                //Utils.setLabel(error_label, "Group not Found", Color.RED); // Show error if user is not found
            }
        }
    }
    
    @FXML
    void delete(ActionEvent event) throws SQLException {
    	if(selectedGroup != null && selectedGroup.getId() > 0)
        {
            data.group_articles_db.deleteGroup(selectedGroup.getId());
            all_Groups.remove(all_Groups.indexOf(selectedGroup));
        }
    }

    @FXML
    void delete_backup(ActionEvent event) throws SQLException {
    	if(selectedBackup != null)
        {
            data.user_db.deleteBackupFile(data.getAppUser().getUsername(), selectedBackup);
            setBackupsTable();
            removeFile(selectedBackup);
        }
    }

    private void removeFile(String file_name) {
        String filePath = "Backups/" + file_name; // Update this to the file you want to delete
        File file = new File(filePath);

        // Check if the file exists and delete it
        if (file.exists()) {
            if (file.delete()) {
                System.out.println("File deleted successfully: " + filePath);
            } else {
                System.out.println("Failed to delete the file: " + filePath);
            }
        } else {
            System.out.println("File not found: " + filePath);
        }
    }

    @FXML
    void backup(ActionEvent event) {
        if(selected_groups.size() > 0)
        {
            // Create a TextInputDialog
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Backup File Input");
            dialog.setHeaderText("Enter the Backup File Name");
            dialog.setContentText("File Name:");

            // Show the dialog and wait for the response
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(fileName -> {
                try {
                    if(all_backups == null || !all_backups.contains(fileName))
                    {
                        data.group_articles_db.backup(selected_groups, fileName);
                        data.user_db.updateBackupFiles(data.getAppUser().getUsername(), fileName);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
        }
        setBackupsTable();
    }

    @FXML
    void restore(ActionEvent event) throws SQLException {
        if(selectedBackup != null)
        {
            alert.setContentText("Are you sure you want to Restore backup");
            alert.showAndWait().ifPresent(response -> {
                try {
                    data.group_articles_db.restore(selectedBackup);
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (Exception EncryptionError){
                    EncryptionError.printStackTrace();
                }
                setGroupTable();                   
            });
            
        }
    }

    @FXML
    void merge(ActionEvent event) throws SQLException {
        if(selectedBackup != null)
        {
            alert.setContentText("Are you sure you want to Merge and Restore backup");
            alert.showAndWait().ifPresent(response -> {
                try {
                    data.group_articles_db.restoreMerge(selectedBackup);
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (Exception EncryptionError){
                    EncryptionError.printStackTrace();
                }
                setGroupTable();                   
            });
            
        }
    }

    @FXML
    void view(ActionEvent event) throws IOException {
        if(selectedGroup != null)
        {
            data.edit_group = selected_groups;
            Utils.setContentArea(data.content_area,"manage_articles");
        } 
    }
    
    @FXML
    void addGroup(ActionEvent event) throws SQLException{
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Group");
        dialog.setHeaderText("Enter the name of the new group");
        dialog.setContentText("Group Name:");

        Optional<String> result = dialog.showAndWait();

        result.ifPresent(groupName -> {
            if (!groupName.isBlank()) {
                try {
                    //Group new_group = data.group_articles_db.addGroup(groupName);
                    all_Groups.add(new_group);                    
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                showAlert("Input Error", "Group name cannot be empty.");
            }
        });
    	//data.group_articles_db.addGroup(null);    
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    void selectGroup(ActionEvent event) {
        if(selectedGroup != null)
        {
            selected_groups.add(selectedGroup.getId());
            groups_selected_txt.setText(selected_groups.toString());
        }
    }

    @FXML
    void removeGroup(ActionEvent event) {
        if(selectedGroup != null)
        {
            selected_groups.remove(Integer.valueOf(selectedGroup.getId()));
            groups_selected_txt.setText(selected_groups.toString());
        }
    } 
    
}
