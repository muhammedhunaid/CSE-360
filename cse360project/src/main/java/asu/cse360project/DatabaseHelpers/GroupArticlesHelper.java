package asu.cse360project.DatabaseHelpers;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.stream.Collectors;

import asu.cse360project.Article;
import asu.cse360project.EncryptionHelpers.EncryptionHelper;
import asu.cse360project.Group;
import asu.cse360project.Singleton;
import asu.cse360project.User;
import asu.cse360project.backup_container;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

// import org.junit.jupiter.api.Test;
// import static org.junit.jupiter.api.Assertions.*;

/**
 * This class provides methods to interact with the database for Group and Article related operations.
 */
public class GroupArticlesHelper{	

    public  Singleton data = Singleton.getInstance();
    private Connection connection = null; // Connection to the database
    private Statement statement = null; // Statement for executing SQL queries
    
    //Declare the encryptionHelper object whcih will help us encrypt and decrypt objects
    private EncryptionHelper encryptionHelper;

    /**
     * Constructor for GroupArticlesHelper class.
     * @param connection The connection to the database.
     * @param statement The statement for executing SQL queries.
     * @param encryptionHelper The encryption helper for encrypting/decrypting objects.
     * @param data The singleton instance for shared data.
     * @throws SQLException If an SQL exception occurs.
     */
    public GroupArticlesHelper(Connection connection, Statement statement, EncryptionHelper encryptionHelper) throws SQLException{
        data = Singleton.getInstance();
        this.connection = connection;
        this.statement = statement;
        this.encryptionHelper = encryptionHelper;
        this.data = data;
    }

    /**
     * Checks if the Groups database is empty.
     * @return True if the database is empty, false otherwise.
     * @throws SQLException If an SQL exception occurs.
     */
    public boolean isGroupsDatabaseEmpty() throws SQLException {
        String query = "SELECT COUNT(*) AS count FROM Groups"; // SQL query to count the number of users
        ResultSet resultSet = statement.executeQuery(query); // Execute the query and store the result
        if (resultSet.next()) { // Move to the first row of the result
            return resultSet.getInt("count") == 0; // Return true if there are no users (count == 0)
        }
        return true; // Return true if the result set is empty
    }

    /**
     * Creates the Articles table in the database.
     * @throws SQLException If an SQL exception occurs.
     */
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

    /**
     * Creates the Groups table in the database.
     * @throws SQLException If an SQL exception occurs.
     */
    public void createGroupsTable() throws SQLException {
        String createGroupsTableSQL = 
            "CREATE TABLE IF NOT EXISTS Groups (" +
                "group_id INT PRIMARY KEY AUTO_INCREMENT," +
                "special BOOLEAN NOT NULL, " + 
                "group_name VARCHAR(255) UNIQUE NOT NULL)";
        statement.execute(createGroupsTableSQL);
    }

    /**
     * Creates the Article_Groups table in the database.
     * @throws SQLException If an SQL exception occurs.
     */
    public void createArticleGroupsTable() throws SQLException {
        String createArticleGroupsTableSQL =
            "CREATE TABLE IF NOT EXISTS Article_Groups (" +
                "article_id BIGINT," +
                "group_id INT," +
                "PRIMARY KEY (article_id, group_id)," +
                "FOREIGN KEY (article_id) REFERENCES Articles(article_id) ON DELETE CASCADE," +
                "FOREIGN KEY (group_id) REFERENCES Groups(group_id) ON DELETE CASCADE)";
        statement.execute(createArticleGroupsTableSQL);
    }

    public void createUserGroupsTable() throws SQLException {
        String createArticleGroupsTableSQL =
            "CREATE TABLE IF NOT EXISTS User_Groups (" +
                "id INT," +
                "group_id INT," +
                "admin BOOLEAN NOT NULL, " + 
                "PRIMARY KEY (id, group_id)," +
                "FOREIGN KEY (id) REFERENCES cse360users(id) ON DELETE CASCADE," +
                "FOREIGN KEY (group_id) REFERENCES Groups(group_id) ON DELETE CASCADE)";
        statement.execute(createArticleGroupsTableSQL);
    }

    /**
     * Creates the Article_Links table in the database.
     * @throws SQLException If an SQL exception occurs.
     */
    public void createArticleLinksTable() throws SQLException {
        String createArticleLinksTableSQL = 
            "CREATE TABLE IF NOT EXISTS Article_Links (" +
                "article_id BIGINT," +
                "linked_article_id BIGINT," +
                "PRIMARY KEY (article_id, linked_article_id)," +
                "FOREIGN KEY (article_id) REFERENCES Articles(article_id) ON DELETE CASCADE," +
                "FOREIGN KEY (linked_article_id) REFERENCES Articles(article_id) ON DELETE CASCADE)";
        statement.execute(createArticleLinksTableSQL);
    }

