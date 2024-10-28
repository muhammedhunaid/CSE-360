package asu.cse360project.DatabaseHelpers;

import java.sql.*;

import asu.cse360project.Article;
import asu.cse360project.Group;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.ArrayList; 

public class GroupArticlesHelper {	

    private Connection connection = null; // Connection to the database
    private Statement statement = null; // Statement for executing SQL queries

    public GroupArticlesHelper(Connection connection, Statement statement) throws SQLException{
        this.connection = connection;
        this.statement = statement;
    }

    public boolean isGroupsDatabaseEmpty() throws SQLException {
        String query = "SELECT COUNT(*) AS count FROM Groups"; // SQL query to count the number of users
        ResultSet resultSet = statement.executeQuery(query); // Execute the query and store the result
        if (resultSet.next()) { // Move to the first row of the result
            return resultSet.getInt("count") == 0; // Return true if there are no users (count == 0)
        }
        return true; // Return true if the result set is empty
    }

    public void createArticlesTable() throws SQLException {
        String createArticlesTableSQL = 
            "CREATE TABLE IF NOT EXISTS Articles ( " +
                "article_id BIGINT PRIMARY KEY AUTO_INCREMENT," +
                "title TEXT, " +
                "authors TEXT, " +
                "abstract TEXT, " +
                "keywords TEXT, " +
                "level TEXT, " +
                "permissions TEXT, " + 
                "body TEXT)";
        statement.execute(createArticlesTableSQL);
    }

    public void createGroupsTable() throws SQLException {
        String createGroupsTableSQL = 
            "CREATE TABLE IF NOT EXISTS Groups (" +
                "group_id INT PRIMARY KEY AUTO_INCREMENT," +
                "group_name VARCHAR(255) UNIQUE NOT NULL)";
        statement.execute(createGroupsTableSQL);
    }

    public void createArticleGroupsTable() throws SQLException {
        String createArticleGroupsTableSQL =
            "CREATE TABLE IF NOT EXISTS Article_Groups (" +
                "article_id INT," +
                "group_id INT," +
                "PRIMARY KEY (article_id, group_id)," +
                "FOREIGN KEY (article_id) REFERENCES Articles(article_id) ON DELETE CASCADE," +
                "FOREIGN KEY (group_id) REFERENCES Groups(group_id) ON DELETE CASCADE)";
        statement.execute(createArticleGroupsTableSQL);
    }

    public void createArticleLinksTable() throws SQLException {
        String createArticleLinksTableSQL = 
            "CREATE TABLE IF NOT EXISTS Article_Links (" +
                "article_id INT," +
                "linked_article_id INT," +
                "PRIMARY KEY (article_id, linked_article_id)," +
                "FOREIGN KEY (article_id) REFERENCES Articles(article_id) ON DELETE CASCADE," +
                "FOREIGN KEY (linked_article_id) REFERENCES Articles(article_id) ON DELETE CASCADE)";
        statement.execute(createArticleLinksTableSQL);
    }

    public void createAllTables() throws SQLException {
        createArticlesTable();
        createGroupsTable();
        createArticleGroupsTable();
        createArticleLinksTable();
    }

    //add group to groups table
    public Group addGroup(String groupName) throws SQLException {
        String insertGroupSQL = "INSERT INTO Groups (group_name) VALUES (?);";
        PreparedStatement pstmt = connection.prepareStatement(insertGroupSQL);
        pstmt.setString(1, groupName);
        pstmt.executeUpdate();

        return getGroup(groupName);
    }

     private Group getGroup(String groupName) throws SQLException {
        String query = "SELECT group_id, group_name FROM Groups WHERE group_name = ?;";
        Group group = null;
        PreparedStatement pstmt = connection.prepareStatement(query);

        pstmt.setString(1, groupName);
        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                Integer id = rs.getInt("group_id");
                String name = rs.getString("group_name");
                group = new Group(name, id);  // Create a Group object with the retrieved data
                System.out.println("Group found: " + group);
            } else {
                System.out.println("No group found with name: " + groupName);
            }
        }
        return group;
    }

    // Method to get all groups from the database and add to an ObservableList
    public ObservableList<Group> getAllGroups() throws SQLException {
        ObservableList<Group> groups = FXCollections.observableArrayList();
        String query = "SELECT * FROM Groups;";
        PreparedStatement pstmt = connection.prepareStatement(query);
        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
            String groupName = rs.getString("group_name");
            Integer groupID = rs.getInt("group_id");
            groups.add(new Group(groupName, groupID));
        }
        return groups;
    }

    public void deleteGroup(int groupId) throws SQLException {
        String deleteArticleGroupSQL = "DELETE FROM Article_Groups WHERE group_id = ?;";
        String deleteGroupSQL = "DELETE FROM Groups WHERE group_id = ?;";

        // Delete from the group article association table
        try (PreparedStatement pstmtDeleteAssociations = connection.prepareStatement(deleteArticleGroupSQL)) {
            pstmtDeleteAssociations.setLong(1, groupId);
            pstmtDeleteAssociations.executeUpdate();
            System.out.println("Group removed");
        }

        // Delete the group from the Groups table
        try (PreparedStatement pstmtDeleteGroup = connection.prepareStatement(deleteGroupSQL)) {
            pstmtDeleteGroup.setInt(1, groupId);
            pstmtDeleteGroup.executeUpdate();
        }
    }

    //Method to insert a new article into the database with encrypted fields for title, authors, abstract, keywords, body, and references
