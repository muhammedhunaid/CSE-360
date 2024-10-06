package asu.cse360project;

import java.security.SecureRandom;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class Utils {
    public static String username_regex = "^[a-zA-Z][a-zA-Z0-9._-]{5,15}$";
    public static String password_regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=])[A-Za-z\\d@#$%^&+=]{8,}$";
    public static String email_regex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    public static String name_regex = "^[a-zA-Z]+$";
    private static String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public static String validateUsername(String username) {
        // Define the individual regex conditions
        String lengthRegex = ".{3,15}";
        String startWithLetterRegex = "^[a-zA-Z].*";
        String allowedCharsRegex = "^[a-zA-Z0-9._-]+$";
        String s = "";

        // Check each condition and print specific feedback if not met
        if (!username.matches(lengthRegex)) {
            s += "Username must be between 3 and 15 characters long.\n";
        }
        if (!username.matches(startWithLetterRegex)) {
            s += "Username must start with a letter.\n";
        }
        if (!username.matches(allowedCharsRegex)) {
            s += "Username can only contain letters, digits, underscores (_), hyphens (-), and periods (.).";
        }
        
        if(s == "")
        {
            return "";
        }else{ 
            return s;
        }
    }

    public static String validatePassword(String password) {
        // Define the individual regex conditions
        String lengthRegex = ".{8,}";
        String lowercaseRegex = ".*[a-z].*";
        String uppercaseRegex = ".*[A-Z].*";
        String digitRegex = ".*\\d.*";
        String specialCharRegex = ".*[@#$%^&+=].*";
        String noSpacesRegex = "^[^\\s]*$";

        String s = "";

        // Check each condition and print specific feedback if not met
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

        if(s == "")
        {
            return "";
        }else{ 
            return s;
        }
    }

    public static String validateEmail(String email) {            
            if (email.matches(email_regex)) {
                return "";
            } else {
                return "Invalid Email";
            }
    }

    public static String validateName(String email) {            
        if (email.matches(name_regex)) {
            return "";
        } else {
            return "Invalid Name";
        }
    }   

    public static void enableNode(Node node)
    {
        node.setManaged(true);
        node.setVisible(true);        
    }

    public static void disableNode(Node node)
    {
        node.setManaged(false);
        node.setVisible(false);        
    }

    public static void setLabel(Label label, String text, Color color)
    {
        enableNode(label);
        label.setText(text);
        label.setTextFill(color);
    }
    public static void setText(Text label, String text, Color color)
    {
        enableNode(label);
        label.setText(text);
        label.setFill(color);
    }

    public static boolean isValid(Text text, String valid)
    {
        boolean ret = false;
        if(valid == "")
        {
            ret = true;
            disableNode(text);
        }else{
            ret = false;
            setText(text, valid, Color.RED);
        }
        return ret;
    }

    public static String generateInviteCode(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder inviteCode = new StringBuilder(length);
    
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(CHARACTERS.length());
            inviteCode.append(CHARACTERS.charAt(index));
        }
    
        return inviteCode.toString();
    }

}
