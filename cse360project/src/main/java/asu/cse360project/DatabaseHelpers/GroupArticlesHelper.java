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

import asu.cse360project.Article;
import asu.cse360project.Group;
import asu.cse360project.Singleton;
import asu.cse360project.User;
import asu.cse360project.EncryptionHelpers.EncryptionHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * This class provides methods to interact with the database for Group and Article related operations.
 */
public class GroupArticlesHelper extends DatabaseHelper{	

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

    //add group to groups table
    public Group addGroup(String groupName) throws SQLException {
        String insertGroupSQL = "INSERT INTO Groups (group_name, special) VALUES (?, ?);";
        try (PreparedStatement pstmt = connection.prepareStatement(insertGroupSQL)) {
            pstmt.setString(1, groupName);
            pstmt.setBoolean(2, false);
            pstmt.executeUpdate();
        }
        return getGroup(groupName);
    }

    public Group addGroup(String groupName, int group_id) throws SQLException {
        String insertGroupSQL = "INSERT INTO Groups (group_id, group_name) VALUES (?, ?);";
        try (PreparedStatement pstmt = connection.prepareStatement(insertGroupSQL)) {
            pstmt.setInt(1, group_id);
            pstmt.setString(2, groupName);
            pstmt.executeUpdate();
        }
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
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String groupName = rs.getString("group_name");
                Integer groupID = rs.getInt("group_id");
                groups.add(new Group(groupName, groupID));
            }
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
                String groupName = rs.getString("group_name");
                Integer groupID = rs.getInt("group_id");
                groups.add(new Group(groupName, groupID));
            }
        }
        return groups;
    }

    //method to get all special groups a user belongs to
    public ObservableList<Group> getAllSpecialGroups(int user_id) throws SQLException {
        ObservableList<Group> groups = FXCollections.observableArrayList();
        String query = 
            "SELECT g.group_name " + 
            "FROM Groups g " + 
            "JOIN User_Groups ag ON g.group_id = ag.group_id " +
            "JOIN cse360users a ON ag.id = a.id " +
            "WHERE a.id = ?;";
        
        PreparedStatement stmt = connection.prepareStatement(query);            
        // Set theuser_id parameter
        stmt.setLong(1, user_id);

        try (ResultSet rs = stmt.executeQuery()) {
            // Iterate through the result set and create Group objects
            while (rs.next()) {
                String g_name = rs.getString("name");
                int g_id = rs.getInt("group_id");
                Group g = new Group(g_name, g_id); g.setSpecial(true);
                groups.add(g);
            }
        }
        return groups;
    }

    public void deleteGroup(int groupId) throws SQLException {
        String deleteGroupSQL = "DELETE FROM Groups WHERE group_id = ?;";

        // Delete from the group article association table
        try (PreparedStatement pstmt = connection.prepareStatement(deleteGroupSQL)) {
            pstmt.setInt(1, groupId);
            pstmt.executeUpdate();
            System.out.println("Group removed");
        }
    }

    public void listAllArticles(ObservableList<Article> articles) throws SQLException {
        String query = "SELECT * FROM Articles;";

        try (PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            // Clear the ObservableList to avoid duplicate entries
            articles.clear();

            // Retrieve each row and populate the Article object
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
                ArrayList<Long> references = getArticleRefs(id);
                ArrayList<String> groupNames = getArticleGroupNames(id);

                // Create a new Article object and add it to the ObservableList
                Article article = new Article(title, authors, abstractTxt, keywords, body, id, level, groups, references, permissions, groupNames);
                articles.add(article);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e; // Re-throw the exception for handling by calling code
        }
    }


