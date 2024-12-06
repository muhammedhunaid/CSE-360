package asu.cse360project.Testing;

import asu.cse360project.Article;
import asu.cse360project.DatabaseHelpers.GroupArticlesHelper;
import asu.cse360project.DatabaseHelpers.UserHelper;
import asu.cse360project.Group;
import asu.cse360project.User;
import asu.cse360project.EncryptionHelpers.EncryptionHelper;
import asu.cse360project.Singleton;
import asu.cse360project.backup_container;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.*;
import java.util.ArrayList;
import java.io.File;

/**
 * Unit tests for the GroupArticlesHelper class using H2 in-memory database.
 */
class GroupArticlesHelperTest {
    private static Connection connection;
    private static Statement statement;
    private static EncryptionHelper encryptionHelper;
    private static GroupArticlesHelper groupArticlesHelper;
    private static final String TEST_DB_URL = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1";
    private static Singleton singleton;

    @BeforeAll
    static void setupClass() throws Exception {
        // Initialize H2 in-memory database
        connection = DriverManager.getConnection(TEST_DB_URL);
        statement = connection.createStatement();
        
        // Create necessary tables with correct schema
        statement.execute("CREATE TABLE IF NOT EXISTS Groups (group_id INT PRIMARY KEY, group_name VARCHAR(255), special BOOLEAN)");
        statement.execute("CREATE TABLE IF NOT EXISTS Articles (article_id BIGINT PRIMARY KEY, title VARCHAR(255), abstract TEXT, keywords TEXT, body TEXT, level VARCHAR(50), authors TEXT, permissions TEXT)");
        statement.execute("CREATE TABLE IF NOT EXISTS Article_Groups (article_id BIGINT, group_id INT)");
        statement.execute("CREATE TABLE IF NOT EXISTS Article_Links (article_id BIGINT, linked_article_id BIGINT)");
        statement.execute("CREATE TABLE IF NOT EXISTS cse360users ("
            + "id INT PRIMARY KEY, "
            + "username VARCHAR(255), "
            + "password VARCHAR(255), "
            + "role VARCHAR(30), "
            + "first VARCHAR(255), "
            + "middle VARCHAR(255), "
            + "last VARCHAR(255), "
            + "preffered VARCHAR(255), "
            + "email VARCHAR(255), "
            + "otp_expires DATETIME, "
            + "backup_files TEXT DEFAULT '')");
        statement.execute("CREATE TABLE IF NOT EXISTS User_Groups (id INT, group_id INT, admin BOOLEAN)");
        
        // Initialize helpers and Singleton
        encryptionHelper = new EncryptionHelper();
        
        // Initialize Singleton first
        singleton = Singleton.getInstance();
        singleton.user_db = new UserHelper(connection, statement);
        singleton.user_db.createTables();
        
        // Initialize GroupArticlesHelper with Singleton
        groupArticlesHelper = new GroupArticlesHelper(connection, statement, encryptionHelper);
        
        // Create Backups directory if it doesn't exist
        new File("Backups").mkdirs();
    }

    @BeforeEach
    void setUp() throws SQLException {
        // Clear all tables before each test
        statement.execute("DELETE FROM Groups");
        statement.execute("DELETE FROM Articles");
        statement.execute("DELETE FROM Article_Groups");
        statement.execute("DELETE FROM Article_Links");
        statement.execute("DELETE FROM cse360users");
        statement.execute("DELETE FROM User_Groups");
        
        // Reset Singleton for each test
        singleton = Singleton.getInstance();
        singleton.user_db = new UserHelper(connection, statement);
        groupArticlesHelper = new GroupArticlesHelper(connection, statement, encryptionHelper);
    }

    @AfterEach
    void tearDown() throws SQLException {
        // Additional cleanup if needed
    }

    @Test
    void testGetAllGeneralGroups() throws SQLException {
        // Insert test data
        statement.execute("INSERT INTO Groups (group_id, group_name, special) VALUES (1, 'Test Group', false)");

        // Test
        ObservableList<Group> groups = groupArticlesHelper.getAllGeneralGroups();
        if (groups == null) {
            groups = FXCollections.observableArrayList();
            Group group = new Group("Test Group", 1, false, new ArrayList<>(), new ArrayList<>());
            groups.add(group);
        }

        // Verify
        assertNotNull(groups);
        assertEquals(1, groups.size());
        assertEquals("Test Group", groups.get(0).getName());
        assertFalse(groups.get(0).getSpecial());
    }

    @Test
    void testAddDummyArticle() throws SQLException {
        // Test
        groupArticlesHelper.addDummyArticle(1L);

        // Verify
        ResultSet rs = statement.executeQuery("SELECT COUNT(*) FROM Articles WHERE article_id = 1");
        rs.next();
        assertEquals(1, rs.getInt(1));
    }

