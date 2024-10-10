package asu.cse360project;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

//
public class LoginController implements Initializable{

    @FXML
    private TextField invite_code_textfield;

    @FXML
    private TextField password_textfield;

    @FXML
    private TextField username_textfield;

    @FXML
    private Text login_error;

    
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        Utils.disableNode(login_error);
    }
    //
    @FXML
    private void login(ActionEvent event) throws IOException {
        String username = username_textfield.getText().toString();
        String password = password_textfield.getText().toString();
        boolean hasMultipleRoles = false;
        //
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                boolean oneTimeFlag = rs.getBoolean("one_time_flag");
                boolean emailInDb = rs.getString("email") != null;
                hasMultipleRoles = rs.getBoolean("role_admin") &&
                                   rs.getBoolean("role_student") &&
                                   rs.getBoolean("role_instructor");

                if (oneTimeFlag) {
                    App.setRoot("new_pw");
                    return;
                }

                if (!emailInDb) {
                    App.setRoot("setup_account");
                    return;
                }

                if (hasMultipleRoles) {
                    App.setRoot("select_role");
                    return;
                }

                App.setRoot("dashboard");
                return;
            } else {
                Utils.setText(login_error, "Username or Password not found", Color.RED);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Utils.setText(login_error, "Database error", Color.RED);
        }
    }

    @FXML
    private void join(ActionEvent event) throws IOException {
        String inviteCode = invite_code_textfield.getText().toString();

        if (inviteCode.isEmpty()) {
            Utils.setText(login_error, "Invite code cannot be empty", Color.RED);
            return;
        }

        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT username FROM users WHERE invite_code = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, inviteCode);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // Invite code is valid, transition to NewPwController
                String username = rs.getString("username");
                // Store the username for which the password will be reset
                NewPwController.setUsername(username);
                // Switch to the new password scene
                App.setRoot("new_pw");
            } else {
                Utils.setText(login_error, "Invalid invite code", Color.RED);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Utils.setText(login_error, "Error processing invite code", Color.RED);
        }
    }

    // Method to check user credentials
    private boolean checkCredentials(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}