public void createArticle(Article newArticle) throws Exception {

		String title = newArticle.getTitle();
		String authors = newArticle.getAuthors();
		String abstractInfo = newArticle.getAbstractText();
		String keywords = newArticle.getKeywords();
		String body = newArticle.getBody();

		//sql command for inserting the article into the table
		String insertArticle = "INSERT INTO articles (title, authors, abstract, keywords, body) VALUES (?, ?, ?, ?, ?, ?)";

		//defining the Initialization Vector for the encryptions

	    //byte[] IV = EncryptionUtils.getInitializationVector("team35".toCharArray());

		//adding the sql insert statements
		try (PreparedStatement pstmt = connection.prepareStatement(insertArticle)) {

			//encrypt the title, authors, abstract, body, referenes and then push them into the table
			pstmt.setString(1, title);
			pstmt.setString(2, authors);
			pstmt.setString(3, abstractInfo);
			pstmt.setString(4, keywords);
			pstmt.setString(5, body);

			//executing sql statement for insertion of the article
			pstmt.executeUpdate();

			//clear the article from the system memory after storing the information in DB
			newArticle.clearArticle();

		} catch (Error e) {
			System.out.println("error: " + e.getMessage()); // Log SQL error
			throw e; // Rethrow the exception if necessary
		}
	}

    public ObservableList<Article> ListArticles(int group_id) throws SQLException {
        ObservableList<Article> articles =  FXCollections.observableArrayList();

        String query = 
            "SELECT * " + 
            "FROM Articles a " + 
            "JOIN Article_Groups ag ON a.article_id = ag.article_id " +
            "JOIN Groups g ON ag.group_id = g.group_id " +
            "WHERE g.group_id = ?;";

            PreparedStatement stmt = connection.prepareStatement(query);            
            // Set the group name parameter
            stmt.setInt(1, group_id);

            try (ResultSet rs = stmt.executeQuery()) {
                // Iterate through the result set and create Article objects
                while (rs.next()) {
                    Long id = rs.getLong("article_id");
                    String title = rs.getString("title");
                    String authors = rs.getString("authors");
                    String abstractTxt = rs.getString("abstract");
                    String body = rs.getString("body");
                    String keywords = rs.getString("keywords");
                    String level = rs.getString("level");
                    String permissions = rs.getString("permissions");
                    ArrayList<Integer> groups = getArticleGroups(id);
                    ArrayList<Long> refrences = getArticleRefs(id);

                    Article article = new Article(title, authors, abstractTxt, keywords, body, id, level, groups, refrences, permissions);
                    articles.add(article);
                }
            }

        return articles;
    }
    
    public ObservableList<Article> ListMultipleGroupsArticles(ArrayList<Integer> group_id) throws SQLException {
        ObservableList<Article> articles =  FXCollections.observableArrayList();

        if(group_id.contains(-1))
        {
            return AddAllArticles();
        }

        for(Integer grp_id: group_id)
        {
            articles.addAll(ListArticles(grp_id));
        }
        return articles;
    }
    
    private ObservableList<Article> AddAllArticles() throws SQLException {
        ObservableList<Article> articles =  FXCollections.observableArrayList();

        String query =  "SELECT * FROM Articles";
        PreparedStatement stmt = connection.prepareStatement(query);            

        try (ResultSet rs = stmt.executeQuery()) {
            // Iterate through the result set and create Article objects
            while (rs.next()) {
                Long id = rs.getLong("article_id");
                String title = rs.getString("title");
                String authors = rs.getString("authors");
                String abstractTxt = rs.getString("abstract");
                String body = rs.getString("body");
                String keywords = rs.getString("keywords");
                String level = rs.getString("level");
                String permissions = rs.getString("permissions");
                ArrayList<Integer> groups = getArticleGroups(id);
                ArrayList<Long> refrences = getArticleRefs(id);

                Article article = new Article(title, authors, abstractTxt, keywords, body, id, level, groups, refrences, permissions);
                articles.add(article);
            }
        }

        return articles;
    }

    private ArrayList<Integer> getArticleGroups(Long id) throws SQLException {
        ArrayList<Integer> groups = new ArrayList<>();
        String query = 
            "SELECT g.group_id " + 
            "FROM Groups g " + 
            "JOIN Article_Groups ag ON g.group_id = ag.group_id " +
            "JOIN articles a ON ag.article_id = a.article_id " +
            "WHERE a.article_id = ?;";
        
        PreparedStatement stmt = connection.prepareStatement(query);            
        // Set the group name parameter
        stmt.setLong(1, id);

        try (ResultSet rs = stmt.executeQuery()) {
            // Iterate through the result set and create Article objects
            while (rs.next()) {
                groups.add(rs.getInt("group_id"));
            }
        }
        return groups;
    }

    private ArrayList<Long> getArticleRefs(long id) throws SQLException {
        ArrayList<Long> refrences = new ArrayList<>();
        String query = 
            "SELECT r.article_id " + 
            "FROM articles r " + 
            "JOIN Article_Links ar ON r.article_id = ar.linked_article_id " +
            "JOIN articles a ON ar.article_id = a.article_id " +
            "WHERE a.article_id = ?;";
        
        PreparedStatement stmt = connection.prepareStatement(query);            
        // Set the group name parameter
        stmt.setLong(1, id);

        try (ResultSet rs = stmt.executeQuery()) {
            // Iterate through the result set and create Article objects
            while (rs.next()) {
                refrences.add(rs.getLong("article_id"));
            }
        }
        return refrences;
    }

    public void addArticle(String title, String abstractTxt, String keywords, String body, String level, String authors, String permissions, ArrayList<Integer> groups, ArrayList<Long> links) throws SQLException {
    
        String insertArticleQuery = "INSERT INTO articles (title, abstract, keywords, body, level, authors, permissions) VALUES (?, ?, ?, ?, ?, ?, ?)";
    
        try (PreparedStatement articleStmt = connection.prepareStatement(insertArticleQuery, Statement.RETURN_GENERATED_KEYS)) {

            // Insert the article
            articleStmt.setString(1, title);
            articleStmt.setString(2, abstractTxt);
            articleStmt.setString(3, keywords);
            articleStmt.setString(4, body);
            articleStmt.setString(5, level);
            articleStmt.setString(6, authors);
            articleStmt.setString(7, permissions);
            articleStmt.executeUpdate();

            // Get the generated article ID
            long articleId;
            try (ResultSet generatedKeys = articleStmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    articleId = generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Failed to insert article, no ID obtained.");
                }
            }

           linkGroups(articleId, groups);
           linkArticles(articleId, links);
        }
    }

    private void linkArticles(long articleId, ArrayList<Long> links) throws SQLException {
        String linkArticlesQuery = "INSERT INTO Article_Links (article_id, linked_article_id) VALUES (?, ?)";
        

        for(long link: links)
        {
            PreparedStatement linkStmt = connection.prepareStatement(linkArticlesQuery);
            linkStmt.setLong(1, articleId);
            linkStmt.setLong(2, link);
            linkStmt.executeUpdate();
        }
    }

    private void linkGroups(long articleId,  ArrayList<Integer> groups) throws SQLException {
        String linkArticleGroupQuery = "INSERT INTO article_groups (article_id, group_id) VALUES (?, ?)";

        for(int group: groups)
        {
            PreparedStatement linkStmt = connection.prepareStatement(linkArticleGroupQuery);
            linkStmt.setLong(1, articleId);
            linkStmt.setInt(2, group);
            linkStmt.executeUpdate();
        }
    }

    public void updateArticle(Long id, String title, String abstractTxt, String keywords, String body, String level, String authors, String permissions, ArrayList<Integer> groups, ArrayList<Long> links) throws SQLException {
        String sql = "UPDATE articles SET title = ?, abstract = ?, keywords = ?, body = ?, level = ?, authors = ?, permissions = ? WHERE article_id = ?";
        PreparedStatement pstmt = connection.prepareStatement(sql);
            
            pstmt.setString(1, title);
            pstmt.setString(2, abstractTxt);
            pstmt.setString(3, keywords);
            pstmt.setString(4, body);
            pstmt.setString(5, level);
            pstmt.setString(6, authors);
            pstmt.setString(7, permissions);
            pstmt.setLong(8, id);
            pstmt.executeUpdate();

            removeArticleLinks(id);
            removeGroupLinks(id);

            linkGroups(id, groups);
            linkArticles(id, links);    
    }

    private void removeGroupLinks(Long id) throws SQLException {
        String deleteLinksSQL = "DELETE FROM Article_Groups WHERE article_id = ?";
        PreparedStatement pstmtLinks = connection.prepareStatement(deleteLinksSQL);
        pstmtLinks.setLong(1, id);  // Set the article ID
        pstmtLinks.executeUpdate();
    }

    private void removeArticleLinks(Long id) throws SQLException {
        String deleteLinksSQL = "DELETE FROM Article_Links WHERE article_id = ?";
        PreparedStatement pstmtLinks = connection.prepareStatement(deleteLinksSQL);
        pstmtLinks.setLong(1, id);  // Set the article ID
        pstmtLinks.executeUpdate();
    }

    public void deleteArticle(Long id) throws SQLException {
        String deleteLinksSQL = "DELETE FROM Articles WHERE article_id = ?";
        PreparedStatement pstmtLinks = connection.prepareStatement(deleteLinksSQL);
        pstmtLinks.setLong(1, id);  // Set the article ID
        pstmtLinks.executeUpdate();
    }
}
