package asu.cse360project;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class SetupAccountController {

    @FXML
    private TextField email;

    @FXML
    private TextField first_name;

    @FXML
    private TextField middle_name;

    @FXML
    private TextField last_name;

    @FXML
    private Text email_error;

    @FXML
    private Text first_name_error;

    @FXML
    private Text middle_name_error;

    @FXML
    private Text last_name_error;

    @FXML
    //
    private void finish() {
        String emailInput = email.getText();
        String firstName = first_name.getText();
        String middleName = middle_name.getText();
        String lastName = last_name.getText();

        if (validateInputs(emailInput, firstName, lastName)) {
            try (Connection conn = Database.getConnection()) {
                String sql = "INSERT INTO users (email, first_name, middle_name, last_name) VALUES (?, ?, ?, ?)";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, emailInput);
                pstmt.setString(2, firstName);
                pstmt.setString(3, middleName);
                pstmt.setString(4, lastName);
                pstmt.executeUpdate();
                // Navigate to the next scene or show success message
            } catch (SQLException e) {
                e.printStackTrace();
                // Handle SQL exception
            }
        }
    }

    private boolean validateInputs(String email, String firstName, String lastName) {
        boolean valid = true;
        if (email.isEmpty()) {
            email_error.setText("Email is required");
            valid = false;
        }
        if (firstName.isEmpty()) {
            first_name_error.setText("First name is required");
            valid = false;
        }
        if (lastName.isEmpty()) {
            last_name_error.setText("Last name is required");
            valid = false;
        }
        return valid;
    }
}