    /**
     * Creates all the tables in the database.
     * @throws SQLException If an SQL exception occurs.
     */
    public void createAllTables() throws SQLException {
        createArticlesTable();
        createGroupsTable();
        createArticleGroupsTable();
        createArticleLinksTable();
        createUserGroupsTable();
    }

    //Get group object from database by group id
    public Group getGroup(int group_id) throws SQLException {
        String query = "SELECT * FROM Groups WHERE group_id = ?;";
        Group group = null;
        PreparedStatement pstmt = connection.prepareStatement(query);

        pstmt.setInt(1, group_id);
        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                group = getGroup(rs); // Create a Group object with the retrieved data
                return group;
            } else {
                System.out.println("No group found with name: " + group_id);
                return group;
            }
        } catch (Exception e) {
            return null; //dont return anything if exception
        }
    }

    //Get group object from database by group name
    public Group getGroup(String group_name) throws SQLException {
        String query = "SELECT * FROM Groups WHERE group_name = ?;";
        PreparedStatement pstmt = connection.prepareStatement(query);

        pstmt.setString(1, group_name);
        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return getGroup(rs); // Create a Group object with the retrieved data
            } else {
                System.out.println("No group found with name: " + group_name); 
                return null;
                //return null if not found
            }
        } catch (Exception e) {
            return null; //dont return anything if exception
        }
    }

    // Method to get all groups from the database and add to an ObservableList
    public ObservableList<Group> getAllGroups() throws SQLException {
        ObservableList<Group> groups = FXCollections.observableArrayList();
        String query = "SELECT * FROM Groups;";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                groups.add(getGroup(rs)); //add groups found to list
            }
        } catch (Exception e) {
            return null; //dont return anything if exception
        }
        return groups;
    }

     // Method to get general groups from the database and add to an ObservableList
     public ObservableList<Group> getAllGeneralGroups() throws SQLException {
        ObservableList<Group> groups = FXCollections.observableArrayList();
        String query = "SELECT * FROM Groups WHERE special = ?;";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setBoolean(1, false);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                groups.add(getGroup(rs)); //add groups to list
            }
        } catch(Exception e) {
            return null; //dont return anything if exception
        }
        return groups;
    }

    //method to get all special groups a user belongs to
    public ObservableList<Group> getAllSpecialGroups(int user_id) throws SQLException {
        ObservableList<Group> groups = FXCollections.observableArrayList();

        String query = 
            "SELECT * " + 
            "FROM Groups g " + 
            "JOIN User_Groups ag ON g.group_id = ag.group_id " +
            "JOIN cse360users a ON ag.id = a.id " +
            "WHERE a.id = ?;";
        
        PreparedStatement stmt = connection.prepareStatement(query);            
        stmt.setLong(1, user_id);

        try (ResultSet rs = stmt.executeQuery()) {
            // Iterate through the result set and create Group objects
            while (rs.next()) {
                Group g = getGroup(rs);
                if (g.getSpecial()) { //add identifier to name if special access group
                    g.setName(g.getName() + " *SAG*");
                }
                groups.add(g);
            }
        } catch (Exception e) {
            return null; //dont return anything if exception 
        }
        return groups;
    }

    //get list of group IDs user can access
    public ArrayList<Integer> getAllSpecialGroupIds(int user_id) throws SQLException {
        ArrayList<Integer> groups = new ArrayList<>();

        String query = 
            "SELECT g.group_id " + 
            "FROM Groups g " + 
            "JOIN User_Groups ag ON g.group_id = ag.group_id " +
            "JOIN cse360users a ON ag.id = a.id " +
            "WHERE a.id = ?;";
        
        PreparedStatement stmt = connection.prepareStatement(query);            
        stmt.setInt(1, user_id);

        try (ResultSet rs = stmt.executeQuery()) {
            // Iterate through the result set and create Group objects
            while (rs.next()) {
                int group_id = rs.getInt("group_id");
                groups.add(group_id);
            }
            return groups;
        } catch (Exception e) {
            return null; //dont return anything if exception 
        }
    }

    //Helper method that converts group result set to group object
    private Group getGroup(ResultSet rs) throws SQLException{
        String g_name = rs.getString("group_name");
        int g_id = rs.getInt("group_id");
        Boolean special = rs.getBoolean("special");
        ObservableList<User> group_admins = ListSAGUsers(g_id, true);
        ObservableList<User> group_viewers = ListSAGUsers(g_id, false);
        return new Group(g_name, g_id, special, new ArrayList<>(group_admins), new ArrayList<>(group_viewers));
    }

    //Delete group by group_id
    public boolean deleteGroup(int groupId) throws SQLException {
        String deleteGroupSQL = "DELETE FROM Groups WHERE group_id = ?;";

        // Delete from the group article association table
        try (PreparedStatement pstmt = connection.prepareStatement(deleteGroupSQL)) {
            pstmt.setInt(1, groupId);
            pstmt.executeUpdate();
            return true; //return true if deleted successfully 
        } catch (Exception e) {
            return false; //dont return anything if exception 
        }
    }

    //return observable list of all articles in db
    public ObservableList<Article> listAllArticles() throws SQLException {
        ObservableList<Article> articles = FXCollections.observableArrayList();
        String query = "SELECT * FROM Articles;";

        try (PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            // Retrieve each row and populate the Article object
            while (rs.next()) {
                articles.add(getArticle(rs));
            }
        } catch (SQLException e) {
            return null; //dont return anything if exception 
        }
        return articles;
    }


    //list all articles accossiated to group by group_id
    public ObservableList<Article> ListArticles(int group_id) throws SQLException {
        ObservableList<Article> articles = FXCollections.observableArrayList();

        String query = 
            "SELECT * " + 
            "FROM Articles a " + 
            "JOIN Article_Groups ag ON a.article_id = ag.article_id " +
            "JOIN Groups g ON ag.group_id = g.group_id " +
            "WHERE g.group_id = ?;";

            PreparedStatement stmt = connection.prepareStatement(query);            
            stmt.setInt(1, group_id);

            try (ResultSet rs = stmt.executeQuery()) {
                // Iterate through the result set and create Article objects
                while (rs.next()) {
                    articles.add(getArticle(rs));
                }
            } catch (Exception e) {
                return null; //dont return anything if exception 
            }

        return articles;
    }

    // Method to search articles based on group IDs, search level, keywords, user ID, and role
    public ObservableList<Article> searchArticles(ArrayList<Integer> group_ids, String search_level, String search_keywords, int user_id, String role) throws SQLException {
        // ObservableList to store the result set of articles
        ObservableList<Article> articles = FXCollections.observableArrayList();

        // If the provided group IDs list is empty
        if (group_ids.isEmpty()) {
            // Retrieve all special group IDs associated with the user
            group_ids = getAllSpecialGroupIds(user_id);

            // If no groups are found, return an empty list
            if (group_ids.isEmpty()) {
                return articles;
            }

            // If the user is not a student, include ungrouped articles in the search results
            if (!role.equals("student")) {
                articles.addAll(ListAllUnGroupedArticles(search_level, search_keywords));
            }
        }

        // If the group IDs contain '0', include ungrouped articles in the search results
        if (group_ids.contains(0)) {
            articles.addAll(ListAllUnGroupedArticles(search_level, search_keywords));
        }

        // Construct the SQL query to retrieve articles belonging to specified groups
        String query = 
            "SELECT * " +
            "FROM Articles a " +
            "JOIN Article_Groups ag ON a.article_id = ag.article_id " +
            "JOIN Groups g ON ag.group_id = g.group_id " +
            "WHERE g.group_id IN ";

        // Build a parameterized list of group IDs for the query
        String values = "(";
        for (int i = 0; i < group_ids.size() - 1; i++) {
            values += "?,";
        }
        values += "?)";
        query += values;

        // Add additional conditions to the query for search level if it's not 'all'
        if (!search_level.equals("all")) {
            query += " AND (a.level = ?)";
        }

        // Add conditions for searching by keywords if provided
        if (!search_keywords.isEmpty()) {
            query += " AND (a.title LIKE ? OR a.authors LIKE ? OR a.abstract LIKE ?)";
        }

        // Prepare the SQL statement with the constructed query
        PreparedStatement stmt = connection.prepareStatement(query);

        // Set the group IDs as parameters in the query
        int param = 1;
        for (int i : group_ids) {
            stmt.setInt(param, i);
            param++;
        }

        // Set the search level parameter if applicable
        if (!search_level.equals("all")) {
            stmt.setString(param, search_level);
            param++;
        }

        // Set the search keyword parameters if provided
        if (!search_keywords.isEmpty()) {
            stmt.setString(param, "%" + search_keywords + "%"); param++;
            stmt.setString(param, "%" + search_keywords + "%"); param++;
            stmt.setString(param, "%" + search_keywords + "%");
        }

        // Execute the query and process the results
        try (ResultSet rs = stmt.executeQuery()) {
            // Iterate through the result set and create Article objects for each row
            while (rs.next()) {
                articles.add(getArticle(rs));
            }
        } catch (Exception e) {
            return null; //dont return anything if exception 
        }

        // Return the list of articles that match the search criteria
        return articles;
    }

    //helper function for creating article object from result set
    private Article getArticle(ResultSet rs) throws SQLException{
        // get columns from result set
        Long id = rs.getLong("article_id");
        String title = rs.getString("title");
        String authors = rs.getString("authors");
        String abstractTxt = rs.getString("abstract");
        String body = rs.getString("body");
        String keywords = rs.getString("keywords");
        String level = rs.getString("level");
        String permissions = rs.getString("permissions");
        //get links
        ArrayList<Integer> groups = getArticleGroupIds(id); //article group ids
        ArrayList<Long> refrences = getArticleRefs(id); //articles refrences
        ArrayList<String> groups_names = getArticleGroupNames(id); //articles groups

        return new Article(title, authors, abstractTxt, keywords, body, id, level, groups, refrences, permissions, groups_names);
    }
    

    // Method to retrieve articles from multiple groups and return them as an ObservableList
    public ObservableList<Article> ListMultipleGroupsArticles(ArrayList<Integer> groups) throws SQLException {
        ObservableList<Article> articles = FXCollections.observableArrayList();

        // If the groups list contains -1, retrieve and return all articles
        if (groups.contains(-1)) {
            articles.addAll(listAllArticles());
            return articles; // Return immediately as -1 signifies retrieving all articles
        }

        // If the groups list contains 0, add all ungrouped articles to the result
        if (groups.contains(0)) {
            articles.addAll(ListAllUnGroupedArticles("all", ""));
        }

        // For each group ID in the list, retrieve articles and add them to the result
        for (Integer grp_id : groups) {
            // Use the addUnique method to avoid duplicate entries in the result list
            articles = addUnique(articles, ListArticles(grp_id));
        }

        // Return the final list of articles
        return articles;
    }


    // Method to list all ungrouped articles based on optional search level and keywords
    private ObservableList<Article> ListAllUnGroupedArticles(String search_level, String search_keywords) throws SQLException {
        ObservableList<Article> articles = FXCollections.observableArrayList();
        String query = 
            "SELECT a.* " + 
            "FROM Articles a " +
            "LEFT JOIN Article_Groups ag ON a.article_id = ag.article_id " +
            "WHERE ag.article_id IS NULL"; // Filters out grouped articles

        // Add condition for search level if it is not set to 'all'
        if (!search_level.equals("all")) {
            query += " AND (a.level = ?)";
        }

        // Add condition for search keywords if they are provided
        if (!search_keywords.isEmpty()) {
            query += " AND (a.title LIKE ? OR a.authors LIKE ? OR a.abstract LIKE ?)";
        }

        PreparedStatement stmt = connection.prepareStatement(query);

        // Bind parameters to the prepared statement
        int param = 1;

        // Set the search level parameter if applicable
        if (!search_level.equals("all")) {
            stmt.setString(param, search_level);
            param++;
        }

        // Set the search keyword parameters if provided
        if (!search_keywords.isEmpty()) {
            stmt.setString(param, "%" + search_keywords + "%"); param++;
            stmt.setString(param, "%" + search_keywords + "%"); param++;
            stmt.setString(param, "%" + search_keywords + "%");
        }

        // Execute the query and process the results
        try (ResultSet rs = stmt.executeQuery()) {
            // Iterate through the result set and create Article objects for each row
            while (rs.next()) {
                articles.add(getArticle(rs)); // Convert each row into an Article object and add it to the list
            }
        } catch (Exception e) {
            return null; //dont return anything if exception
        }

        // Return the list of ungrouped articles
        return articles;
    }

    // Helper method to add unique Articles to an ObservableList
    private ObservableList<Article> addUnique(ObservableList<Article> articles, ObservableList<Article> new_articles) {
        for(Article a: new_articles) {
            if (!articles.contains(a)) { // Check if the list already contains the article
                articles.add(a);
            }
        }
        return articles;
    }

    //return arraylist of group ids  that an article belongs to
    private ArrayList<Integer> getArticleGroupIds(Long id) throws SQLException {
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
            // Iterate through the result set and get group ids
            while (rs.next()) {
                groups.add(rs.getInt("group_id"));
            }
        }
        return groups;
    }

    //return array list of group objects that an article belongs to 
    public ArrayList<Group> getArticleGroups(Long id) throws SQLException {
        ArrayList<Group> groups = new ArrayList<>();
        String query = 
            "SELECT * " + 
            "FROM Groups g " + 
            "JOIN Article_Groups ag ON g.group_id = ag.group_id " +
            "JOIN articles a ON ag.article_id = a.article_id " +
            "WHERE a.article_id = ?;";
        
        PreparedStatement stmt = connection.prepareStatement(query);            
        // Set the group name parameter
        stmt.setLong(1, id);

        try (ResultSet rs = stmt.executeQuery()) {
            // Iterate through the result set and create Group objects
            while (rs.next()) {
                groups.add(getGroup(rs));
            }
        }
        return groups;
    }

    //return array list of group names that articles belong to
    private ArrayList<String> getArticleGroupNames(Long id) throws SQLException {
        ArrayList<String> groups = new ArrayList<>();
        String query = 
            "SELECT g.group_name " + 
            "FROM Groups g " + 
            "JOIN Article_Groups ag ON g.group_id = ag.group_id " +
            "JOIN articles a ON ag.article_id = a.article_id " +
            "WHERE a.article_id = ?;";
        
        PreparedStatement stmt = connection.prepareStatement(query);            
        // Set the group name parameter
        stmt.setLong(1, id);

        try (ResultSet rs = stmt.executeQuery()) {
            // Iterate through the result set and add group names
            while (rs.next()) {
                groups.add(rs.getString("group_name"));
            }
        }
        return groups;
    }

    //return arraylist of articles IDs that an article refrences
    public ArrayList<Long> getArticleRefs(long id) throws SQLException {
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
            // Iterate through the result set and add article refrence ids
            while (rs.next()) {
                refrences.add(rs.getLong("article_id"));
            }
        }
        return refrences;
    }

    // add article to database
    public void addArticle(String title, String abstractTxt, String keywords, String body, String level, String authors, String permissions, ArrayList<Integer> groups, ArrayList<Long> links) throws SQLException, Exception {
        String insertArticleQuery = "INSERT INTO articles (title, abstract, keywords, body, level, authors, permissions) VALUES (?, ?, ?, ?, ?, ?, ?)";
    
        try (PreparedStatement articleStmt = connection.prepareStatement(insertArticleQuery, Statement.RETURN_GENERATED_KEYS)) {

            // Insert the article
            articleStmt.setString(1, title);
            articleStmt.setString(2, abstractTxt);
            articleStmt.setString(3, keywords);
            //encrypt before setting param
            articleStmt.setString(4, encryptionHelper.encrypt(body));
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

            //use ID to set group and article associations in db
           linkGroups(articleId, groups);
           linkArticles(articleId, links);
        }
    }

    //add an article with only an id - used for restore operations
    public void addDummyArticle(Long article_id) throws SQLException
    {
        String insertArticleQuery = "INSERT INTO articles (article_id) VALUES (?)";

        try (PreparedStatement articleStmt = connection.prepareStatement(insertArticleQuery)) {
            articleStmt.setLong(1, article_id);
            articleStmt.executeUpdate();
        }
    }

    // Method to make links between an article and a list of other articles in the database.