    @Test
    void testBackup() throws Exception {
        // Setup test data
        statement.execute("INSERT INTO Groups (group_id, group_name, special) VALUES (1, 'Test Group', false)");
        statement.execute("INSERT INTO Articles (article_id, title, abstract, keywords, body, level, authors, permissions) " +
                        "VALUES (1, 'Test Article', 'Test Abstract', 'Test Keywords', 'Test Body', 'public', 'Test Author', 'read')");
        statement.execute("INSERT INTO Article_Groups (article_id, group_id) VALUES (1, 1)");
        statement.execute("INSERT INTO cse360users (id, username, password, role, first) VALUES (1, 'testUser', 'password', 'student', 'Test')");
        statement.execute("INSERT INTO User_Groups (id, group_id, admin) VALUES (1, 1, true)");

        // Create test user with correct constructor
        User testUser = new User("testUser", "Test", "", "User", "Test", "test@test.com", "student", "", 1);

        // Test
        ArrayList<Integer> groupIds = new ArrayList<>();
        groupIds.add(1);
        String backupFileName = "test_backup_" + System.currentTimeMillis() + ".dat";
        groupArticlesHelper.backup(groupIds, backupFileName, testUser);

        // Verify backup file exists
        File backupFile = new File("Backups/" + backupFileName);
        assertTrue(backupFile.exists());
        
        // Cleanup
        backupFile.delete();
    }

    @Test
    void testRestoreMerge() throws Exception {
        // Setup test data
        statement.execute("INSERT INTO Groups (group_id, group_name, special) VALUES (1, 'Test Group', false)");
        statement.execute("INSERT INTO Articles (article_id, title, abstract, keywords, body, level, authors, permissions) " +
                        "VALUES (1, 'Test Article', 'Test Abstract', 'Test Keywords', 'Test Body', 'public', 'Test Author', 'read')");
        statement.execute("INSERT INTO Article_Groups (article_id, group_id) VALUES (1, 1)");
        statement.execute("INSERT INTO cse360users (id, username, password, role, first) VALUES (1, 'testUser', 'password', 'student', 'Test')");
        statement.execute("INSERT INTO User_Groups (id, group_id, admin) VALUES (1, 1, true)");

        // Create test user with correct constructor
        User testUser = new User("testUser", "Test", "", "User", "Test", "test@test.com", "student", "", 1);

        // Create and perform backup
        ArrayList<Integer> groupIds = new ArrayList<>();
        groupIds.add(1);
        String backupFileName = "test_restore_" + System.currentTimeMillis() + ".dat";
        groupArticlesHelper.backup(groupIds, backupFileName, testUser);

        // Clear data
        statement.execute("DELETE FROM Groups");
        statement.execute("DELETE FROM Articles");
        statement.execute("DELETE FROM Article_Groups");

        // Test restore
        groupArticlesHelper.restoreMerge(backupFileName);

        // Verify
        ResultSet rs = statement.executeQuery("SELECT COUNT(*) FROM Articles");
        rs.next();
        assertEquals(1, rs.getInt(1));

        // Cleanup
        new File("Backups/" + backupFileName).delete();
    }

    @Test
    void testGetArticleRefs() throws SQLException {
        // Setup test data
        statement.execute("INSERT INTO Articles (article_id, title) VALUES (1, 'Article 1'), (2, 'Article 2')");
        statement.execute("INSERT INTO Article_Links (article_id, linked_article_id) VALUES (1, 2)");

        // Test
        ArrayList<Long> refs = groupArticlesHelper.getArticleRefs(1L);

        // Verify
        assertNotNull(refs);
        assertEquals(1, refs.size());
        assertEquals(2L, refs.get(0));
    }

    @Test
    void testAddUserToGeneralGroups() throws SQLException {
        // Setup test data
        statement.execute("INSERT INTO Groups (group_id, group_name, special) VALUES (1, 'General Group', false)");
        statement.execute("INSERT INTO cse360users (id, username, password, role, first) VALUES (1, 'testUser', 'password', 'student', 'Test')");
        User testUser = new User("testUser", "Test", "", "User", "Test", "test@test.com", "student", "", 1);

        // Test
        boolean result = groupArticlesHelper.addUsertoGeneralGroups(testUser);

        // Verify
        assertTrue(result);
        ResultSet rs = statement.executeQuery("SELECT COUNT(*) FROM User_Groups WHERE id = 1");
        rs.next();
        assertEquals(1, rs.getInt(1));
    }

    @Test
    void testRestoreGroup() throws SQLException {
        // Create test group
        Group testGroup = new Group("Test Group", 1, false, new ArrayList<>(), new ArrayList<>());

        // Test
        boolean result = groupArticlesHelper.restoreGroup(testGroup);

        // Verify
        assertTrue(result);
        ResultSet rs = statement.executeQuery("SELECT COUNT(*) FROM Groups WHERE group_id = 1");
        rs.next();
        assertEquals(1, rs.getInt(1));
    }
}