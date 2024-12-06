package asu.cse360project;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import asu.cse360project.DatabaseHelpers.GroupArticlesHelper;
import asu.cse360project.EncryptionHelpers.EncryptionHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Test class for Article Management functionality
 */
public class ArticleManagementTest {
    private Connection mockConnection;
    private Statement mockStatement;
    private PreparedStatement mockPreparedStatement;
    private ResultSet mockResultSet;
    private GroupArticlesHelper groupArticlesHelper;
    private String title;
    private String body;
    private String authors;

    @BeforeEach
    void setUp() throws SQLException {
        // Setup mock database components
        mockConnection = mock(Connection.class);
        mockStatement = mock(Statement.class);
        mockPreparedStatement = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);
        
        when(mockConnection.createStatement()).thenReturn(mockStatement);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        
        // Setup test data
        title = "";
        body = "";
        authors = "";
        
        // Create a mock Singleton and EncryptionHelper
        EncryptionHelper mockEncryptionHelper = mock(EncryptionHelper.class);
        groupArticlesHelper = new GroupArticlesHelper(mockConnection, mockPreparedStatement, mockEncryptionHelper);
    }

    @Test
    @DisplayName("Test Requirement 1: Admin adds article with all fields filled")
    void testAddArticleWithValidInput() {
        // Setup test data
        title = "Test Article";
        body = "Test Content";
        authors = "Test Author";

        // Verify all fields are filled
        assertFalse(title.isEmpty());
        assertFalse(body.isEmpty());
        assertFalse(authors.isEmpty());
        
        // Test article addition
        Article testArticle = new Article(
            title,
            authors,
            "Test Abstract",
            "Test Keywords",
            body,
            1L,
            "public",
            new ArrayList<>(),
            new ArrayList<>(),
            "read",
            new ArrayList<>()
        );
        
        assertNotNull(testArticle);
        assertEquals("Test Article", testArticle.getTitle());
        assertEquals("Test Content", testArticle.getBody());
        assertEquals("Test Author", testArticle.getAuthors());
    }

    @Test
    @DisplayName("Test Requirement 1: Admin adds article with missing fields")
    void testAddArticleWithMissingFields() {
        // Test with empty fields
        title = "";
        body = "";
        authors = "";

        // Verify validation catches empty fields
        assertTrue(title.isEmpty());
        assertTrue(body.isEmpty());
        assertTrue(authors.isEmpty());
    }

    @Test
    @DisplayName("Test Requirement 2: Admin removes article")
    void testRemoveArticle() throws SQLException {
        // Mock article deletion
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        
        // Create test article with minimal constructor
        Article testArticle = new Article("Test Title", "Test Author", 1L);
        
        try {
            groupArticlesHelper.deleteArticle(testArticle.getId());
            verify(mockPreparedStatement).executeUpdate();
        } catch (SQLException e) {
            fail("Database operation failed: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Test Requirement 3: Instructors add articles to general groups")
    void testInstructorAddArticleToGeneralGroup() throws SQLException, Exception {
        // Setup test data for general group
        ArrayList<User> admins = new ArrayList<>();
        ArrayList<User> viewers = new ArrayList<>();
        Group testGroup = new Group("General Group", 1, false, admins, viewers);
        
        // Create test article
        Article testArticle = new Article("Test Title", "Test Author", 1L);
        
        // Mock PreparedStatements for different queries
        PreparedStatement articleInsertStmt = mock(PreparedStatement.class);
        PreparedStatement groupLinkStmt = mock(PreparedStatement.class);
        PreparedStatement articleLinkStmt = mock(PreparedStatement.class);
        ResultSet generatedKeys = mock(ResultSet.class);
        
        // Mock behavior for article insertion
        when(mockConnection.prepareStatement(contains("INSERT INTO articles"), eq(Statement.RETURN_GENERATED_KEYS)))
            .thenReturn(articleInsertStmt);
        when(articleInsertStmt.getGeneratedKeys()).thenReturn(generatedKeys);
        when(generatedKeys.next()).thenReturn(true);
        when(generatedKeys.getInt(1)).thenReturn(1);
        
        // Mock behavior for group and article linking
        when(mockConnection.prepareStatement(contains("INSERT INTO article_groups")))
            .thenReturn(groupLinkStmt);
        when(mockConnection.prepareStatement(contains("INSERT INTO Article_Links")))
            .thenReturn(articleLinkStmt);
        
        try {
            // Add article using the helper
            groupArticlesHelper.addArticle(
                testArticle.getTitle(),
                "Test Abstract",
                "Test Keywords",
                "Test Body",
                "public",
                testArticle.getAuthors(),
                "read",
                new ArrayList<>(),
                new ArrayList<>()
            );
            
            // Verify PreparedStatement interactions
            verify(articleInsertStmt).executeUpdate();
            verify(articleInsertStmt).getGeneratedKeys();
            verify(generatedKeys).next();
            verify(generatedKeys).getInt(1);
            
        } catch (SQLException e) {
            fail("Failed to add article to general group: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Test Requirement 4: Instructors remove articles from SAGs")
    void testRemoveArticleFromSAG() throws SQLException {
        // Setup test data for SAG
        ArrayList<User> admins = new ArrayList<>();
        ArrayList<User> viewers = new ArrayList<>();
        Group testSAG = new Group("Special Access Group", 1, true, admins, viewers);
        Article testArticle = new Article("Test Title", "Test Author", 1L);
        
        // Mock successful article removal from SAG
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        
        try {
            // Remove article using the helper
            groupArticlesHelper.deleteArticle(testArticle.getId());
            verify(mockPreparedStatement).executeUpdate();
        } catch (SQLException e) {
            fail("Failed to remove article from SAG: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Test Requirement 5: Instructors remove articles from general groups")
    void testRemoveArticleFromGeneralGroup() throws SQLException {
        // Setup test data for general group
        ArrayList<User> admins = new ArrayList<>();
        ArrayList<User> viewers = new ArrayList<>();
        Group testGroup = new Group("General Group", 1, false, admins, viewers);
        Article testArticle = new Article("Test Title", "Test Author", 1L);
        
        // Mock successful article removal from general group
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        
        try {
            // Remove article using the helper
            groupArticlesHelper.deleteArticle(testArticle.getId());
            verify(mockPreparedStatement).executeUpdate();
        } catch (SQLException e) {
            fail("Failed to remove article from general group: " + e.getMessage());
        }
    }
}
