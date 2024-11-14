package asu.cse360project;
import java.sql.SQLException;
import java.util.ArrayList;

import asu.cse360project.DatabaseHelpers.DatabaseHelper;
import asu.cse360project.DatabaseHelpers.GroupArticlesHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * This class is used to test the GroupArticlesHelper methods for adding, deleting, and updating articles.
 */
public class TestScriptArticles {

    /**
     * The main method that runs the test script.
     * @param args The command line arguments.
     * @throws SQLException If a database access error occurs.
     */
    public static void main(String[] args) throws SQLException, Exception {
        System.out.println("Welcome to the Article Methods Test Script");

        DatabaseHelper databaseManager = new DatabaseHelper();
        databaseManager.connectToDatabase();

        GroupArticlesHelper articleManager = databaseManager.getGroupArticlesHelper();

        System.out.println("Starting testing script now...");
        System.out.println("\n------------------------------------\n");

        ArrayList<Integer> failedTestCases = new ArrayList<>();

        articleManager.addArticle(1001L, "Advances in AI Research", "Exploring recent developments in artificial intelligence.", "AI, machine learning, neural networks", "The field of artificial intelligence has seen rapid advancements in recent years...", "advanced", "Dr. A. Smith, Prof. B. Johnson", "public", new ArrayList<>(), new ArrayList<>());
        
        ObservableList<Article> allArticles = FXCollections.observableArrayList();
        articleManager.listAllArticles(allArticles);
        
        System.out.println("Articles listed successfully:");
        for (Article article : allArticles) {
            System.out.println(article);
        }

        // Close database connection
        databaseManager.closeConnection();
    }

}

    /**
     * Test Case 1: Adding Articles
     * @param articleManager The GroupArticlesHelper instance.
     * @return True if the test case passed, false if it failed.
     * @throws SQLException If a database access error occurs.
     */
//     private static boolean TestCase1(GroupArticlesHelper articleManager) throws SQLException {
//         System.out.println("\n------------------------------------\n");
//         System.out.println("Test Case 1: Adding Articles");

//         boolean passed = true;

//         // Article data
//         System.out.println("Adding articles...");
//         try {
//             articleManager.addArticle(1001L, "Advances in AI Research", "Exploring recent developments in artificial intelligence.",
//                     "AI, machine learning, neural networks", "The field of artificial intelligence has seen rapid advancements in recent years...",
//                     "advanced", "Dr. A. Smith, Prof. B. Johnson", "public", new ArrayList<>(), new ArrayList<>());

//             articleManager.addArticle(1002L, "Environmental Impact of Urbanization", "A review of how urban growth affects ecosystems.",
//                     "environment, urbanization, sustainability", "Urbanization is one of the most significant drivers of environmental change today...",
//                     "intermediate", "Dr. C. Adams, Dr. D. Lee", "restricted", new ArrayList<>(), new ArrayList<>());

//             articleManager.addArticle(1003L, "Quantum Computing Basics", "Introduction to quantum computing concepts and applications.",
//                     "quantum computing, qubits, quantum mechanics", "Quantum computing offers a revolutionary approach to solving complex problems...",
//                     "beginner", "Dr. E. Perez", "public", new ArrayList<>(), new ArrayList<>());

//             System.out.println("Articles added successfully.");

//             System.out.println("Listing all articles in database...");
//             ObservableList<Article> allArticles = FXCollections.observableArrayList();
//             articleManager.listAllArticles(allArticles);

//             System.out.println("Articles listed successfully:");
//             for (Article article : allArticles) {
//                 System.out.println(article);
//             }

//         } catch (Exception e) {
//             System.out.println("Error while adding articles: " + e.getMessage());
//             passed = false;
//         }

//         return passed;
//     }

//     /**
//      * Test Case 2: Listing Articles
//      * @param articleManager The GroupArticlesHelper instance.
//      * @return True if the test case passed, false if it failed.
//      * @throws SQLException If a database access error occurs.
//      */
//     private static boolean TestCase2(GroupArticlesHelper articleManager) throws SQLException {
//         System.out.println("\n------------------------------------\n");
//         System.out.println("Test Case 2: Listing Articles");

