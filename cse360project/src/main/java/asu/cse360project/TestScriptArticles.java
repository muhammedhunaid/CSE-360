package asu.cse360project;

import asu.cse360project.DatabaseHelpers.DatabaseHelper;
import asu.cse360project.DatabaseHelpers.GroupArticlesHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public class TestScriptArticles {

    public static void main(String[] args) throws SQLException {
        System.out.println("Welcome to the Article Methods Test Script");

        DatabaseHelper databaseManager = new DatabaseHelper();
        databaseManager.connectToDatabase();

        GroupArticlesHelper articleManager = databaseManager.getGroupArticlesHelper();

        System.out.println("Starting testing script now...");
        System.out.println("\n------------------------------------\n");

        ArrayList<Integer> failedTestCases = new ArrayList<>();

        // Test Case 1: Adding Articles
        if(TestCase1(articleManager)){
            System.out.println("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
            System.out.println("Test Case 1 PASSED");
            System.out.println("Expected: True\nActual: True");
        } else {
            System.out.println("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
            System.out.println("Test Case 1 FAILED!!");
            failedTestCases.add(1);
            System.out.println("Expected: True\nActual: False");
        }

        // Test Case 2: Listing Articles
        if(TestCase2(articleManager)){
            System.out.println("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
            System.out.println("Test Case 2 PASSED");
            System.out.println("Expected: True\nActual: True");
        } else {
            System.out.println("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
            System.out.println("Test Case 2 FAILED!!");
            failedTestCases.add(2);
            System.out.println("Expected: True\nActual: False");

        }

        // Test Case 3: Updating Articles
        if(TestCase3(articleManager)){
            System.out.println("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
            System.out.println("Test Case 3 PASSED");
            System.out.println("Expected: True\nActual: True");
        } else {
            System.out.println("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
            System.out.println("Test Case 3 FAILED!!");
            failedTestCases.add(3);
            System.out.println("Expected: True\nActual: False");
        }

        // Test Case 4: Deleting Articles
        if(TestCase4(articleManager)){
            System.out.println("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
            System.out.println("Test Case 4 PASSED");
            System.out.println("Expected: True\nActual: True");
        } else {
            System.out.println("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
            System.out.println("Test Case 4 FAILED!!");
            failedTestCases.add(4);
            System.out.println("Expected: True\nActual: False");
        }

        // Test Case 5: Adding Article with Missing Required Fields
        if(TestCase5(articleManager) == false){
            System.out.println("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
            System.out.println("Test Case 5 PASSED");
            System.out.println("Expected: False\nActual: False");
        } else {
            System.out.println("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
            System.out.println("Test Case 5 FAILED!!");
            failedTestCases.add(5);
            System.out.println("Expected: False\nActual: True");
        }

        // Test Case 6: Updating Non-existent Article
        if(TestCase6(articleManager) == false){
            System.out.println("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
            System.out.println("Test Case 6 PASSED");
            System.out.println("Expected: False\nActual: False");
        } else {
            System.out.println("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
            System.out.println("Test Case 6 FAILED!!");
            failedTestCases.add(6);
            System.out.println("Expected: False\nActual: True");
        }

        // Test Case 7: Deleting Non-existent Article
        if(TestCase7(articleManager) == false){
            System.out.println("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
            System.out.println("Test Case 7 PASSED");
            System.out.println("Expected: False\nActual: False");
        } else {
            System.out.println("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
            System.out.println("Test Case 7 FAILED!!");
            failedTestCases.add(7);
            System.out.println("Expected: False\nActual: True");
        }

        // Test Case 8: Listing Articles When None Exist
        if(TestCase8(articleManager)){
            System.out.println("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
            System.out.println("Test Case 8 PASSED");
            System.out.println("Expected: False\nActual: False");
        } else {
            System.out.println("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
            System.out.println("Test Case 8 FAILED!!");
            failedTestCases.add(8);
            System.out.println("Expected: False\nActual: True");
        }

        // Test Case 9: Adding a Large Number of Articles
        if(TestCase9(articleManager)){
            System.out.println("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
            System.out.println("Test Case 9 PASSED");
            System.out.println("Expected: True\nActual: True");
        } else {
            System.out.println("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
            System.out.println("Test Case 9 FAILED!!");
            failedTestCases.add(9);
            System.out.println("Expected: False\nActual: False");
        }

        // Test Case 10: Adding Article with Invalid Data
        if(TestCase10(articleManager) == false){
            System.out.println("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
            System.out.println("Test Case 10 PASSED");
            System.out.println("Expected: False\nActual: False");
        } else {
            System.out.println("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
            System.out.println("Test Case 10 FAILED!!");
            failedTestCases.add(10);
            System.out.println("Expected: False\nActual: True");
        }


        // Test Case 13: Backup when there are no articles
        if (TestCase13(articleManager)) {
            System.out.println("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
            System.out.println("Test Case 13 PASSED");
            System.out.println("Expected: True\nActual: True");
        } else {
            System.out.println("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
            System.out.println("Test Case 13 FAILED!!");
            failedTestCases.add(13);
            System.out.println("Expected: True\nActual: False");
        }

        // Test Case 14: Restore from a corrupted backup file
        if (TestCase14(articleManager)) {
            System.out.println("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
            System.out.println("Test Case 14 PASSED");
            System.out.println("Expected: False\nActual: False");
        } else {
            System.out.println("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
            System.out.println("Test Case 14 FAILED!!");
            failedTestCases.add(14);
            System.out.println("Expected: False\nActual: True");
        }

        // Test Case 15: Restore when articles have conflicting IDs
        if (TestCase15(articleManager)) {
            System.out.println("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
            System.out.println("Test Case 15 PASSED");
            System.out.println("Expected: True\nActual: True");
        } else {
            System.out.println("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
            System.out.println("Test Case 15 FAILED!!");
            failedTestCases.add(15);
            System.out.println("Expected: True\nActual: False");
        }

        // Test Case 16: Backup to an invalid file path
        if (TestCase16(articleManager) == false) {
            System.out.println("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
            System.out.println("Test Case 16 PASSED");
            System.out.println("Expected: False\nActual: False");
        } else {
            System.out.println("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
            System.out.println("Test Case 16 FAILED!!");
            failedTestCases.add(16);
            System.out.println("Expected: False\nActual: True");
        }

        // Test Case 17: Restore from backup file with invalid data
        if (TestCase17(articleManager) == false) {
            System.out.println("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
            System.out.println("Test Case 17 PASSED");
            System.out.println("Expected: False\nActual: False");
        } else {
            System.out.println("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
            System.out.println("Test Case 17 FAILED!!");
            failedTestCases.add(17);
            System.out.println("Expected: False\nActual: True");
        }

        // List failed test cases
        System.out.println("\nList of Failed Test Cases: ");
        if(failedTestCases.isEmpty()){
            System.out.println("None");
        } else {
            for(Integer i : failedTestCases){
                System.out.println("Test Case " + i);
            }
        }

        // Close database connection
        databaseManager.closeConnection();
    }

    private static boolean TestCase1(GroupArticlesHelper articleManager) throws SQLException {
        System.out.println("\n------------------------------------\n");
        System.out.println("Test Case 1: Adding Articles");

        boolean passed = true;

        // Article data
        System.out.println("Adding articles...");
        try {
            articleManager.addArticle(1001L, "Advances in AI Research", "Exploring recent developments in artificial intelligence.",
                    "AI, machine learning, neural networks", "The field of artificial intelligence has seen rapid advancements in recent years...",
                    "advanced", "Dr. A. Smith, Prof. B. Johnson", "public", new ArrayList<>(), new ArrayList<>());

            articleManager.addArticle(1002L, "Environmental Impact of Urbanization", "A review of how urban growth affects ecosystems.",
                    "environment, urbanization, sustainability", "Urbanization is one of the most significant drivers of environmental change today...",
                    "intermediate", "Dr. C. Adams, Dr. D. Lee", "restricted", new ArrayList<>(), new ArrayList<>());

            articleManager.addArticle(1003L, "Quantum Computing Basics", "Introduction to quantum computing concepts and applications.",
                    "quantum computing, qubits, quantum mechanics", "Quantum computing offers a revolutionary approach to solving complex problems...",
                    "beginner", "Dr. E. Perez", "public", new ArrayList<>(), new ArrayList<>());

            System.out.println("Articles added successfully.");

            System.out.println("Listing all articles in database...");
            ObservableList<Article> allArticles = FXCollections.observableArrayList();
            articleManager.listAllArticles(allArticles);

            System.out.println("Articles listed successfully:");
            for (Article article : allArticles) {
                System.out.println(article);
            }

        } catch (Exception e) {
            System.out.println("Error while adding articles: " + e.getMessage());
            passed = false;
        }

        return passed;
    }

    private static boolean TestCase2(GroupArticlesHelper articleManager) throws SQLException {
        System.out.println("\n------------------------------------\n");
        System.out.println("Test Case 2: Listing Articles");

        boolean passed = true;

        try {
            System.out.println("Listing all articles...");
            ObservableList<Article> allArticles = FXCollections.observableArrayList();
            articleManager.listAllArticles(allArticles);

            System.out.println("Articles listed successfully:");
            for (Article article : allArticles) {
                System.out.println(article);
            }

        } catch (Exception e) {
            System.out.println("Error while listing articles: " + e.getMessage());
            passed = false;
        }

        return passed;
    }

    private static boolean TestCase3(GroupArticlesHelper articleManager) throws SQLException {
        System.out.println("\n------------------------------------\n");
        System.out.println("Test Case 3: Updating Articles");

        boolean passed = true;

        try {
            System.out.println("Updating article with ID 1002...");
            ObservableList<Article> articles = FXCollections.observableArrayList();
            articleManager.listAllArticles(articles);

            Article articleToUpdate = null;
            for (Article article : articles) {
                if (article.getId() == 1002L) {
                    articleToUpdate = article;
                    break;
                }
            }

            if (articleToUpdate == null) {
                System.out.println("Error: Article with ID 1002 not found.");
                return false;
            }

            articleManager.updateArticle(articleToUpdate.getId(), "Updated Environmental Impact of Urbanization",
                    "Updated abstract.", "updated keywords", "Updated body content.",
                    "advanced", "Updated Authors", "private", new ArrayList<>(), new ArrayList<>());

            // Verify the update
            articles.clear();
            articleManager.listAllArticles(articles);

            System.out.println("Listing all articles...");
            for (Article article : articles) {
                System.out.println(article);
            }

        } catch (Exception e) {
            System.out.println("Error while updating article: " + e.getMessage());
            passed = false;
        }

        return passed;
    }

    //Write the testcase for backuping the article

    private static boolean TestCase4(GroupArticlesHelper articleManager) throws SQLException {
        System.out.println("\n------------------------------------\n");
        System.out.println("Test Case 4: Deleting Articles");

        boolean passed = true;

        try {
            System.out.println("Deleting article with ID 1001...");
            articleManager.deleteArticle(1001L);

            // Verify deletion
            ObservableList<Article> articles = FXCollections.observableArrayList();
            articleManager.listAllArticles(articles);

            for (Article article : articles) {
                if (article.getId() == 1001L) {
                    System.out.println("Error: Article with ID 1001 was not deleted.");
                    passed = false;
                    break;
                }
            }

            if (passed) {
                System.out.println("Article deleted successfully.");
            }
        } catch (Exception e) {
            System.out.println("Error while deleting article: " + e.getMessage());
            passed = false;
        }

        // Clean up remaining articles
        articleManager.deleteArticle(1002L);
        articleManager.deleteArticle(1003L);

        System.out.println("Listing all articles after deletion...");
        ObservableList<Article> allArticles = FXCollections.observableArrayList();
        articleManager.listAllArticles(allArticles);

        System.out.println("Articles listed successfully:");
        for (Article article : allArticles) {
            System.out.println(article);
        }

        return passed;
    }

    private static boolean TestCase5(GroupArticlesHelper articleManager) throws SQLException {
        System.out.println("\n------------------------------------\n");
        System.out.println("Test Case 5: Adding Article with Missing Required Fields");

        boolean passed = true;

        // Attempt to add an article with missing title
        try {
            System.out.println("Attempting to add article with missing title...");
            articleManager.addArticle(2001L, "", "Abstract with no title",
                    "keywords", "Body content", "Easy", "Author", "Public",
                    new ArrayList<>(), new ArrayList<>());
            System.out.println("Error: Article with missing title was added.");
            passed = false;
        } catch (Exception e) {
            System.out.println("Caught expected exception: " + e.getMessage());
        }

        // Attempt to add an article with missing body
        try {
            System.out.println("Attempting to add article with missing body...");
            articleManager.addArticle(2002L, "Title with no body", "Abstract",
                    "keywords", "", "Easy", "Author", "Public",
                    new ArrayList<>(), new ArrayList<>());
            System.out.println("Error: Article with missing body was added.");
            passed = false;
        } catch (Exception e) {
            System.out.println("Caught expected exception: " + e.getMessage());
        }

        // Attempt to add an article with missing author
        try {
            System.out.println("Attempting to add article with missing author...");
            articleManager.addArticle(2003L, "Title", "Abstract",
                    "keywords", "Body content", "Easy", "", "Public",
                    new ArrayList<>(), new ArrayList<>());
            System.out.println("Error: Article with missing author was added.");
            passed = false;
        } catch (Exception e) {
            System.out.println("Caught expected exception: " + e.getMessage());
        }

        // Verify that no articles were added
        ObservableList<Article> articles = FXCollections.observableArrayList();
        articleManager.listAllArticles(articles);
        if (articles.size() > 0) {
            System.out.println("Error: Articles with missing required fields were added to the database.");
            passed = false;
        } else {
            System.out.println("Articles with missing required fields were not added, as expected.");
        }

        return passed;
    }

    private static boolean TestCase6(GroupArticlesHelper articleManager) throws SQLException {
        System.out.println("\n------------------------------------\n");
        System.out.println("Test Case 6: Updating Non-existent Article");

        boolean passed = true;

        // Attempt to update an article that does not exist
        try {
            System.out.println("Attempting to update article with ID 9999...");
            articleManager.updateArticle(9999L, "Non-existent Article",
                    "Abstract", "keywords", "Body", "Easy", "Author", "Public",
                    new ArrayList<>(), new ArrayList<>());
            System.out.println("Error: Non-existent article was updated.");
            passed = false;
        } catch (Exception e) {
            System.out.println("Caught expected exception: " + e.getMessage());
        }

        return passed;
    }

    private static boolean TestCase7(GroupArticlesHelper articleManager) throws SQLException {
        System.out.println("\n------------------------------------\n");
        System.out.println("Test Case 7: Deleting Non-existent Article");

        boolean passed = true;

        // Attempt to delete an article that does not exist
        try {
            System.out.println("Attempting to delete article with ID 9999...");
            articleManager.deleteArticle(9999L);
            System.out.println("Error: Non-existent article was deleted.");
            passed = false;
        } catch (Exception e) {
            System.out.println("Caught expected exception: " + e.getMessage());
        }

        return passed;
    }

    private static boolean TestCase8(GroupArticlesHelper articleManager) throws SQLException {
        System.out.println("\n------------------------------------\n");
        System.out.println("Test Case 8: Listing Articles When None Exist");

        boolean passed = true;

        // Ensure that no articles exist
        ObservableList<Article> articles = FXCollections.observableArrayList();
        articleManager.listAllArticles(articles);
        for (Article article : articles) {
            articleManager.deleteArticle(article.getId());
        }

        // Try to list articles
        articles.clear();
        articleManager.listAllArticles(articles);
        if (articles.isEmpty()) {
            System.out.println("No articles found, as expected.");
        } else {
            System.out.println("Error: Expected no articles, but found some.");
            passed = false;
        }

        return passed;
    }

    private static boolean TestCase9(GroupArticlesHelper articleManager) throws SQLException {
        System.out.println("\n------------------------------------\n");
        System.out.println("Test Case 9: Adding a Large Number of Articles");

        boolean passed = true;

        try {
            System.out.println("Adding 100 articles...");
            for (long i = 1; i <= 100; i++) {
                articleManager.addArticle(3000L + i, "Title " + i, "Abstract " + i,
                        "keywords", "Body content " + i, "Easy", "Author " + i, "Public",
                        new ArrayList<>(), new ArrayList<>());
            }

            // Verify that 100 articles were added
            ObservableList<Article> articles = FXCollections.observableArrayList();
            articleManager.listAllArticles(articles);
            if (articles.size() == 100) {
                System.out.println("100 articles added successfully.");
            } else {
                System.out.println("Error: Expected 100 articles, found " + articles.size());
                passed = false;
            }
        } catch (Exception e) {
            System.out.println("Error while adding a large number of articles: " + e.getMessage());
            passed = false;
        }

        // Clean up
        ObservableList<Article> articles = FXCollections.observableArrayList();
        articleManager.listAllArticles(articles);
        for (Article article : articles) {
            articleManager.deleteArticle(article.getId());
        }

        return passed;
    }

    private static boolean TestCase10(GroupArticlesHelper articleManager) throws SQLException {
        System.out.println("\n------------------------------------\n");
        System.out.println("Test Case 10: Adding Article with Invalid Data");

        boolean passed = true;

        // Attempt to add an article with negative ID
        try {
            System.out.println("Attempting to add article with negative ID...");
            articleManager.addArticle(-1L, "Invalid ID Article", "Abstract",
                    "keywords", "Body content", "Easy", "Author", "Public",
                    new ArrayList<>(), new ArrayList<>());
            System.out.println("Error: Article with negative ID was added.");
            passed = false;
        } catch (Exception e) {
            System.out.println("Caught expected exception: " + e.getMessage());
        }

        // Attempt to add an article with invalid level
        try {
            System.out.println("Attempting to add article with invalid level...");
            articleManager.addArticle(4001L, "Invalid Level Article", "Abstract",
                    "keywords", "Body content", "InvalidLevel", "Author", "Public",
                    new ArrayList<>(), new ArrayList<>());
            System.out.println("Error: Article with invalid level was added.");
            passed = false;
        } catch (Exception e) {
            System.out.println("Caught expected exception: " + e.getMessage());
        }

        // Verify that no articles were added
        ObservableList<Article> articles = FXCollections.observableArrayList();
        articleManager.listAllArticles(articles);
        if (articles.size() > 0) {
            System.out.println("Error: Articles with invalid data were added to the database.");
            passed = false;
        } else {
            System.out.println("Articles with invalid data were not added, as expected.");
        }

        return passed;
    }

    private static boolean TestCase13(GroupArticlesHelper articleManager) throws SQLException {
        System.out.println("\n------------------------------------\n");
        System.out.println("Test Case 13: Backup when there are no articles");

        boolean passed = true;

        try {
            // Ensure the database is empty
            ObservableList<Article> articles = FXCollections.observableArrayList();
            articleManager.listAllArticles(articles);
            for (Article article : articles) {
                articleManager.deleteArticle(article.getId());
            }

            // Perform backup
            System.out.println("Performing backup with no articles...");
            String backupFileName = "backupFile.txt";
            ArrayList<Integer> groupIds = new ArrayList<>(); // No groups
            articleManager.backup(groupIds, backupFileName);

            // Verify backup file exists
            java.io.File backupFile = new java.io.File("Backups/" + backupFileName);
            if (backupFile.exists()) {
                System.out.println("Backup file created successfully: " + backupFileName);
            } else {
                System.out.println("Error: Backup file was not created.");
                passed = false;
            }

            // Optional: Verify that the backup file contains an empty list
            // This would involve deserializing the file and checking the contents

        } catch (Exception e) {
            System.out.println("Error during backup test: " + e.getMessage());
            passed = false;
        }

        return passed;
    }

    private static boolean TestCase14(GroupArticlesHelper articleManager) throws SQLException {
        System.out.println("\n------------------------------------\n");
        System.out.println("Test Case 14: Restore from a corrupted backup file");

        boolean passed = false;

        try {
            // Prepare a corrupted backup file
            System.out.println("Creating a corrupted backup file...");
            String backupFileName = "backupFile.txt";
            try (FileOutputStream fos = new FileOutputStream("Backups/" + backupFileName)) {
                fos.write("This is not a valid backup file".getBytes());
            } catch (IOException e) {
                System.out.println("Error creating corrupted backup file: " + e.getMessage());
                return false;
            }

            // Attempt to restore from the corrupted backup file
            System.out.println("Attempting to restore from corrupted backup file...");
            articleManager.restore(backupFileName);

            System.out.println("Error: Restore should have failed due to corrupted backup file.");
        } catch (Exception e) {
            System.out.println("Caught expected exception: " + e.getMessage());
            passed = true;
        }

        return passed;
    }

    private static boolean TestCase15(GroupArticlesHelper articleManager) throws SQLException {
        System.out.println("\n------------------------------------\n");
        System.out.println("Test Case 15: Restore when articles have conflicting IDs");

        boolean passed = true;

        try {
            // Add an article to the database
            System.out.println("Adding an article to the database...");
            articleManager.addArticle(8001L, "Existing Article", "Abstract", "Keywords",
                    "Body content", "Easy", "Author", "Public", new ArrayList<>(), new ArrayList<>());

            // Prepare a backup file with an article that has the same ID but different content
            System.out.println("Preparing backup file with conflicting article ID...");
            String backupFileName = "backupFile.txt";
            ArrayList<Article> backupArticles = new ArrayList<>();
            backupArticles.add(new Article("Conflicting Article", "Authors", "Different Abstract", "Different Keywords",
                    "Different Body content", 8001L, "Advanced", new ArrayList<Integer>(),  new ArrayList<Long>(), "Private", new ArrayList<>()));

            // Write backupArticles to file
            articleManager.writeArticlesToFile(backupArticles, backupFileName);

            // Perform restore
            System.out.println("Performing restore...");
            articleManager.restore(backupFileName);

            // Verify that the article was updated
            ObservableList<Article> articles = FXCollections.observableArrayList();
            articleManager.listAllArticles(articles);
            boolean updateVerified = false;
            for (Article article : articles) {
                if (article.getId() == 8001L && article.getTitle().equals("Conflicting Article")) {
                    updateVerified = true;
                    break;
                }
            }
            if (updateVerified) {
                System.out.println("Article updated successfully during restore.");
            } else {
                System.out.println("Error: Article was not updated during restore.");
                passed = false;
            }

        } catch (Exception e) {
            System.out.println("Error during restore test: " + e.getMessage());
            passed = false;
        }

        // Clean up
        articleManager.deleteArticle(8001L);

        return passed;
    }

    private static boolean TestCase16(GroupArticlesHelper articleManager) throws SQLException {
        System.out.println("\n------------------------------------\n");
        System.out.println("Test Case 16: Backup to an invalid file path");

        boolean passed = false;

        try {
            // Add a sample article
            System.out.println("Adding a sample article...");
            articleManager.addArticle(9001L, "Sample Article", "Abstract", "Keywords",
                    "Body content", "Easy", "Author", "Public", new ArrayList<>(), new ArrayList<>());

            // Attempt to backup to an invalid file path
            System.out.println("Attempting to backup to an invalid file path...");
            String invalidBackupFileName = "fileDoesNotExist.txt";
            ArrayList<Integer> groupIds = new ArrayList<>(); // No groups
            articleManager.backup(groupIds, invalidBackupFileName);

            System.out.println("Error: Backup should have failed due to invalid file path.");
        } catch (Exception e) {
            System.out.println("Caught expected exception: " + e.getMessage());
            passed = true;
        }

        // Clean up
        articleManager.deleteArticle(9001L);

        return passed;
    }

    private static boolean TestCase17(GroupArticlesHelper articleManager) throws SQLException {
        System.out.println("\n------------------------------------\n");
        System.out.println("Test Case 17: Restore from backup file with invalid data");

        boolean passed = false;

        try {
            // Prepare a backup file with articles containing invalid data
            System.out.println("Preparing backup file with invalid data...");
            String backupFileName = "invalid_data_backup.dat";
            ArrayList<Article> backupArticles = new ArrayList<>();
            backupArticles.add(new Article("", "Authors", "Abstract", "Keywords",
                    "Body content", 10001L, "Easy", new ArrayList<Integer>(),  new ArrayList<Long>(), "Public", new ArrayList<>()));

            // Article with empty title, which is invalid

            // Write backupArticles to file
            articleManager.writeArticlesToFile(backupArticles, backupFileName);

            // Attempt to restore
            System.out.println("Attempting to restore from backup file with invalid data...");
            articleManager.restore(backupFileName);

            System.out.println("Error: Restore should have failed due to invalid article data.");
        } catch (Exception e) {
            System.out.println("Caught expected exception: " + e.getMessage());
            passed = true;
        }

        return passed;
    }
}
