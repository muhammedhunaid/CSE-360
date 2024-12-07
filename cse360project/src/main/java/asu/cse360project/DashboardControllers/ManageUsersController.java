package asu.cse360project.DashboardControllers;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

import asu.cse360project.Article;
import asu.cse360project.Group;
import asu.cse360project.Singleton;
import asu.cse360project.User;
import asu.cse360project.Utils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

/**
 * Controller class for managing users in the application.
 * This class handles all user management operations including:
 * - User creation and deletion
 * - Role assignment and modification
 * - User information display and updates
 * - Group membership management
 * 
 * The controller implements JavaFX's Initializable interface and provides
 * a comprehensive user management interface through various UI components
 * and database interactions.
 *
 * @author Tu35
 * @version 3.00 2024-11-15 Enhanced role management and added user deletion functionality
 * @version 2.00 2024-11-01 Added database connection and initialization
 * @version 1.00 2024-10-30 Initial version with basic user management functionalities
 */
public class ManageUsersController implements Initializable {

    /** Singleton instance for sharing data across the application */
    Singleton data = Singleton.getInstance();
    
    /** Flag indicating if a new user is being added */
    boolean adding_user = false;
    
    /** Currently selected role for user creation/modification */
    public String role = "";
    
    /** Currently selected user in the interface */
    private User selectedUser = null;
    
    /** Alert dialog for user notifications */
    Alert alert;
    
    /** Observable list of all users in the system */
    ObservableList<User> all_Users;
    
    /** Observable list of all articles in the system */
    ObservableList<Article> all_articles;

    // FXML elements for the UI
    @FXML private RadioButton admin_btn;
    @FXML private VBox change_role_box;
    @FXML private RadioButton instructor_btn;
    @FXML private Label error_label;
    @FXML private TableColumn<User, String> name_col;
    @FXML private TableColumn<User, String> role_col;
    @FXML private Text role_text;
    @FXML private Button search_button;
    @FXML private TextField search_user;
    @FXML private RadioButton student_btn;
    @FXML private TableView<User> table;
    @FXML private TableColumn<User, String> username_col;

