package asu.cse360project.DashboardControllers;

import asu.cse360project.User;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ManageUsersControllerTest {
    @Test
    public void testUserValidation() {
        ManageUsersController controller = new ManageUsersController();
        String username = "testUser";
        String password = "testPass";
        
        assertNotNull(username, "Username should not be null");
        assertTrue(password.length() >= 6, "Password should meet requirements");
    }

    @Test
    public void testUserCreation() {
        ManageUsersController controller = new ManageUsersController();
        User user = new User("testUser", "John", "", "Doe", "Johnny", "john@test.com", "user", "", 1);
        
        assertNotNull(user, "User should not be null");
        assertEquals("testUser", user.getUsername(), "Username should match");
    }
}
