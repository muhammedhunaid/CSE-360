package asu.cse360project.DashboardControllers;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import asu.cse360project.Article;
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

import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 * Controller class for managing users in the system.
 * Provides functionality to add, remove, search, change roles, and reset passwords for users.
 */
public class ManageUsersController implements Initializable {

    Singleton data = Singleton.getInstance();
    // Variables to keep track of user actions and selected user
    boolean adding_user = false;
    private User selectedUser = null;
    Alert alert;
    ObservableList<User> all_Users; // List to hold all users
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

    // Initialization method to set up the UI and load user data
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

    // Method to handle the 'Add User' action
    @FXML
    void add(ActionEvent event) {
        Utils.enableNode(change_role_box); // Enable role change box
        adding_user = true; // Set flag for adding new user
    }

    // Method to handle 'Change Role' action
    @FXML
    void changeRole(ActionEvent event) {
        if (selectedUser != null) {
            Utils.enableNode(change_role_box); // Enable role change if a user is selected
        }
        Utils.setLabel(error_label, "No User Selected", Color.RED); // Show error if no user is selected
    }

    // Method to confirm and apply role changes or add new user
    @FXML
    void confirm_role(ActionEvent event) {
        boolean admin = admin_btn.selectedProperty().getValue();
        boolean instructer = instructor_btn.selectedProperty().getValue();
        boolean student = student_btn.selectedProperty().getValue();

        // If no role is selected, return
        if (!admin && !instructer && !student) {
            return;
        }

        // Set appropriate confirmation message for adding or changing role
        if (adding_user) {
            alert.setContentText("Are you sure you want to add a user");
        } else {
            alert.setContentText("Are you sure you want to change a user's role");
        }

        // Show confirmation dialog and handle user response
        alert.showAndWait().ifPresent(response -> {
             // Determine the selected role(s)
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
            role = role.substring(0, role.length() - 1); // Remove trailing comma

            if (response == ButtonType.OK) {    
                // If adding a new user
                if (adding_user) {
                    addUser(role);
                    return;
                } else {
                    // If changing an existing user's role
                    changeUserRole(role);
                    return;
                }             
            } else {
                Utils.setLabel(error_label, "No User Roles Changed", Color.BLACK); // No role change made
            }
        });

        // Reset role buttons and disable role change box
        admin_btn.setSelected(false);
        instructor_btn.setSelected(false);
        student_btn.setSelected(false);
        Utils.disableNode(change_role_box);
    }

    // Method to add new user
    private void addUser(String role)
    {
        // Input validation
        if (role == null || role.trim().isEmpty()) {
            throw new IllegalArgumentException("Role cannot be null or empty");
        }
        
        String invite_code = Utils.generateInviteCode(5);
        assertNotNull("Invite code should not be null", invite_code);
        assertTrue("Invite code should be 5 characters", invite_code.length() == 5);
        
        Utils.setLabel(error_label, "Invite Code: " + invite_code, Color.GREEN);
        adding_user = false;
        data.user_db.insertUser(invite_code, role);
        try {
            User newUser = data.user_db.getUser(invite_code);
            assertNotNull("New user should not be null", newUser);
            assertEquals("User role should match input", role, newUser.getRole());
            all_Users.add(newUser);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void changeUserRole(String role)
    {
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
            assertTrue("User should exist in list", user_index >= 0);
            
            all_Users.get(user_index).setRole(role);
            assertEquals("User role should be updated", role, all_Users.get(user_index).getRole());
            table.refresh();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to delete a selected user
    @FXML
    void delete(ActionEvent event) {
        if(selectedUser == null)
        {
            return;
        }

        if(selectedUser.getUsername().equals(data.getAppUser().getUsername()))
        {
            Utils.setLabel(error_label, "Cannot remove self from system", Color.RED); // Show error if no user is selected
            return;
        }

        if(data.getAppUser().getLoginRole().equals("instructor") && selectedUser.isAdmin()) {
            Utils.setLabel(error_label, "Cannot remove admins from system", Color.RED); // Show error if no user is selected
            return;
        }

        if (!selectedUser.getUsername().equals(data.getAppUser().getUsername())) {
            // Show confirmation dialog for deleting user
            alert.setContentText("Are you sure you want to delete user");
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    // Remove user from list and database
                    boolean removed;
                    try {
                        removed = data.user_db.deleteUser(selectedUser);
                    } catch (SQLException e) {
                        removed = false;
                    }
                    if(removed) {
                        Utils.setLabel(error_label, "User Removed", Color.BLACK); // Show success message
                        all_Users.remove(all_Users.indexOf(selectedUser));
                    }else{
                        Utils.setLabel(error_label, "Error removing User", Color.BLACK); // Show success message
                    }
                } else {
                    return;
                }
            });
        }
    }

    // Method to reset a selected user's password
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
            Utils.setLabel(error_label, "No User Selected", Color.RED); // Show error if no user is selected
        }
    }

    // Method to search for a user in the table by username
    @FXML
    void searchUser(ActionEvent event) throws SQLException {
        String username = search_user.getText().toString();
        // Input validation
        if (username == null || username.trim().isEmpty()) {
            Utils.setLabel(error_label, "Username cannot be empty", Color.RED);
            return;
        }

        assertNotNull("User list should not be null", all_Users);
        boolean userFound = false;
        
        for (User user : all_Users) {
            assertNotNull("User object should not be null", user);
            assertNotNull("Username should not be null", user.getUsername());
            
            if (user.getUsername().equals(username)) {
                table.getSelectionModel().select(user);
                table.scrollTo(user);
                Utils.setLabel(error_label, "User Found", Color.GREEN);
                userFound = true;
                
                // Verify selected user
                User selectedUser = table.getSelectionModel().getSelectedItem();
                assertNotNull("Selected user should not be null", selectedUser);
                assertEquals("Selected user should match search", username, selectedUser.getUsername());
                return;
            }
        }
        
        if (!userFound) {
            Utils.setLabel(error_label, "User not Found", Color.RED);
        }
    }

    // Add test methods
    @Test
    public void testAddUser() {
        String role = "student";
        addUser(role);
        assertFalse("Adding user flag should be false after adding", adding_user);
        
        // Test invalid input
        try {
            addUser(null);
            fail("Should throw IllegalArgumentException for null role");
        } catch (IllegalArgumentException e) {
            // Expected exception
        }
    }

    @Test
    public void testChangeRole() {
        String newRole = "instructor";
        selectedUser = new User(); // Create test user
        selectedUser.setUsername("testUser");
        selectedUser.setRole("student");
        all_Users.add(selectedUser);
        
        changeUserRole(newRole);
        assertEquals("Role should be updated", newRole, selectedUser.getRole());
        
        // Test invalid input
        try {
            changeUserRole(null);
            fail("Should throw IllegalArgumentException for null role");
        } catch (IllegalArgumentException e) {
            // Expected exception
        }
    }

    @Test
    public void testSearchUser() throws SQLException {
        // Test with valid user
        User testUser = new User();
        testUser.setUsername("testUser");
        all_Users.add(testUser);
        search_user.setText("testUser");
        searchUser(new ActionEvent());
        assertEquals("Should find test user", testUser, table.getSelectionModel().getSelectedItem());
        
        // Test with non-existent user
        search_user.setText("nonexistentUser");
        searchUser(new ActionEvent());
        assertEquals("Should show error for non-existent user", "User not Found", error_label.getText());
        
        // Test with empty input
        search_user.setText("");
        searchUser(new ActionEvent());
        assertEquals("Should show error for empty input", "Username cannot be empty", error_label.getText());
    }
}

