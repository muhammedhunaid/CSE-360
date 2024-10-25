package asu.cse360project;

import java.io.IOException;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import asu.cse360project.DatabaseHelper;

/**
 * Controller class for managing users in the system.
 * Provides functionality to add, remove, search, change roles, and reset passwords for users.
 */
public class GroupController implements Initializable {

    Alert alert;
    ObservableList<Group> all_Groups; // List to hold all group names
    
    private Group selectedGroup = null;
	
    // FXML elements for the UI
    @FXML private TableColumn<Group, String> groupname_col;
    @FXML private TableView<Group> table;
    @FXML private Label error_label;
    @FXML private Button search_button;
    @FXML private TextField search_group;
    @FXML private TableColumn<Group, String> article_ids_col;

    // Initialization method to set up the UI and load user data
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        // Disable role change box and error label initially
        Utils.disableNode(error_label);

        // Initialize alert for confirmation dialogs
        alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirm");
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Please confirm your action");

        // Set up table columns to display user information
        groupname_col.setCellValueFactory(new PropertyValueFactory<>("group_name"));
        article_ids_col.setCellValueFactory(new PropertyValueFactory<>("ids_col"));

        // Load all users from the database
        all_Groups = FXCollections.observableArrayList();
        try {
        	all_Groups = App.databaseHelper.listGroups(all_Groups);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        table.setItems(all_Groups);

        // Add listener to handle row selection in the table
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
            	selectedGroup = newSelection;
                Utils.disableNode(error_label);
            }
        });
    }
    
    // Method to search for a user in the table by username
    @FXML
    void searchGroup(ActionEvent event) throws SQLException {
        String grpname = search_group.getText().toString();
        for (Group grp : all_Groups) {
            if (grp.getName().equals(grpname)) {
                table.getSelectionModel().select(grp); // Highlight the row if user is found
                table.scrollTo(grp); // Scroll to the row
                Utils.setLabel(error_label, "Group Found", Color.GREEN); // Show success message
                return;
            } else {
                Utils.setLabel(error_label, "Group not Found", Color.RED); // Show error if user is not found
            }
        }
    }
    
    @FXML
    void hi(ActionEvent event) {
    	System.out.println("Clicked");
    }
    
    @FXML
    void addGroup(ActionEvent event) throws SQLException{
    	long[] list = {47380L, 982L};
    	DatabaseHelper databaseHelper = new DatabaseHelper();
		databaseHelper.insertGroup("Dummy", list);
    }
    
    @FXML
    void goBackDashboard(ActionEvent event) throws IOException {
    	Utils.setRoot("dashboard");
    }