//         boolean passed = true;

//         try {
//             System.out.println("Listing all articles...");
//             ObservableList<Article> allArticles = FXCollections.observableArrayList();
//             articleManager.listAllArticles(allArticles);

//             System.out.println("Articles listed successfully:");
//             for (Article article : allArticles) {
//                 System.out.println(article);
//             }

//         } catch (Exception e) {
//             System.out.println("Error while listing articles: " + e.getMessage());
//             passed = false;
//         }

//         return passed;
//     }

//     /**
//      * Test Case 3: Updating Articles
//      * @param articleManager The GroupArticlesHelper instance.
//      * @return True if the test case passed, false if it failed.
//      * @throws SQLException If a database access error occurs.
//      */
//     private static boolean TestCase3(GroupArticlesHelper articleManager) throws SQLException {
//         System.out.println("\n------------------------------------\n");
//         System.out.println("Test Case 3: Updating Articles");

//         boolean passed = true;

//         try {
//             System.out.println("Updating article with ID 1002...");
//             ObservableList<Article> articles = FXCollections.observableArrayList();
//             articleManager.listAllArticles(articles);

//             Article articleToUpdate = null;
//             for (Article article : articles) {
//                 if (article.getId() == 1002L) {
//                     articleToUpdate = article;
//                     break;
//                 }
//             }

//             if (articleToUpdate == null) {
//                 System.out.println("Error: Article with ID 1002 not found.");
//                 return false;
//             }

//             articleManager.updateArticle(articleToUpdate.getId(), "Updated Environmental Impact of Urbanization",
//                     "Updated abstract.", "updated keywords", "Updated body content.",
//                     "advanced", "Updated Authors", "private", new ArrayList<>(), new ArrayList<>());

//             // Verify the update
//             articles.clear();
//             articleManager.listAllArticles(articles);

//             System.out.println("Listing all articles...");
//             for (Article article : articles) {
//                 System.out.println(article);
//             }

//         } catch (Exception e) {
//             System.out.println("Error while updating article: " + e.getMessage());
//             passed = false;
//         }

//         return passed;
//     }

//     /**
//      * Test Case 4: Deleting Articles
//      * @param articleManager The GroupArticlesHelper instance.
//      * @return True if the test case passed, false if it failed.
//      * @throws SQLException If a database access error occurs.
//      */
//     private static boolean TestCase4(GroupArticlesHelper articleManager) throws SQLException {
//         System.out.println("\n------------------------------------\n");
//         System.out.println("Test Case 4: Deleting Articles");

//         boolean passed = true;

//         try {
//             System.out.println("Deleting article with ID 1001...");
//             articleManager.deleteArticle(1001L);

//             // Verify deletion
//             ObservableList<Article> articles = FXCollections.observableArrayList();
//             articleManager.listAllArticles(articles);

//             for (Article article : articles) {
//                 if (article.getId() == 1001L) {
//                     System.out.println("Error: Article with ID 1001 was not deleted.");
//                     passed = false;
//                     break;
//                 }
//             }

//             if (passed) {
//                 System.out.println("Article deleted successfully.");
//             }
//         } catch (Exception e) {
//             System.out.println("Error while deleting article: " + e.getMessage());
//             passed = false;
//         }

//         // Clean up remaining articles
//         articleManager.deleteArticle(1002L);
//         articleManager.deleteArticle(1003L);

//         System.out.println("Listing all articles after deletion...");
//         ObservableList<Article> allArticles = FXCollections.observableArrayList();
//         articleManager.listAllArticles(allArticles);

//         System.out.println("Articles listed successfully:");
//         for (Article article : allArticles) {
//             System.out.println(article);
//         }

//         return passed;
//     }

//     /**
//      * Test Case 5: Adding Article with Missing Required Fields
//      * @param articleManager The GroupArticlesHelper instance.
//      * @return True if the test case passed, false if it failed.
//      * @throws SQLException If a database access error occurs.
//      */
//     private static boolean TestCase5(GroupArticlesHelper articleManager) throws SQLException {
//         System.out.println("\n------------------------------------\n");
//         System.out.println("Test Case 5: Adding Article with Missing Required Fields");