public ObservableList<Article> ListArticles(int group_id) throws SQLException {
        ObservableList<Article> articles = FXCollections.observableArrayList();

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
                    articles.add(getArticle(rs));
                }
            }

        return articles;
    }

    public ObservableList<Article> searchArticles(int group_id, String search_level, String search_keywords) throws SQLException {
        ObservableList<Article> articles = FXCollections.observableArrayList();

        String query = 
            "SELECT * " + 
            "FROM Articles a " + 
            "JOIN Article_Groups ag ON a.article_id = ag.article_id " +
            "JOIN Groups g ON ag.group_id = g.group_id " +
            "WHERE g.group_id = ?";

        if(!search_level.equals("All")) {
            query += "AND (a.level = ?)";
        }

        if(!search_keywords.isEmpty()) {
            query += "AND (a.title LIKE ? OR a.authors LIKE ? OR a.abstract LIKE ?)";
        }

        PreparedStatement stmt = connection.prepareStatement(query);            
        // Set the group name parameter
        stmt.setInt(1, group_id);
        int update_param = 0;

        if(!search_level.equals("All")) {
            stmt.setString(2, search_level);
        }else{
            update_param = -1;
        }

        if(!search_keywords.isEmpty()) {
            stmt.setString(3 + update_param, "%" + search_keywords + "%");
            stmt.setString(4 + update_param, "%" + search_keywords + "%");
            stmt.setString(5 + update_param, "%" + search_keywords + "%");
        }

        try (ResultSet rs = stmt.executeQuery()) {
            // Iterate through the result set and create Article objects
            while (rs.next()) {
                articles.add(getArticle(rs));
            }
        }

        return articles;
    }

    //helper function for creating article object from result set
    private Article getArticle(ResultSet rs) throws SQLException{
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
        ArrayList<String> groups_names = getArticleGroupNames(id);

        return new Article(title, authors, abstractTxt, keywords, body, id, level, groups, refrences, permissions, groups_names);
    }
    
    public ObservableList<Article> ListMultipleGroupsArticles(ArrayList<Integer> groups) throws SQLException {
        ObservableList<Article> articles =  FXCollections.observableArrayList();

        if(groups.contains(-1))
        {
            return AddAllArticles();
        }

        if(groups.contains(0))
        {
            articles.addAll(ListAllUnGroupedArticles());
        }

        for(Integer grp_id: groups)
        {
            articles = addUnique(articles, ListArticles(grp_id));
        }
        return articles;
    }

    private ObservableList<Article> addUnique(ObservableList<Article> articles, ObservableList<Article> new_articles) {
        for(Article a: new_articles) {
            if (!articles.contains(a)) {
                articles.add(a);
            }
        }
        return articles;
    }
    
    private ObservableList<Article> ListAllUnGroupedArticles() throws SQLException {
        ObservableList<Article> articles =  FXCollections.observableArrayList();

        String query = 
            "SELECT a.* " + 
            "FROM Articles a " + 
            "LEFT JOIN Article_Groups ag ON a.article_id = ag.article_id " +
            "WHERE ag.article_id IS NULL;";

            PreparedStatement stmt = connection.prepareStatement(query);            
            try (ResultSet rs = stmt.executeQuery()) {
                // Iterate through the result set and create Article objects
                while (rs.next()) {
                    articles.add(getArticle(rs));
                }
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
                articles.add(getArticle(rs));
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

    //TODO: encrypt body before adding
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

    //TODO: Encrypt vody before updating
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

    public void backup(ArrayList<Integer> groups, String file_name) throws SQLException {
        ObservableList<Article> observale_articles_list = ListMultipleGroupsArticles(groups);
        ArrayList<Article> article_list = new ArrayList<>(observale_articles_list);
        writeArticlesToFile(article_list, file_name);
    }

    public void restoreMerge(String file_name) throws SQLException, Exception {
        ArrayList<Article> articles = readArticlesFromFile(file_name);

        for(Article a: articles) {

            if(!articleExists(a.getId()))
            {
                checkGroups(a);
                addArticle(a.getId(),a.getTitle(),a.getAbstractText(),a.getKeywords(),encryptionHelper.decrypt(a.getBody()),a.getLevel(),a.getAuthors(),a.getPermissions(),a.getGroups(), a.getReferences());
            }
        }
    }
    
    public void restore(String file_name) throws SQLException, Exception {
        ArrayList<Article> articles = readArticlesFromFile(file_name);

        for(Article a: articles) {
            checkGroups(a);
            if(articleExists(a.getId()))
            {
                updateArticle(a.getId(),a.getTitle(),a.getAbstractText(),a.getKeywords(),encryptionHelper.decrypt(a.getBody()),a.getLevel(),a.getAuthors(),a.getPermissions(),a.getGroups(), a.getReferences());
            }else{
                addArticle(a.getId(),a.getTitle(),a.getAbstractText(),a.getKeywords(),encryptionHelper.decrypt(a.getBody()),a.getLevel(),a.getAuthors(),a.getPermissions(),a.getGroups(), a.getReferences());
            }
        }  
    }

    private void checkGroups(Article a) throws SQLException {
        for(int i = 0; i < a.getGroups().size(); i++) {
            if(!groupExists(a.getGroups().get(i))) {
                addGroup(a.getGroup_names().get(i), a.getGroups().get(i));
            }
        }
    }

    public void writeArticlesToFile(ArrayList<Article> articles, String fileName) {
        try (FileOutputStream fos = new FileOutputStream("Backups/" + fileName);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            
            oos.writeObject(articles);
            System.out.println("Articles have been written to " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public ArrayList<Article> readArticlesFromFile(String fileName) {
        ArrayList<Article> articles = null;
        try (FileInputStream fis = new FileInputStream("Backups/" + fileName);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            
            articles = (ArrayList<Article>) ois.readObject();
            System.out.println("Articles have been read from " + fileName);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return articles;
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

    public void addArticle(Long articleId, String title, String abstractTxt, String keywords, String body, String level, String authors, String permissions, ArrayList<Integer> groups, ArrayList<Long> links) throws SQLException, Exception {
    
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
        }
    }

    //Assume same user cannot be added twice
    public Boolean linkSAG(int user,  int group_id, boolean admin) throws SQLException {
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

        String query = 
        "SELECT * " + 
        "FROM cse360users a " + 
        "JOIN User_Groups ag ON a.id = ag.id " +
        "JOIN Groups g ON ag.group_id = g.group_id " +
        "WHERE g.group_id = ? AND ag.admin = ?;";

        PreparedStatement stmt = connection.prepareStatement(query);            
        // Set the group name parameter
        stmt.setInt(1, group_id);
        stmt.setBoolean(2, admin);

        try (ResultSet rs = stmt.executeQuery()) {
            // Iterate through the result set and create SAG Group objects
            while (rs.next()) {
                String username = rs.getString("username");
                data.user_db.getUser(username);
                users.add(data.user_db.getUser(username));
                return users;
            }
        } catch (SQLException e) {
            return null;
        }
        return users;
    }
}
