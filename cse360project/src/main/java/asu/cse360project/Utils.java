package asu.cse360project;

import java.io.IOException;
import java.security.SecureRandom;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
// Utility class for validation and UI management in the application
public class Utils {
    // Regular expressions for validating usernames, passwords, emails, and names
    public static String username_regex = "^[a-zA-Z][a-zA-Z0-9._-]{5,15}$";
    public static String password_regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=])[A-Za-z\\d@#$%^&+=]{8,}$";
    public static String email_regex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    public static String name_regex = "^[a-zA-Z]+$";
    
    // Characters allowed in the invite code
    private static String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    // Validate the provided username based on specified criteria
    public static String validateUsername(String username) {
        // Define the individual regex conditions for username validation
        String lengthRegex = ".{3,15}"; // Length between 3 and 15 characters
        String startWithLetterRegex = "^[a-zA-Z].*"; // Must start with a letter
        String allowedCharsRegex = "^[a-zA-Z0-9._-]+$"; // Allowed characters

        String s = ""; // String to accumulate validation messages

        // Check each condition and append specific feedback if not met
        if (!username.matches(lengthRegex)) {
            s += "Username must be between 3 and 15 characters long.\n";
        }
        if (!username.matches(startWithLetterRegex)) {
            s += "Username must start with a letter.\n";
        }
        if (!username.matches(allowedCharsRegex)) {
            s += "Username can only contain letters, digits, underscores (_), hyphens (-), and periods (.).";
        }
        
        // Return validation messages or an empty string if valid
        return s.isEmpty() ? "" : s; 
    }

    // Validate the provided password based on specified criteria
    public static String validatePassword(String password) {
        // Define the individual regex conditions for password validation
        String lengthRegex = ".{8,}"; // Minimum length of 8 characters
        String lowercaseRegex = ".*[a-z].*"; // Must contain at least one lowercase letter
        String uppercaseRegex = ".*[A-Z].*"; // Must contain at least one uppercase letter
        String digitRegex = ".*\\d.*"; // Must contain at least one digit
        String specialCharRegex = ".*[@#$%^&+=].*"; // Must contain at least one special character
        String noSpacesRegex = "^[^\\s]*$"; // Must not contain spaces

        String s = ""; // String to accumulate validation messages

        // Check each condition and append specific feedback if not met
        if (!password.matches(lengthRegex)) {
            s += "Password must be at least 8 characters long.\n";
        }
        if (!password.matches(lowercaseRegex)) {
            s += "Password must contain at least one lowercase letter.\n";
        } 
        if (!password.matches(uppercaseRegex)) {
            s += "Password must contain at least one uppercase letter.\n";
        }
        if (!password.matches(digitRegex)) {
            s += "Password must contain at least one digit.\n";
        }
        if (!password.matches(specialCharRegex)) {
            s += "Password must contain at least one special character from @#$%^&+=.\n";
        }
        if (!password.matches(noSpacesRegex)) {
            s += "Password must not contain spaces.";
        }

        // Return validation messages or an empty string if valid
        return s.isEmpty() ? "" : s;
    }

    // Validate the provided email address using regex
    public static String validateEmail(String email) {            
        // Return an empty string if the email matches the regex, otherwise return "Invalid Email"
        return email.matches(email_regex) ? "" : "Invalid Email";
    }

    // Validate the provided name using regex
    public static String validateName(String name) {            
        // Return an empty string if the name matches the regex, otherwise return "Invalid Name"
        return name.matches(name_regex) ? "" : "Invalid Name";
    }   

    // Enable a UI node (make it visible and managed)
    public static void enableNode(Node node) {
        node.setManaged(true); // Set the node to be managed by the layout
        node.setVisible(true); // Make the node visible        
    }

    // Disable a UI node (make it invisible and unmanaged)
    public static void disableNode(Node node) {
        node.setManaged(false); // Set the node to be unmanaged by the layout
        node.setVisible(false); // Make the node invisible        
    }

    // Set the text and color of a label and enable it
    public static void setLabel(Label label, String text, Color color) {
        enableNode(label); // Enable the label
        label.setText(text); // Set the text of the label
        label.setTextFill(color); // Set the text color
    }

    // Set the text and color of a Text element and enable it
    public static void setText(Text label, String text, Color color) {
        enableNode(label); // Enable the text element
        label.setText(text); // Set the text of the text element
        label.setFill(color); // Set the fill color of the text
    }

    // Check if a validation result is valid and update the corresponding Text element
    public static boolean isValid(Text text, String valid) {
        boolean ret = false; // Default return value
        
        // If the validation message is empty
        if (valid.isEmpty()) {
            ret = true; // Mark as valid
            disableNode(text); // Disable the text element
        } else {
            ret = false; // Mark as invalid
            setText(text, valid, Color.RED); // Set the text element with the error message in red
        }
        return ret; // Return the validation result
    }

    // Generate a random invite code of a specified length
    public static String generateInviteCode(int length) {
        SecureRandom random = new SecureRandom(); // Create a SecureRandom instance
        StringBuilder inviteCode = new StringBuilder(length); // Initialize StringBuilder for invite code
    
        // Generate the invite code by randomly selecting characters
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(CHARACTERS.length()); // Get a random index
            inviteCode.append(CHARACTERS.charAt(index)); // Append the character at the random index to the invite code
        }
    
        return inviteCode.toString(); // Return the generated invite code
    }

    public static void setRoot(String fxml) throws IOException {
        Singleton data = Singleton.getInstance();
        FXMLLoader loader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        Parent root = loader.load();
        data.scene.setRoot(root);  // Set the root of the scene to the newly loaded FXML
    }

    public static void setContentArea(String fxml) throws IOException {
        Singleton data = Singleton.getInstance();
        Parent fxmlLoader = FXMLLoader.load(App.class.getResource("DashboardScenes/" + fxml + ".fxml"));
        data.content_area.getChildren().removeAll(); // Remove existing children from content area
        data.content_area.getChildren().setAll(fxmlLoader); // Set the new scene
    }
}