    /**
     * Initializes the controller after FXML injection is complete.
     * Sets up table columns, loads initial data, and configures event handlers.
     *
     * @param location The location used to resolve relative paths for the root object
     * @param resources The resources used to localize the root object
     */
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {

        Singleton data = Singleton.getInstance();
        // Disable role change box and error label initially
        Utils.disableNode(change_role_box);
        Utils.disableNode(error_label);

        // Initialize alert for confirmation dialogs
        alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirm");
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Please confirm your action");

        // Set up table columns to display user information
        username_col.setCellValueFactory(new PropertyValueFactory<>("username"));
        name_col.setCellValueFactory(new PropertyValueFactory<>("first_name"));
        role_col.setCellValueFactory(new PropertyValueFactory<>("role"));

        // Load all users from the database
        all_Users = FXCollections.observableArrayList();

        //Load all the articles from the table articles in the database
        all_articles = FXCollections.observableArrayList();
        try {
            all_Users = data.user_db.ListUsers(all_Users);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        table.setItems(all_Users);

        // Add listener to handle row selection in the table
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedUser = newSelection;
                Utils.disableNode(error_label);
            }
        });
    }

    /**
     * Handles the action when the 'Add User' button is clicked.
     * Enables the role change box and sets the adding_user flag to true.
     *
     * @param event The action event triggered by the button click
     */
    @FXML
    void add(ActionEvent event) {
        Utils.enableNode(change_role_box); // Enable role change box
        adding_user = true; // Set flag for adding new user
    }

    /**
     * Handles the action when the 'Change Role' button is clicked.
     * Enables the role change box if a user is selected.
     *
     * @param event The action event triggered by the button click
     */
    @FXML
    void changeRole(ActionEvent event) {
        if (selectedUser != null) {
            Utils.enableNode(change_role_box); // Enable role change if a user is selected
        }else{
            Utils.setLabel(error_label, "No User Selected", Color.RED); // Show error if no user is selected
        }
    }

    /**
     * Handles the action when the 'Confirm Role' button is clicked.
     * Applies role changes or adds a new user after confirmation.
     *
     * @param event The action event triggered by the button click
     */
    @FXML
    void confirm_role(ActionEvent event) {
        role = getRole();

        if(role.equals("")) {
            Utils.showErrorFeedback(error_label, "Please select at least one role");
            return;
        }

        // Set appropriate confirmation message for adding or changing role
        if (adding_user) {
            alert.setContentText("Are you sure you want to add a user with role(s): " + role + "?");
        } else {
            alert.setContentText("Are you sure you want to change the user's role to: " + role + "?");
        }

        if(!adding_user) {
            try {
                ArrayList<Group> user_groups = new ArrayList<>(data.group_articles_db.getAllSpecialGroups(selectedUser.getId()));
                if (onlyAdminofGroups(user_groups, selectedUser) && role.equals("student")) {
                    Utils.showErrorFeedback(error_label, "Cannot change role because changing role would remove only admin of a group");
                    return;
                }
            } catch (SQLException e) {
                return;
            }
        }

        String finalRole = role;
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {    
                try {
                    if (adding_user) {
                        addUser(finalRole);
                        Utils.showSuccessFeedback(error_label, "User added successfully!");
                    } else {
                        changeUserRole(finalRole);
                        Utils.showSuccessFeedback(error_label, "User role updated successfully!");
                    }
                } catch (Exception e) {
                    Utils.showErrorFeedback(error_label, "Error: " + e.getMessage());
                }
            } else {
                Utils.showInfoFeedback(error_label, "Operation cancelled");
            }
        });

        admin_btn.setSelected(false);
        instructor_btn.setSelected(false);
        student_btn.setSelected(false);
        Utils.disableNode(change_role_box);
    }

    /**
     * Retrieves the currently selected role(s) from the radio buttons.
     *
     * @return A comma-separated string of selected roles
     */
    public String getRole()
    {
        boolean admin = admin_btn.selectedProperty().getValue();
        boolean instructer = instructor_btn.selectedProperty().getValue();
        boolean student = student_btn.selectedProperty().getValue();

        String role = "";
        if (admin) {
            role += "admin,";
        }
        if (instructer) {
            role += "instructor,";
        }
        if (student) {
            role += "student,";
        }

        if(!role.isEmpty()) {
            role.substring(0, role.length() - 1); // Remove trailing comma        
        }
        return role;
    }

    /**
     * Checks if a user is the only admin of any groups.
     *
     * @param groups The list of groups to check
     * @param user The user to check
     * @return True if the user is the only admin of any group, false otherwise
     */
    public boolean onlyAdminofGroups(ArrayList<Group> groups, User user) {
        for (Group g : groups)
        {
            if(g.isOnlyAdmin(user)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Adds a new user to the system with the specified role.
     *
     * @param role The role to assign to the new user
     */
    private void addUser(String role)
    {
        // Input validation
        if (role == null || role.trim().isEmpty()) {
            throw new IllegalArgumentException("Role cannot be null or empty");
        }
        
        String invite_code = Utils.generateInviteCode(5);
        Utils.setLabel(error_label, "Invite Code: " + invite_code, Color.GREEN);
        adding_user = false;
        data.user_db.insertUser(invite_code, role);
        try {
            User newUser = data.user_db.getUser(invite_code);
            all_Users.add(newUser);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Changes the role of the currently selected user.
     *
     * @param role The new role to assign to the user
     */
    private void changeUserRole(String role)
    {
        if(selectedUser == null) {
            return;
        }

        if (selectedUser != null) {
            try {
                data.user_db.changeRole(selectedUser.getUsername(), role); // Update user role in database
                int user_index = all_Users.indexOf(selectedUser);
                all_Users.get(user_index).setRole(role); // Update role in the list
                table.refresh(); // Refresh table to show changes
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        // Input validation
        if (role == null || role.trim().isEmpty()) {
            throw new IllegalArgumentException("Role cannot be null or empty");
        }
        if (selectedUser == null) {
            throw new IllegalStateException("No user selected for role change");
        }

        try {
            data.user_db.changeRole(selectedUser.getUsername(), role);
            int user_index = all_Users.indexOf(selectedUser);
            all_Users.get(user_index).setRole(role);
            table.refresh();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes the currently selected user from the system.
     *
     * @param event The action event triggered by the button click
     */
    @FXML
    void delete(ActionEvent event) {
        if (selectedUser == null) {
            Utils.showErrorFeedback(error_label, "Please select a user to delete");
            return;
        }

        if (selectedUser.getUsername().equals(data.getAppUser().getUsername())) {
            Utils.showErrorFeedback(error_label, "Cannot remove self from system");
            return;
        }

        if (selectedUser.isAdmin()) {
            Utils.showErrorFeedback(error_label, "Cannot remove admins from system");
            return;
        }

        try {
            ArrayList<Group> user_groups = new ArrayList<>(data.group_articles_db.getAllSpecialGroups(selectedUser.getId()));
            if (onlyAdminofGroups(user_groups, selectedUser)) {
                Utils.showErrorFeedback(error_label, "Cannot delete user because it would remove only admin of a group");
                return;
            }
        } catch (SQLException e) {
            return;
        }

        alert.setContentText("Are you sure you want to delete user: " + selectedUser.getUsername() + "?");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    boolean removed = data.user_db.deleteUser(selectedUser);
                    if(removed) {
                        Utils.showSuccessFeedback(error_label, "User deleted successfully!");
                        all_Users.remove(all_Users.indexOf(selectedUser));
                    } else {
                        Utils.showErrorFeedback(error_label, "Error deleting user");
                    }
                } catch (Exception e) {
                    Utils.showErrorFeedback(error_label, "Error deleting user: " + e.getMessage());
                }
            } else {
                Utils.showInfoFeedback(error_label, "Delete operation cancelled");
            }
        });
    }

    /**
     * Resets the password of the currently selected user.
     *
     * @param event The action event triggered by the button click
     */
    @FXML
    void resetPassword(ActionEvent event) {
        if (selectedUser != null) {
            // Show confirmation dialog for resetting password
            alert.setContentText("Are you sure you want to reset user");
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    String one_time_pw = Utils.generateInviteCode(10); // Generate a one-time password
                    Utils.setLabel(error_label, "One time Password: " + one_time_pw, Color.GREEN); // Display OTP
                    try {
                        data.user_db.resetOTPPassword(selectedUser.getUsername(), one_time_pw); // Reset password in the database
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            Utils.showErrorFeedback(error_label, "No User Selected");
        }
    }

    /**
     * Searches for a user in the table by username.
     *
     * @param event The action event triggered by the button click
     */
    @FXML
    void searchUser(ActionEvent event) throws SQLException {
        String username = search_user.getText().toString();
        // Input validation
        if (username == null || username.trim().isEmpty()) {
            Utils.showErrorFeedback(error_label, "Username cannot be empty");
            return;
        }

        boolean userFound = false;
        
        for (User user : all_Users) {
            if (user.getUsername().equals(username)) {
                table.getSelectionModel().select(user);
                table.scrollTo(user);
                Utils.showSuccessFeedback(error_label, "User Found");
                userFound = true;
                
                // Verify selected user
                User selectedUser = table.getSelectionModel().getSelectedItem();
                return;
            }
        }
        
        if (!userFound) {
            Utils.showErrorFeedback(error_label, "User not Found");
        }
    }
}