private void linkArticles(long articleId, ArrayList<Long> links) throws SQLException {
    String linkArticlesQuery = "INSERT INTO Article_Links (article_id, linked_article_id) VALUES (?, ?)";

    // Loop through each article ID in the list of links.
    for (long link : links) {
        // Check if the article to be linked exists in the database.
        if (!articleExists(link)) {
            // If the article does not exist, create a dummy entry for it in the database.
            addDummyArticle(link);
        }

        PreparedStatement linkStmt = connection.prepareStatement(linkArticlesQuery);
        linkStmt.setLong(1, articleId);
        linkStmt.setLong(2, link);

        // insert the link into the 'Article_Links' table.
        linkStmt.executeUpdate();
    }
}


    // Method to associate an article with a list of groups in the database.
    private void linkGroups(long articleId, ArrayList<Integer> groups) throws SQLException {
        String linkArticleGroupQuery = "INSERT INTO article_groups (article_id, group_id) VALUES (?, ?)";

        for (int group : groups) {
            PreparedStatement linkStmt = connection.prepareStatement(linkArticleGroupQuery);
            linkStmt.setLong(1, articleId); // Set article ID.
            linkStmt.setInt(2, group);      // Set group ID.
            linkStmt.executeUpdate();       // Insert the link into the database.
        }
    }

    // Method to update an article's details and its associations with groups and linked articles.
    public void updateArticle(Long id, String title, String abstractTxt, String keywords, String body, String level, 
                            String authors, String permissions, ArrayList<Integer> groups, ArrayList<Long> links) 
                            throws SQLException, Exception { 
        String sql = "UPDATE articles SET title = ?, abstract = ?, keywords = ?, body = ?, level = ?, authors = ?, permissions = ? WHERE article_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            // Set updated article details.
            pstmt.setString(1, title);
            pstmt.setString(2, abstractTxt);
            pstmt.setString(3, keywords);
            pstmt.setString(4, encryptionHelper.encrypt(body)); // Encrypt the body before updating.
            pstmt.setString(5, level);
            pstmt.setString(6, authors);
            pstmt.setString(7, permissions);
            pstmt.setLong(8, id);
            pstmt.executeUpdate(); // Update the article in the database.

            // Remove existing associations.
            removeArticleLinks(id);
            removeGroupLinks(id);

            // Add new associations.
            linkGroups(id, groups);
            linkArticles(id, links); 
        }
    }


    // Removes all group associations for a given article.
    private void removeGroupLinks(Long id) throws SQLException {
        String deleteLinksSQL = "DELETE FROM Article_Groups WHERE article_id = ?";
        try (PreparedStatement pstmtLinks = connection.prepareStatement(deleteLinksSQL)) {
            pstmtLinks.setLong(1, id); 
            pstmtLinks.executeUpdate(); // Delete the group links.
        }
    }

    // Removes all article-to-article links for a given article.
    private void removeArticleLinks(Long id) throws SQLException {
        String deleteLinksSQL = "DELETE FROM Article_Links WHERE article_id = ?";
        try (PreparedStatement pstmtLinks = connection.prepareStatement(deleteLinksSQL)) {
            pstmtLinks.setLong(1, id);
            pstmtLinks.executeUpdate(); // Delete the article links.
        }
    }

    // Deletes an article from the database.
    public void deleteArticle(Long id) throws SQLException {
        String deleteLinksSQL = "DELETE FROM Articles WHERE article_id = ?";
        try (PreparedStatement pstmtLinks = connection.prepareStatement(deleteLinksSQL)) {
            pstmtLinks.setLong(1, id);
            pstmtLinks.executeUpdate(); // Delete the article.
        }
    }

    // Backs up articles belonging to specified groups into a file and updates user backup records.
    public boolean backup(ArrayList<Integer> groups, String file_name, User user) throws SQLException {
        ObservableList<Article> observableArticlesList = ListMultipleGroupsArticles(groups);
        ArrayList<Article> articleList = new ArrayList<>(observableArticlesList);
        ArrayList<Group> articleGroups = new ArrayList<>();

        for (Article a : articleList) {
            articleGroups.addAll(getArticleGroups(a.getId())); // Collect groups for each article.
        }
        articleGroups.stream().distinct().collect(Collectors.toList()); // Remove duplicate groups.

        System.out.println("Working Directory = " + System.getProperty("user.dir")); // Log current working directory.

        // Write articles and groups to a file and update user backup records.
        if (writeArticlesToFile(new backup_container(articleList, articleGroups), file_name)) {
            data.user_db.updateBackupFiles(user.getUsername(), file_name);
            return true;
        }else{
            return false;
        }
    }

    // Restores articles and groups from a backup file, merging articles without overwriting existing ones.
    public void restoreMerge(String file_name) throws Exception {
        backup_container backup = readArticlesFromFile(file_name);

        for (Group g : backup.groups) {
            restoreGroup(g); // Restore groups from backup.
        }

        for (Article a : backup.articles) {
            if (!articleExists(a.getId())) {
                // Add articles that don't already exist.
                addArticle(a.getId(), a.getTitle(), a.getAbstractText(), a.getKeywords(), a.getBody(), 
                        a.getLevel(), a.getAuthors(), a.getPermissions(), a.getGroups(), a.getReferences());
            }
        }
    }

    // Restores articles and groups from a backup file, updating existing articles and adding new ones.
    public void restore(String file_name) throws Exception {
        backup_container backup = readArticlesFromFile(file_name);

        for (Group g : backup.groups) {
            restoreGroup(g); // Restore groups from backup.
        }

        for (Article a : backup.articles) {
            if (articleExists(a.getId())) {
                // Update existing articles.
                updateArticle(a.getId(), a.getTitle(), a.getAbstractText(), a.getKeywords(), a.getBody(), 
                            a.getLevel(), a.getAuthors(), a.getPermissions(), a.getGroups(), a.getReferences());
            } else {
                // Add new articles.
                addArticle(a.getId(), a.getTitle(), a.getAbstractText(), a.getKeywords(), a.getBody(), 
                        a.getLevel(), a.getAuthors(), a.getPermissions(), a.getGroups(), a.getReferences());
            }
        }
    }

    // Retrieves the names of groups based on their IDs.
    public ArrayList<String> getGroupNames(ArrayList<Integer> group_ids) throws SQLException {
        ArrayList<String> group_names = new ArrayList<>(0);

        // If no group IDs are provided, return a default "All" group.
        if (group_ids.size() == 0) {
            group_names.add("All");
            return group_names;
        }

        // Iterate through the group IDs to get their names.
        for (int gid : group_ids) {
            String name;
            if (gid == 0) {
                name = ("Ungrouped"); // Handle the special case for ungrouped articles.
            } else {
                name = getGroup(gid).getName(); // Fetch the name of the group by ID.
            }
            group_names.add(name);
        }
        return group_names;
    }

    // Writes a backup container object to a file.
    public boolean writeArticlesToFile(backup_container backup, String fileName) {
        try (FileOutputStream fos = new FileOutputStream("Backups/" + fileName);
            ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(backup); // Serialize the backup object to the file.
            System.out.println("Articles have been written to " + fileName);
            return true; // Return true if writing is successful.
        } catch (IOException e) {
            return false; // Return false if an exception occurs.
        }
    }

    // Reads a backup container object from a file.
    public backup_container readArticlesFromFile(String fileName) {
        backup_container contents = null;
        try (FileInputStream fis = new FileInputStream("Backups/" + fileName);
            ObjectInputStream ois = new ObjectInputStream(fis)) {
            
            contents = (backup_container) ois.readObject(); // Deserialize the backup object from the file.
            System.out.println("Articles have been read from " + fileName);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return contents; // Return backup container.
    }


    //check if article with articleId exists in database
    public boolean articleExists(Long articleId) {
        String query = "SELECT EXISTS (SELECT 1 FROM Articles WHERE article_id = ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setLong(1, articleId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getBoolean(1); // Returns true if exists, false otherwise
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Default return value if there was an exception
    }

     //check if group with group_id exists in database
    public boolean groupExists(int group_id) {
        String query = "SELECT EXISTS (SELECT 1 FROM Groups WHERE group_id = ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, group_id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getBoolean(1); // Returns true if exists, false otherwise
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Default return value if there was an exception
    }

    // Adds a new article to the database with specified attributes.
    public boolean addArticle(Long articleId, String title, String abstractTxt, String keywords, String body, String level, String authors, String permissions, ArrayList<Integer> groups, ArrayList<Long> links) throws SQLException, Exception {
        // Check if the article already exists.
        if (articleExists(articleId)) {
            return false;
        }

        String insertArticleQuery = "INSERT INTO Articles (article_id, title, abstract, keywords, body, level, authors, permissions) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement articleStmt = connection.prepareStatement(insertArticleQuery, Statement.RETURN_GENERATED_KEYS)) {
            // Recheck and update if the article exists.
            if (articleExists(articleId)) {
                updateArticle(articleId, title, abstractTxt, keywords, body, level, authors, permissions, groups, links);
            }

            // Insert the new article into the database.
            articleStmt.setLong(1, articleId);
            articleStmt.setString(2, title);
            articleStmt.setString(3, abstractTxt);
            articleStmt.setString(4, keywords);
            articleStmt.setString(5, encryptionHelper.encrypt(body)); // Encrypt the article body before storing.
            articleStmt.setString(6, level);
            articleStmt.setString(7, authors);
            articleStmt.setString(8, permissions);
            articleStmt.executeUpdate();

            // Link the article to groups and other articles.
            linkGroups(articleId, groups);
            linkArticles(articleId, links);

            return true; // Return true if the article is successfully added.
        }
    }

    // Links a user to a group with optional admin privileges.
    public Boolean linkSAG(int user, int group_id, boolean admin) throws SQLException {
        // Check if the user is already linked to the group.
        if (isUserLinked(group_id, user)) {
            return false;
        }

        // Ensure the user exists in the database.
        if (!data.user_db.userExists(user)) {
            return false;
        }

        String linkSAGQuery = "INSERT INTO User_Groups (id, group_id, admin) VALUES (?, ?, ?)";
        try (PreparedStatement linkStmt = connection.prepareStatement(linkSAGQuery)) {
            // Insert the user-group association into the database.
            linkStmt.setInt(1, user);
            linkStmt.setInt(2, group_id);
            linkStmt.setBoolean(3, admin);
            linkStmt.executeUpdate();
            return true; // Return true if the link is successfully created.
        } catch (SQLException e) {
            return false; // Return false if an exception occurs.
        }
    }

    // Deletes all user associations with a group based on the user ID.
    public Boolean deleteSAGUsers(int id, int group_id) throws SQLException {
        String deleteGroupSQL = "DELETE FROM User_Groups WHERE id = ? AND group_id = ?;";

        try (PreparedStatement pstmt = connection.prepareStatement(deleteGroupSQL)) {
            // Execute the delete operation for the specified user ID.
            pstmt.setInt(1, id);
            pstmt.setInt(2, group_id);
            pstmt.executeUpdate();
            System.out.println("delted!!!!!");
            return true; // Return true if deletion is successful.
        } catch (SQLException e) {
            return false; // Return false if an exception occurs.
        }
    }


    // Lists all users in a specific group who have admin or non-admin roles.
    public ObservableList<User> ListSAGUsers(int group_id, boolean admin) throws SQLException {
        ObservableList<User> users = FXCollections.observableArrayList();

        String query =
            "SELECT * " + 
            "FROM cse360users a " + 
            "JOIN User_Groups ag ON a.id = ag.id " + 
            "JOIN Groups g ON ag.group_id = g.group_id " + 
            "WHERE g.group_id = ? AND ag.admin = ?;";

        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setInt(1, group_id);
        stmt.setBoolean(2, admin);

        try (ResultSet rs = stmt.executeQuery()) {
            // Iterate through the result set and fetch user details.
            while (rs.next()) {
                String username = rs.getString("username");
                users.add(data.user_db.getUser(username));
            }
        } catch (SQLException e) {
            return null; // Return null if an exception occurs.
        }
        return users; // Return the list of users.
    }

    // Lists all users not associated with a specific group.
    public ObservableList<User> ListUsersNotInGroup(int group_id) throws SQLException {
        ObservableList<User> users = FXCollections.observableArrayList(); // Create an observable list for users.
        data = Singleton.getInstance(); // Access the Singleton instance for shared data.

        String query = 
            "SELECT * " + 
            "FROM cse360users a " + 
            "LEFT JOIN User_Groups ag ON a.id = ag.id AND ag.group_id = ? " +
            "WHERE ag.group_id IS NULL;";

        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setInt(1, group_id);

        try (ResultSet rs = stmt.executeQuery()) {
            // Iterate through the result set and fetch user details.
            while (rs.next()) {
                String username = rs.getString("username");
                users.add(data.user_db.getUser(username)); // Retrieve the User object and add it to the list.
            }
        } catch (SQLException e) {
            return null; // Return null if an exception occurs.
        }
        return users; // Return the list of users.
    }

    // Checks if a group with the specified name exists in the database.
    public boolean doesGroupExist(String groupName) {
        String query = "SELECT COUNT(*) FROM groups WHERE group_name = ?"; // Query to count groups with the given name.

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, groupName); // Set the group name parameter.
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1); // Fetch the count from the result set.
                    return count > 0; // Return true if the count is greater than 0 (group exists).
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Print the exception stack trace.
            return false; // Return false if an exception occurs.
        }
        return false; // Return false if the group does not exist.
    }

   // Checks if a user is linked to a specific group.
    public boolean isUserLinked(int group_id, int user_id) {
        String query = "SELECT COUNT(*) FROM User_Groups WHERE (group_id = ? AND id = ?)";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, group_id);
            statement.setInt(2, user_id); 
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    return count > 0; 
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Return false if an exception occurs.
        }
        return false; // Return false if no link is found.
    }

    // Creates a new group and optionally links a user as an admin.
    public boolean createGroup(String group_name, boolean special, int user_id) throws SQLException {
        // Check if the group already exists.
        if (doesGroupExist(group_name)) {
            return false; // Return false if the group exists.
        }

        String insertGroupSQL = "INSERT INTO Groups (group_name, special) VALUES (?, ?);";
        try (PreparedStatement pstmt = connection.prepareStatement(insertGroupSQL)) {
            pstmt.setString(1, group_name); 
            pstmt.setBoolean(2, special);
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) { // Check if the insertion was successful.
                Group new_group = getGroup(group_name); // Retrieve the created group object.
                
                if (!special) {
                    return addGeneralGroupUsers(new_group.getId()); // Link general users if not a special group.
                } else {
                    return linkSAG(user_id, new_group.getId(), true); // Link the user as an admin for special groups.
                }
            } else {
                return false; // Return false if the insertion failed.
            }
        }
    }

    // Restores a group and its associated users from a backup.
    public boolean restoreGroup(Group group) throws SQLException {
        // Check if the group already exists.
        if (doesGroupExist(group.getName())) {
            return false; // Return false if the group exists.
        }

        String insertGroupSQL = "INSERT INTO Groups (group_id, group_name, special) VALUES (?, ?, ?);";
        try (PreparedStatement pstmt = connection.prepareStatement(insertGroupSQL)) {
            pstmt.setInt(1, group.getId()); 
            pstmt.setString(2, group.getName()); 
            pstmt.setBoolean(3, group.getSpecial()); 
            int rowsAffected = pstmt.executeUpdate(); 
            
            if (rowsAffected > 0) { // Check if the insertion was successful.
                if (!group.getSpecial()) {
                    return addGeneralGroupUsers(group.getId()); // Link general users if not a special group.
                } else {
                    // Link admin users to the special group.
                    for (User i : group.getAdmin_users()) {
                        linkSAG(i.getId(), group.getId(), true);
                    }

                    // Link viewer users to the special group.
                    for (User i : group.getViewer_users()) {
                        linkSAG(i.getId(), group.getId(), false);
                    }
                    return true; // Return true after successful linking.
                }
            } else {
                return false; // Return false if the insertion failed.
            }
        }
    }

    // Adds all general users to the specified group based on their roles.
    public boolean addGeneralGroupUsers(int group_id) {
        String query = "SELECT * FROM cse360users";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String role = rs.getString("role");
                
                // Link the user as an admin or general member based on their role.
                if (role.contains("instructor") || role.contains("admin")) {
                    linkSAG(id, group_id, true); // Link as an admin.
                } else {
                    linkSAG(id, group_id, false); // Link as a general member.
                }
            }
            return true; // Return true if all users are successfully linked.
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Return false if an error occurs.
        }
    }

    // Adds a single user to all general (non-special) groups.
    public boolean addUsertoGeneralGroups(User user) {
        String query = "SELECT * FROM Groups WHERE special = ?;";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setBoolean(1, false);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int group_id = rs.getInt("group_id");

                // Link the user to the group. If the user is not a "student-only" user, link them as an admin.
                linkSAG(user.getId(), group_id, !user.isOnlyStudent());
            }
            return true; // Return true if the user is successfully linked to all groups.
        } catch (Exception e) {
            e.printStackTrace();
            return false; // Return false if an error occurs.
        }
    }
}
