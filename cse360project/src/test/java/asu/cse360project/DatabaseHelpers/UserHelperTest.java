package asu.cse360project.DatabaseHelpers;

import asu.cse360project.User;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class UserHelperTest {
    private UserHelper userHelper;
    private Connection connection;
    private Statement statement;

    @Before
    public void setUp() throws SQLException {
        // You'll need to set up a test database connection or mock connection
        // This is a placeholder and should be replaced with actual connection setup
        connection = null; // Replace with actual test database connection
        statement = null; // Replace with actual statement
        userHelper = new UserHelper(connection, statement);
    }

    @Test
    public void testGetUser() throws SQLException {
        // Assuming you have a test user in your database
        User user = userHelper.getUser("testUser");
        assertNotNull("User should not be null", user);
        assertTrue("User should be instance of User class", user instanceof User);
    }
}