//    // Method to handle the 'Add User' action
//    @FXML
//    void add(ActionEvent event) {
//        Utils.enableNode(change_role_box); // Enable role change box
//        adding_user = true; // Set flag for adding new user
//    }
//
//    // Method to handle 'Change Role' action
//    @FXML
//    void changeRole(ActionEvent event) {
//        if (selectedUser != null) {
//            Utils.enableNode(change_role_box); // Enable role change if a user is selected
//        }
//        Utils.setLabel(error_label, "No User Selected", Color.RED); // Show error if no user is selected
//    }
//
//    // Method to confirm and apply role changes or add new user
//    @FXML
//    void confirm_role(ActionEvent event) {
//        boolean admin = admin_btn.selectedProperty().getValue();
//        boolean instructer = instructor_btn.selectedProperty().getValue();
//        boolean student = student_btn.selectedProperty().getValue();
//
//        // If no role is selected, return
//        if (!admin && !instructer && !student) {
//            return;
//        }
//
//        // Set appropriate confirmation message for adding or changing role
//        if (adding_user) {
//            alert.setContentText("Are you sure you want to add a user");
//        } else {
//            alert.setContentText("Are you sure you want to change a user's role");
//        }
//
//        // Show confirmation dialog and handle user response
//        alert.showAndWait().ifPresent(response -> {
//             // Determine the selected role(s)
//            String role = "";
//            if (admin) {
//                role += "admin,";
//            }
//            if (instructer) {
//                role += "instructor,";
//            }
//            if (student) {
//                role += "student,";
//            }
//            role = role.substring(0, role.length() - 1); // Remove trailing comma
//
//            if (response == ButtonType.OK) {    
//                // If adding a new user
//                if (adding_user) {
//                    addUser(role);
//                    return;
//                }else{
//                    // If changing an existing user's role
//                    changeRole(role);
//                    return;
//                }             
//            } else {
//                Utils.setLabel(error_label, "No User Roles Changed", Color.BLACK); // No role change made
//            }
//        });
//
//        // Reset role buttons and disable role change box
//        admin_btn.setSelected(false);
//        instructor_btn.setSelected(false);
//        student_btn.setSelected(false);
//        Utils.disableNode(change_role_box);
//    }
//
//    // Method to add new user
//    private void addUser(String role)
//    {
//        String invite_code = Utils.generateInviteCode(5);
//        Utils.setLabel(error_label, "Invite Code: " + invite_code, Color.GREEN);
//        adding_user = false; // Reset adding flag
//        App.databaseHelper.insertUser(invite_code, role); // Add user to database
//        try {
//            all_Users.add(App.databaseHelper.getUser(invite_code)); // Add user to list and refresh
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void changeRole(String role)
//    {
//        if (selectedUser != null) {
//            try {
//                App.databaseHelper.changeRole(selectedUser.getUsername(), role); // Update user role in database
//                int user_index = all_Users.indexOf(selectedUser);
//                all_Users.get(user_index).setRole(role); // Update role in the list
//                table.refresh(); // Refresh table to show changes
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    // Method to delete a selected user
//    @FXML
//    void delete(ActionEvent event) {
//        if (selectedUser != null) {
//            // Show confirmation dialog for deleting user
//            alert.setContentText("Are you sure you want to delete user");
//            alert.showAndWait().ifPresent(response -> {
//                if (response == ButtonType.OK) {
//                    // Remove user from list and database
//                    all_Users.remove(all_Users.indexOf(selectedUser));
//                    App.databaseHelper.deleteUser(selectedUser.getUsername());
//                    Utils.setLabel(error_label, "User Removed", Color.BLACK); // Show success message
//                } else {
//                    Utils.setLabel(error_label, "User not Removed", Color.BLACK); // Show message if no deletion is made
//                }
//            });
//        } else {
//            Utils.setLabel(error_label, "No User Selected", Color.RED); // Show error if no user is selected
//        }
//    }
//
//    // Method to reset a selected user's password
//    @FXML
//    void resetPassword(ActionEvent event) {
//        if (selectedUser != null) {
//            // Show confirmation dialog for resetting password
//            alert.setContentText("Are you sure you want to reset user");
//            alert.showAndWait().ifPresent(response -> {
//                if (response == ButtonType.OK) {
//                    String one_time_pw = Utils.generateInviteCode(10); // Generate a one-time password
//                    Utils.setLabel(error_label, "One time Password: " + one_time_pw, Color.GREEN); // Display OTP
//                    try {
//                        App.databaseHelper.resetOTPPassword(selectedUser.getUsername(), one_time_pw); // Reset password in the database
//                    } catch (SQLException e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
//        } else {
//            Utils.setLabel(error_label, "No User Selected", Color.RED); // Show error if no user is selected
//        }
//    }
//
//    // Method to search for a user in the table by username
//    @FXML
//    void searchUser(ActionEvent event) throws SQLException {
//        String username = search_user.getText().toString();
//        for (User user : all_Users) {
//            if (user.getUsername().equals(username)) {
//                table.getSelectionModel().select(user); // Highlight the row if user is found
//                table.scrollTo(user); // Scroll to the row
//                Utils.setLabel(error_label, "User Found", Color.GREEN); // Show success message
//                return;
//            } else {
//                Utils.setLabel(error_label, "User not Found", Color.RED); // Show error if user is not found
//            }
//        }
//    }
}

