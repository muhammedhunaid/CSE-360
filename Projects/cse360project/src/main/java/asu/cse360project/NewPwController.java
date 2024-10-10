package asu.cse360project;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class NewPwController implements Initializable {

    @FXML
    private Text password_error;

    @FXML
    private TextField password;

    @FXML
    private Text re_password_error;

    @FXML
    private PasswordField re_password;

    @FXML
    private String username;

    
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        Utils.disableNode(password_error);
        Utils.disableNode(re_password_error);
    }
    
    @FXML
    private void finish() {
        String newPassword = password.getText();
        String confirmPassword = re_password.getText();

        if (!newPassword.equals(confirmPassword)) {
            Utils.setText(password_error, "Passwords do not match", Color.RED);
            return;
        }

        if (!newPassword.matches(Utils.password_regex)) {
            Utils.setText(password_error, "Password does not meet criteria", Color.RED);
            return;
        }
        //
        try (Connection conn = Database.getConnection()) {
            String sql = "UPDATE users SET password = ?, one_time_flag = false WHERE username = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, newPassword);
            pstmt.setString(2, username);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                Utils.setText(password_error, "Password updated successfully", Color.GREEN);
                App.setRoot("login");
            } else {
                Utils.setText(password_error, "No user found to update", Color.RED);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Utils.setText(password_error, "Error updating password", Color.RED);
        } catch (IOException e) {
            e.printStackTrace();
            Utils.setText(password_error, "Error navigating to login page", Color.RED);
        }
    }
}