//         boolean passed = true;

//         // Attempt to add an article with missing title
//         try {
//             System.out.println("Attempting to add article with missing title...");
//             articleManager.addArticle(2001L, "", "Abstract with no title",
//                     "keywords", "Body content", "Easy", "Author", "Public",
//                     new ArrayList<>(), new ArrayList<>());
//             System.out.println("Error: Article with missing title was added.");
//             passed = false;
//         } catch (Exception e) {
//             System.out.println("Caught expected exception: " + e.getMessage());
//         }

//         // Attempt to add an article with missing body
//         try {
//             System.out.println("Attempting to add article with missing body...");
//             articleManager.addArticle(2002L, "Title with no body", "Abstract",
//                     "keywords", "", "Easy", "Author", "Public",
//                     new ArrayList<>(), new ArrayList<>());
//             System.out.println("Error: Article with missing body was added.");
//             passed = false;
//         } catch (Exception e) {
//             System.out.println("Caught expected exception: " + e.getMessage());
//         }

//         // Attempt to add an article with missing author
//         try {
//             System.out.println("Attempting to add article with missing author...");
//             articleManager.addArticle(2003L, "Title", "Abstract",
//                     "keywords", "Body content", "Easy", "", "Public",
//                     new ArrayList<>(), new ArrayList<>());
//             System.out.println("Error: Article with missing author was added.");
//             passed = false;
//         } catch (Exception e) {
//             System.out.println("Caught expected exception: " + e.getMessage());
//         }

//         // Verify that no articles were added
//         ObservableList<Article> articles = FXCollections.observableArrayList();
//         articleManager.listAllArticles(articles);
//         if (articles.size() > 0) {
//             System.out.println("Error: Articles with missing required fields were added to the database.");
//             passed = false;
//         } else {
//             System.out.println("Articles with missing required fields were not added, as expected.");
//         }

//         return passed;
//     }

//     /**
//      * Test Case 6: Updating Non-existent Article
//      * @param articleManager The GroupArticlesHelper instance.
//      * @return True if the test case passed, false if it failed.
//      * @throws SQLException If a database access error occurs.
//      */
//     private static boolean TestCase6(GroupArticlesHelper articleManager) throws SQLException {
//         System.out.println("\n------------------------------------\n");
//         System.out.println("Test Case 6: Updating Non-existent Article");

//         boolean passed = true;

//         // Attempt to update an article that does not exist
//         try {
//             System.out.println("Attempting to update article with ID 9999...");
//             articleManager.updateArticle(9999L, "Non-existent Article",
//                     "Abstract", "keywords", "Body", "Easy", "Author", "Public",
//                     new ArrayList<>(), new ArrayList<>());
//             System.out.println("Error: Non-existent article was updated.");
//             passed = false;
//         } catch (Exception e) {
//             System.out.println("Caught expected exception: " + e.getMessage());
//         }

//         return passed;
//     }

//     /**
//      * Test Case 7: Deleting Non-existent Article
//      * @param articleManager The GroupArticlesHelper instance.
//      * @return True if the test case passed, false if it failed.
//      * @throws SQLException If a database access error occurs.
//      */
//     private static boolean TestCase7(GroupArticlesHelper articleManager) throws SQLException {
//         System.out.println("\n------------------------------------\n");
//         System.out.println("Test Case 7: Deleting Non-existent Article");

//         boolean passed = true;

//         // Attempt to delete an article that does not exist
//         try {
//             System.out.println("Attempting to delete article with ID 9999...");
//             articleManager.deleteArticle(9999L);
//             System.out.println("Error: Non-existent article was deleted.");
//             passed = false;
//         } catch (Exception e) {
//             System.out.println("Caught expected exception: " + e.getMessage());
//         }

//         return passed;
//     }

