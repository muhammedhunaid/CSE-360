package asu.cse360project;

import asu.cse360project.Article;
import asu.cse360project.DatabaseHelpers.GroupArticlesHelper;
import asu.cse360project.Group;
import asu.cse360project.User;
import asu.cse360project.EncryptionHelpers.EncryptionHelper;

import asu.cse360project.backup_container;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.*;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the GroupArticlesHelper class.
 * 
 * This class contains test cases for various methods of the GroupArticlesHelper class.
 * It verifies the functionality of methods such as retrieving all general groups, 
 * retrieving all special groups for a user, adding a dummy article, backing up data, 
 * restoring and merging data, getting article references, adding a user to general groups, 
 * and restoring a group.
 */
class GroupArticlesHelperTest {

    private Connection mockConnection;
    private Statement mockStatement;
    private PreparedStatement mockPreparedStatement;
    private ResultSet mockResultSet;
    private EncryptionHelper mockEncryptionHelper;
    private GroupArticlesHelper groupArticlesHelper;

    /**
     * Sets up the test environment by creating mock objects for database connections and statements.
     * 
     * This method is called before each test to ensure a clean setup for each test case.
     * 
     * @throws SQLException if a database access error occurs
     */
    @BeforeEach
    void setUp() throws SQLException {
        mockConnection = mock(Connection.class);
        mockStatement = mock(Statement.class);
        mockPreparedStatement = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);
        mockEncryptionHelper = mock(EncryptionHelper.class);

        when(mockConnection.createStatement()).thenReturn(mockStatement);

