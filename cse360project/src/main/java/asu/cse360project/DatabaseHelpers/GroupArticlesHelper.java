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
import asu.cse360project.Group;
import asu.cse360project.Singleton;
import asu.cse360project.User;
import asu.cse360project.backup_container;
import asu.cse360project.EncryptionHelpers.EncryptionHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * This class provides methods to interact with the database for Group and Article related operations.
 */
public class GroupArticlesHelper{	

    Singleton data = new Singleton();
    private Connection connection = null; // Connection to the database
    private Statement statement = null; // Statement for executing SQL queries
    
    //Declare the encryptionHelper object whcih will help us encrypt and decrypt objects
    private EncryptionHelper encryptionHelper;

    /**
     * Constructor for GroupArticlesHelper class.
     * @param connection The connection to the database.
     * @param statement The statement for executing SQL queries.
     * @throws SQLException If an SQL exception occurs.
     */
    public GroupArticlesHelper(Connection connection, Statement statement, EncryptionHelper encryptionHelper) throws SQLException{
        this.connection = connection;
        this.statement = statement;
        this.encryptionHelper = encryptionHelper;
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
            // Iterate through the result set and create Article objects
            while (rs.next()) {
                groups.add(rs.getInt("group_id"));
            }
        }
        return groups;
    }

    private ArrayList<Group> getArticleGroups(Long id) throws SQLException {
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
            // Iterate through the result set and create Article objects
            while (rs.next()) {
                groups.add(getGroup(rs));
            }
        }
        return groups;
    }

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
            // Iterate through the result set and create Article objects
            while (rs.next()) {
                groups.add(rs.getString("group_name"));
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

           linkGroups(articleId, groups);
           linkArticles(articleId, links);
        }
    }

    private void addDummyArticle(Long article_id) throws SQLException
    {
        String insertArticleQuery = "INSERT INTO articles (article_id) VALUES (?)";

        try (PreparedStatement articleStmt = connection.prepareStatement(insertArticleQuery)) {
            articleStmt.setLong(1, article_id);
            articleStmt.executeUpdate();
        }
    }

    private void linkArticles(long articleId, ArrayList<Long> links) throws SQLException {
        String linkArticlesQuery = "INSERT INTO Article_Links (article_id, linked_article_id) VALUES (?, ?)";
        

        for(long link: links)
        {
            if(!articleExists(link))
            {
                addDummyArticle(link);
            }
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

    public void updateArticle(Long id, String title, String abstractTxt, String keywords, String body, String level, String authors, String permissions, ArrayList<Integer> groups, ArrayList<Long> links) throws SQLException, Exception {
        String sql = "UPDATE articles SET title = ?, abstract = ?, keywords = ?, body = ?, level = ?, authors = ?, permissions = ? WHERE article_id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            pstmt.setString(1, title);
            pstmt.setString(2, abstractTxt);
            pstmt.setString(3, keywords);
            //encryot before setting param
            pstmt.setString(4, encryptionHelper.encrypt(body));
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
    }

    private void removeGroupLinks(Long id) throws SQLException {
        String deleteLinksSQL = "DELETE FROM Article_Groups WHERE article_id = ?";
        try (PreparedStatement pstmtLinks = connection.prepareStatement(deleteLinksSQL)) {
            pstmtLinks.setLong(1, id);  // Set the article ID
            pstmtLinks.executeUpdate();
        }
    }

    private void removeArticleLinks(Long id) throws SQLException {
        String deleteLinksSQL = "DELETE FROM Article_Links WHERE article_id = ?";
        try (PreparedStatement pstmtLinks = connection.prepareStatement(deleteLinksSQL)) {
            pstmtLinks.setLong(1, id);  // Set the article ID
            pstmtLinks.executeUpdate();
        }
    }

    public void deleteArticle(Long id) throws SQLException {
        String deleteLinksSQL = "DELETE FROM Articles WHERE article_id = ?";
        try (PreparedStatement pstmtLinks = connection.prepareStatement(deleteLinksSQL)) {
            pstmtLinks.setLong(1, id);  // Set the article ID
            pstmtLinks.executeUpdate();
        }
    }

    public void backup(ArrayList<Integer> groups, String file_name, User user) throws SQLException {
        ObservableList<Article> observale_articles_list = ListMultipleGroupsArticles(groups);
        ArrayList<Article> article_list = new ArrayList<>(observale_articles_list);
        ArrayList<Group> article_groups = new ArrayList<>();
        for(Article a : article_list)
        {
            article_groups.addAll(getArticleGroups(a.getId()));
        }
        article_groups.stream().distinct().collect(Collectors.toList());
        System.out.println("Working Directory = " + System.getProperty("user.dir"));
        if(writeArticlesToFile(new backup_container(article_list, article_groups), file_name)) {
            data.user_db.updateBackupFiles(user.getUsername(), file_name);
        }
    }

    //TODO: Fix merge restore
    public void restoreMerge(String file_name) throws Exception {
        backup_container backup = readArticlesFromFile(file_name);

        for (Group g : backup.groups)
        {
            restoreGroup(g);
        }

        for(Article a: backup.articles) {
            if(!articleExists(a.getId()))
            {
                addArticle(a.getId(),a.getTitle(),a.getAbstractText(),a.getKeywords(),a.getBody(),a.getLevel(),a.getAuthors(),a.getPermissions(),a.getGroups(), a.getReferences());
            }
        }
    }
    
    public void restore(String file_name) throws Exception {
        backup_container backup = readArticlesFromFile(file_name);

        for (Group g : backup.groups)
        {
            restoreGroup(g);
        }

        for(Article a: backup.articles) {
            if(articleExists(a.getId()))
            {
                updateArticle(a.getId(),a.getTitle(),a.getAbstractText(),a.getKeywords(),a.getBody(),a.getLevel(),a.getAuthors(),a.getPermissions(),a.getGroups(), a.getReferences());
            }else{
                addArticle(a.getId(),a.getTitle(),a.getAbstractText(),a.getKeywords(),a.getBody(),a.getLevel(),a.getAuthors(),a.getPermissions(),a.getGroups(), a.getReferences());
            }
        }  
    }

    public ArrayList<String> getGroupNames(ArrayList<Integer> group_ids) throws SQLException
    {
        ArrayList<String> group_names = new ArrayList<>(0);

        if (group_ids.size() == 0)
        {
            group_names.add("All");
            return group_names;
        }

        for(int gid : group_ids){
            String name;
            if (gid == 0) {
                name = ("Ungrouped");
            }else{
                name = getGroup(gid).getName();
            }
            group_names.add(name);
        }
        return group_names;
    }

    private boolean writeArticlesToFile(backup_container backup, String fileName) {
        try (FileOutputStream fos = new FileOutputStream("Backups/" + fileName);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(backup);
            System.out.println("Articles have been written to " + fileName);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private backup_container readArticlesFromFile(String fileName) {
        backup_container contents = null;
        try (FileInputStream fis = new FileInputStream("Backups/" + fileName);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            
            contents = (backup_container) ois.readObject();
            System.out.println("Articles have been read from " + fileName);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return contents;
    }

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

    public boolean addArticle(Long articleId, String title, String abstractTxt, String keywords, String body, String level, String authors, String permissions, ArrayList<Integer> groups, ArrayList<Long> links) throws SQLException, Exception {
        if(articleExists(articleId))
        {
            return false;
        }

        String insertArticleQuery = "INSERT INTO Articles (article_id, title, abstract, keywords, body, level, authors, permissions) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    
        try (PreparedStatement articleStmt = connection.prepareStatement(insertArticleQuery, Statement.RETURN_GENERATED_KEYS)) {

            if(articleExists(articleId))
            {
                updateArticle(articleId, title, abstractTxt, keywords, body, level, authors, permissions, groups, links);
            }
            // Insert the article
            articleStmt.setLong(1, articleId);
            articleStmt.setString(2, title);
            articleStmt.setString(3, abstractTxt);
            articleStmt.setString(4, keywords);
            articleStmt.setString(5, encryptionHelper.encrypt(body));
            articleStmt.setString(6, level);
            articleStmt.setString(7, authors);
            articleStmt.setString(8, permissions);
            articleStmt.executeUpdate();
           linkGroups(articleId, groups);
           linkArticles(articleId, links);
           return true;
        }
    }

    public Boolean linkSAG(int user,  int group_id, boolean admin) throws SQLException {
        if(isUserLinked(group_id, user))
        {
            return false;
        }

        if(!data.user_db.userExists(user))
        {
            return false;
        }

        String linkSAGQuery = "INSERT INTO User_Groups (id, group_id, admin) VALUES (?, ?, ?)";
        try(PreparedStatement linkStmt = connection.prepareStatement(linkSAGQuery))
        {
            linkStmt.setInt(1, user);
            linkStmt.setInt(2, group_id);
            linkStmt.setBoolean(3, admin);
            linkStmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public Boolean deleteSAGUsers(int id) throws SQLException {
        String deleteGroupSQL = "DELETE FROM User_Groups WHERE id = ?;";

        // Delete from the SAG association table
        try (PreparedStatement pstmt = connection.prepareStatement(deleteGroupSQL)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public ObservableList<User> ListSAGUsers(int group_id, boolean admin) throws SQLException {
        ObservableList<User> users =  FXCollections.observableArrayList();
        data = Singleton.getInstance();

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
            // Iterate through the result set and create SAG Group objects
            while (rs.next()) {
                String username = rs.getString("username");
                users.add(data.user_db.getUser(username));
            }
        } catch (SQLException e) {
            return null;
        }
        return users;
    }

    public ObservableList<User> ListUsersNotInGroup(int group_id) throws SQLException {
        ObservableList<User> users = FXCollections.observableArrayList();
        data = Singleton.getInstance();
    
        String query = 
            "SELECT * " + 
            "FROM cse360users a " + 
            "LEFT JOIN User_Groups ag ON a.id = ag.id AND ag.group_id = ? " +
            "WHERE ag.group_id IS NULL;";
    
        PreparedStatement stmt = connection.prepareStatement(query);
        // Set the group_id parameter
        stmt.setInt(1, group_id);
    
        try (ResultSet rs = stmt.executeQuery()) {
            // Iterate through the result set and create User objects
            while (rs.next()) {
                String username = rs.getString("username");
                users.add(data.user_db.getUser(username));
            }
        } catch (SQLException e) {
            return null;
        }
        return users;
    }

     public boolean doesGroupExist(String groupName) {
        String query = "SELECT COUNT(*) FROM groups WHERE group_name = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, groupName);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    return count > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

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
            return false;
        }
        return false;
    }

    public boolean createGroup(String group_name, boolean special, int user_id) throws SQLException
    {
        data = Singleton.getInstance();
        //check if group already exists
        if (doesGroupExist(group_name)) 
        {
            return false;
        }

        //insert
        String insertGroupSQL = "INSERT INTO Groups (group_name, special) VALUES (?, ?);";
        try (PreparedStatement pstmt = connection.prepareStatement(insertGroupSQL)) {
            pstmt.setString(1, group_name);
            pstmt.setBoolean(2, special);
            int rowsAffected = pstmt.executeUpdate();
            if(rowsAffected > 0) {
                System.out.println("Special:" + special);
                Group new_group = getGroup(group_name);            
                if(!special)
                {
                    return addGeneralGroupUsers(new_group.getId());
                }else{
                    return linkSAG(user_id, new_group.getId(), true);
                }
            }else{
                return false;
            }
        }
    }
    
    public boolean restoreGroup(Group group) throws SQLException
    {
        data = Singleton.getInstance();
        //check if group already exists
        if (doesGroupExist(group.getName())) 
        {
            return false;
        }

        //insert
        String insertGroupSQL = "INSERT INTO Groups (group_id, group_name, special) VALUES (?, ?, ?);";
        try (PreparedStatement pstmt = connection.prepareStatement(insertGroupSQL)) {
            pstmt.setInt(1, group.getId());
            pstmt.setString(2, group.getName());
            pstmt.setBoolean(3, group.getSpecial());
            int rowsAffected = pstmt.executeUpdate();
            if(rowsAffected > 0) {         
                if(!group.getSpecial())
                {
                    return addGeneralGroupUsers(group.getId());
                }else{
                    for(User i: group.getAdmin_users())
                    {
                        linkSAG(i.getId(), group.getId(), true);
                    }

                    for(User i: group.getViewer_users())
                    {
                        linkSAG(i.getId(), group.getId(), false);
                    }
                    return true;
                }
            }else{
                return false;
            }
        }
    }

    public boolean addGeneralGroupUsers(int group_id) {
        String query = "SELECT * FROM cse360users";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            // Execute the query and get results
            ResultSet rs = statement.executeQuery();

            // Process the result set
            while (rs.next()) {
                int id = rs.getInt("id");
                String role = rs.getString("role");
                if (role.contains("instructor") || role.contains("admin"))
                {
                    linkSAG(id, group_id, true);
                }else{
                    linkSAG(id, group_id, false);
                }
            }
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean addUsertoGeneralGroups(User user) {
        String query = "SELECT * FROM Groups WHERE special = ?;";
        try {
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setBoolean(1, false);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int group_id = rs.getInt("group_id");
                linkSAG(user.getId(), group_id, !user.isOnlyStudent());
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
