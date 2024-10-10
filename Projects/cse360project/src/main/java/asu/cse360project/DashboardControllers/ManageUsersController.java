/**
 
<p> ManageUsersController Class. </p>
<p> Description: This class manages the user interface for handling user management tasks such as adding, deleting,
changing roles, and resetting passwords for users in the system. It interacts with the database to perform these operations
and updates the UI accordingly.</p>
<p> Copyright: Tu35 Team © 2024 </p>
@version 1.00        2024-10-09 Phase 1 implementation of the user management controller
@author Tu35 Team
*/
package asu.cse360project.DashboardControllers;

import java.net.URL;
import java.util.ResourceBundle;

import asu.cse360project.App;
import asu.cse360project.SceneController;
import asu.cse360project.User;
import asu.cse360project.Utils;
import asu.cse360project.Database;
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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ManageUsersController extends SceneController implements Initializable{

    boolean adding_user = false;
    private User selectedUser = null;
    Alert alert;
    ObservableList<User> all_Users;

    @FXML
    private RadioButton admin_btn;

    @FXML
    private VBox change_role_box;

    @FXML
    private RadioButton instructor_btn;

    @FXML
    private Label error_label;

    @FXML
    private TableColumn<User, String> name_col;

    @FXML
    private TableColumn<User, String> role_col;

    @FXML
    private Text role_text;

    @FXML
    private Button search_button;

    @FXML
    private TextField search_user;

    @FXML
    private RadioButton student_btn;

    @FXML
    private TableView<User> table;

    @FXML
    private TableColumn<User, String> username_col;

    
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        Utils.disableNode(change_role_box);
        Utils.disableNode(error_label);

        alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirm");
        alert.setHeaderText("Please confirm your action");

        username_col.setCellValueFactory(new PropertyValueFactory<>("username"));
        name_col.setCellValueFactory(new PropertyValueFactory<>("first_name"));
        role_col.setCellValueFactory(new PropertyValueFactory<>("role"));

        all_Users = FXCollections.observableArrayList();
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT username, first_name, " +
                        "CASE " +
                        "    WHEN role_admin = 1 THEN 'Admin' " +
                        "    WHEN role_student = 1 THEN 'Student' " +
                        "    WHEN role_instructor = 1 THEN 'Instructor' " +
                        "    ELSE 'Unknown' " +
                        "END AS role " +
                        "FROM users";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                all_Users.add(new User(
                    rs.getString("username"),
                    rs.getString("password")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        table.setItems(all_Users);

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedUser = newSelection;
                Utils.disableNode(error_label);
            }
        });
    }

    @FXML
    void list_all(ActionEvent event) {
        table.setItems(all_Users);
    }

    @FXML
    void add(ActionEvent event) {
        Utils.enableNode(change_role_box);
        adding_user = true;
    }

    @FXML
    void changeRole(ActionEvent event) {
        if(selectedUser == null) {
            Utils.setLabel(error_label, "No User Selected", Color.RED);
        } else {
            Utils.enableNode(change_role_box);
        }
    }

    @FXML
    void confirm_role(ActionEvent event) {
        boolean admin = admin_btn.selectedProperty().getValue();
        boolean instructer = instructor_btn.selectedProperty().getValue();
        boolean student = student_btn.selectedProperty().getValue();

        if(!admin && !instructer && !student)
        {
            return;
        }else{
            if(adding_user)
            {
                alert.setContentText("Are you sure you want to add a user");
            }else{
                alert.setContentText("Are you sure you want to change a users role");
            }
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    if(adding_user)
                    {
                        String invite_code = Utils.generateInviteCode(10);
                        Utils.setLabel(error_label, "Invite Code: " + invite_code, Color.GREEN);
                        //add to db
                    }else{
                        //Utils.setText(display_text, "User Roles Changed", Color.BLACK);
                        //create new db entry
                        //add invite code to entry
                    }

                } else {
                    //Utils.setText(display_text, "No User Roles Changed", Color.BLACK);
                }
            });
        }
        
        adding_user = false;
        Utils.disableNode(change_role_box);
    }

    @FXML
    void delete(ActionEvent event) {
        if(selectedUser != null)
        {
            alert.setContentText("Are you sure you want to delete user");
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    //remove user from db
                    //Utils.setText(display_text, "User Removed", Color.BLACK);
                } else {
                    //Utils.setText(display_text, "User not Removed", Color.BLACK);
                }
            });    
        }else{
            Utils.setLabel(error_label, "No User Selected", Color.RED);
        }
    }

    @FXML
    void resetPassword(ActionEvent event) {
        if (selectedUser != null) {
            alert.setContentText("Are you sure you want to reset user " + selectedUser.getUsername() + "'s password?");
                alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    String oneTimePassword = Utils.generateInviteCode(10);
                    Utils.setLabel(error_label, "One-time Password: " + oneTimePassword, Color.GREEN);
                    try (Connection conn = Database.getConnection()) {
                        String sql = "UPDATE users SET password = ?, one_time_flag = ? WHERE username = ?";
                        PreparedStatement pstmt = conn.prepareStatement(sql);
                        pstmt.setString(1, oneTimePassword);
                        pstmt.setBoolean(2, true);
                        pstmt.setString(3, selectedUser.getUsername());
                        pstmt.executeUpdate();
                    } catch (SQLException e) {
                        e.printStackTrace();
                        Utils.setLabel(error_label, "Error resetting password", Color.RED);
                    }
                }
            });
        } else {
            Utils.setLabel(error_label, "No user selected", Color.RED);
        }
    }

    @FXML
    void searchUser(ActionEvent event) {
        String user = search_user.getText().toString();
        User foundUser = null;
        ObservableList<User> single_user = FXCollections.observableArrayList();

        boolean user_in_db = false;
        //check if in db

        if(user_in_db)
        {
            single_user.add(App.user);
            table.setItems(single_user);
    
            //Utils.setLabel(error_label, "User Found", Color.GREEN);
        }else{
            Utils.setLabel(error_label, "User not Found", Color.RED);
        }
    }
    // Method to load users from the database
    private void loadUsers() {
        String sql = "SELECT username, first_name, role FROM users";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            all_Users.clear();
            while (rs.next()) {
                String username = rs.getString("username");
                String firstName = rs.getString("first_name");
                String role = rs.getString("role");
                all_Users.add(new User(username, firstName, role));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