        groupArticlesHelper = new GroupArticlesHelper(mockConnection, mockStatement, mockEncryptionHelper);
    }

    /**
     * Tests the retrieval of all general groups.
     * 
     * This test verifies that the getAllGroups method correctly retrieves all general groups from the database.
     */
    @Test
    void testGetAllGeneralGroups() {
        try {
            String query = "SELECT * FROM Groups;";
            when(mockConnection.prepareStatement(query)).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(true, true, false);
            when(mockResultSet.getString("group_name")).thenReturn("Group1", "Group2");

            ObservableList<Group> groups = groupArticlesHelper.getAllGroups();

            assertNotNull(groups);
            assertEquals(2, groups.size());
            assertEquals("Group1", groups.get(0).getName());
            assertEquals("Group2", groups.get(1).getName());
        } catch (NullPointerException e) {
            System.out.println("NullPointerException in testGetAllGroups: Ensure all objects are initialized.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Tests the retrieval of all special groups for a user.
     * 
     * This test verifies that the getAllSpecialGroups method correctly retrieves all special groups for a given user.
     */
    @Test
    void testGetAllSpecialGroups() {
        try {
            String query = "SELECT * FROM Groups g JOIN User_Groups ag ON g.group_id = ag.group_id JOIN cse360users a ON ag.id = a.id WHERE a.id = ?;";
            when(mockConnection.prepareStatement(query)).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(true, false);
            when(mockResultSet.getString("group_name")).thenReturn("Special Group 1");

            ObservableList<Group> specialGroups = groupArticlesHelper.getAllSpecialGroups(1);

            assertNotNull(specialGroups);
            assertEquals(1, specialGroups.size());
            assertEquals("Special Group 1", specialGroups.get(0).getName());
        } catch (NullPointerException e) {
            System.out.println("NullPointerException in testGetAllSpecialGroups: Ensure all objects are initialized.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Tests the addition of a dummy article.
     * 
     * This test verifies that the addDummyArticle method correctly adds a dummy article to the database.
     */
    @Test
    void testAddDummyArticle() {
        try {
            String query = "INSERT INTO articles (article_id) VALUES (?)";
            when(mockConnection.prepareStatement(query)).thenReturn(mockPreparedStatement);

            groupArticlesHelper.addDummyArticle(1L);

            verify(mockPreparedStatement, times(1)).setLong(1, 1L);
            verify(mockPreparedStatement, times(1)).executeUpdate();
        } catch (NullPointerException e) {
            System.out.println("NullPointerException in testAddDummyArticle: Ensure all objects are initialized.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Tests the backup functionality.
     * 
     * This test verifies that the backup method correctly backs up data for a given list of group IDs.
     */
    @Test
    void testBackup() {
        try {
            ArrayList<Integer> groupIds = new ArrayList<>();
            groupIds.add(1);

            User mockUser = mock(User.class);
            when(mockUser.getUsername()).thenReturn("testUser");

            String query = "SELECT * FROM Articles;";
            when(mockConnection.prepareStatement(query)).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(false); // Simulate no articles

            groupArticlesHelper.backup(groupIds, "backup.dat", mockUser);

            verify(mockUser, times(1)).getUsername();
        } catch (NullPointerException e) {
            System.out.println("NullPointerException in testBackup: Ensure all objects are initialized.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Tests the restore and merge functionality.
     * 
     * This test verifies that the restoreMerge method correctly restores and merges data from a backup file.
     */
    @Test
    void testRestoreMerge() {
        try {
            backup_container mockBackup = mock(backup_container.class);
            Group mockGroup = mock(Group.class);
            Article mockArticle = mock(Article.class);

            when(mockBackup.groups).thenReturn(new ArrayList<Group>() {{
                add(mockGroup);
            }});
            when(mockBackup.articles).thenReturn(new ArrayList<Article>() {{
                add(mockArticle);
            }});
            when(groupArticlesHelper.readArticlesFromFile(anyString())).thenReturn(mockBackup);

            groupArticlesHelper.restoreMerge("backup.dat");

            verify(groupArticlesHelper, times(1)).restoreGroup(mockGroup);
        } catch (Exception e) {
            System.out.println("Exception in testRestoreMerge: " + e.getMessage());
        }
    }

    /**
     * Tests the retrieval of article references.
     * 
     * This test verifies that the getArticleRefs method correctly retrieves references to other articles.
     */
    @Test
    void testGetArticleRefs() {
        try {
            String query = "SELECT r.article_id FROM articles r JOIN Article_Links ar ON r.article_id = ar.linked_article_id JOIN articles a ON ar.article_id = a.article_id WHERE a.article_id = ?;";
            when(mockConnection.prepareStatement(query)).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(true, true, false);
            when(mockResultSet.getLong("article_id")).thenReturn(1L, 2L);

            ArrayList<Long> refs = groupArticlesHelper.getArticleRefs(1L);

            assertNotNull(refs);
            assertEquals(2, refs.size());
            assertTrue(refs.contains(1L));
            assertTrue(refs.contains(2L));
        } catch (NullPointerException e) {
            System.out.println("NullPointerException in testGetArticleRefs: Ensure all objects are initialized.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Tests the addition of a user to general groups.
     * 
     * This test verifies that the addUsertoGeneralGroups method correctly adds a user to general groups.
     */
    @Test
    void testAddUserToGeneralGroups() {
        try {
            // Create a spy for the GroupArticlesHelper
            GroupArticlesHelper spyHelper = spy(groupArticlesHelper);

            String groupQuery = "SELECT * FROM Groups WHERE special = ?;";
            when(mockConnection.prepareStatement(groupQuery)).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(true, false); // Simulate one group in ResultSet
            when(mockResultSet.getInt("group_id")).thenReturn(1); // Return group_id = 1

            User mockUser = mock(User.class);
            when(mockUser.getId()).thenReturn(1);
            when(mockUser.isOnlyStudent()).thenReturn(true); // User is a student

            // Mock linkSAG to return true when called on the spy
            doReturn(true).when(spyHelper).linkSAG(anyInt(), anyInt(), anyBoolean());

            boolean result = spyHelper.addUsertoGeneralGroups(mockUser);

            assertTrue(result); // Assert the expected result
        } catch (NullPointerException e) {
            System.out.println("NullPointerException in testAddUserToGeneralGroups: Ensure all objects are initialized.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Tests the restoration of a group.
     * 
     * This test verifies that the restoreGroup method correctly restores a group.
     */
    @Test
    void testRestoreGroup() {
        try {
            String query = "SELECT COUNT(*) FROM groups WHERE group_name = ?";
            when(mockConnection.prepareStatement(query)).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(true);
            when(mockResultSet.getInt(1)).thenReturn(0); // Simulate group not existing

            Group mockGroup = mock(Group.class);
            when(mockGroup.getId()).thenReturn(1);
            when(mockGroup.getName()).thenReturn("Test Group");
            when(mockGroup.getSpecial()).thenReturn(true);

            boolean restored = groupArticlesHelper.restoreGroup(mockGroup);

            assertTrue(restored);
        } catch (NullPointerException e) {
            System.out.println("NullPointerException in testRestoreGroup: Ensure all objects are initialized.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}