//     /**
//      * Test Case 8: Listing Articles When None Exist
//      * @param articleManager The GroupArticlesHelper instance.
//      * @return True if the test case passed, false if it failed.
//      * @throws SQLException If a database access error occurs.
//      */
//     private static boolean TestCase8(GroupArticlesHelper articleManager) throws SQLException {
//         System.out.println("\n------------------------------------\n");
//         System.out.println("Test Case 8: Listing Articles When None Exist");

//         boolean passed = true;

//         // Ensure that no articles exist
//         ObservableList<Article> articles = FXCollections.observableArrayList();
//         articleManager.listAllArticles(articles);
//         for (Article article : articles) {
//             articleManager.deleteArticle(article.getId());
//         }

//         // Try to list articles
//         articles.clear();
//         articleManager.listAllArticles(articles);
//         if (articles.isEmpty()) {
//             System.out.println("No articles found, as expected.");
//         } else {
//             System.out.println("Error: Expected no articles, but found some.");
//             passed = false;
//         }

//         return passed;
//     }

//     /**
//      * Test Case 9: Adding a Large Number of Articles
//      * @param articleManager The GroupArticlesHelper instance.
//      * @return True if the test case passed, false if it failed.
//      * @throws SQLException If a database access error occurs.
//      */
//     private static boolean TestCase9(GroupArticlesHelper articleManager) throws SQLException {
//         System.out.println("\n------------------------------------\n");
//         System.out.println("Test Case 9: Adding a Large Number of Articles");

//         boolean passed = true;

//         try {
//             System.out.println("Adding 100 articles...");
//             for (long i = 1; i <= 100; i++) {
//                 articleManager.addArticle(3000L + i, "Title " + i, "Abstract " + i,
//                         "keywords", "Body content " + i, "Easy", "Author " + i, "Public",
//                         new ArrayList<>(), new ArrayList<>());
//             }

//             // Verify that 100 articles were added
//             ObservableList<Article> articles = FXCollections.observableArrayList();
//             articleManager.listAllArticles(articles);
//             if (articles.size() == 100) {
//                 System.out.println("100 articles added successfully.");
//             } else {
//                 System.out.println("Error: Expected 100 articles, found " + articles.size());
//                 passed = false;
//             }
//         } catch (Exception e) {
//             System.out.println("Error while adding a large number of articles: " + e.getMessage());
//             passed = false;
//         }

//         // Clean up
//         ObservableList<Article> articles = FXCollections.observableArrayList();
//         articleManager.listAllArticles(articles);
//         for (Article article : articles) {
//             articleManager.deleteArticle(article.getId());
//         }

//         return passed;
//     }

//     private static boolean TestCase10(GroupArticlesHelper articleManager) throws SQLException {
//         System.out.println("\n------------------------------------\n");
//         System.out.println("Test Case 10: Adding Article with Invalid Data");

//         boolean passed = true;

//         // Attempt to add an article with negative ID
//         try {
//             System.out.println("Attempting to add article with negative ID...");
//             articleManager.addArticle(-1L, "Invalid ID Article", "Abstract",
//                     "keywords", "Body content", "Easy", "Author", "Public",
//                     new ArrayList<>(), new ArrayList<>());
//             System.out.println("Error: Article with negative ID was added.");
//             passed = false;
//         } catch (Exception e) {
//             System.out.println("Caught expected exception: " + e.getMessage());
//         }

//         // Attempt to add an article with invalid level
//         try {
//             System.out.println("Attempting to add article with invalid level...");
//             articleManager.addArticle(4001L, "Invalid Level Article", "Abstract",
//                     "keywords", "Body content", "InvalidLevel", "Author", "Public",
//                     new ArrayList<>(), new ArrayList<>());
//             System.out.println("Error: Article with invalid level was added.");
//             passed = false;
//         } catch (Exception e) {
//             System.out.println("Caught expected exception: " + e.getMessage());
//         }

//         // Verify that no articles were added
//         ObservableList<Article> articles = FXCollections.observableArrayList();
//         articleManager.listAllArticles(articles);
//         if (articles.size() > 0) {
//             System.out.println("Error: Articles with invalid data were added to the database.");
//             passed = false;
//         } else {
//             System.out.println("Articles with invalid data were not added, as expected.");
//         }

//         return passed;
//     }
// }
