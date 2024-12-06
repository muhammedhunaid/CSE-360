package asu.cse360project.Testing;

import java.sql.SQLException;
import java.util.ArrayList;

import asu.cse360project.DatabaseHelpers.DatabaseHelper;
import asu.cse360project.DatabaseHelpers.GroupArticlesHelper;
import asu.cse360project.EncryptionHelpers.EncryptionHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

    /*******
     * <p> TestScriptArticles </p>
     * 
     * <p> Description: This class contains a test script to test the Article methods in the GroupArticlesHelper class. </p>
     * 
     * <p> Copyright: Tu35 © 2024 </p>
     * 
     * @author Tu35
     * 
     * @version 1.00 2024-11-20 Created to test Phase 3 Group Articles.
     * 
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

        // Test Case 1: Encryption and storage in database
        if(TestCase1(articleManager)){
            System.out.println("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
            System.out.println("Test Case 1 PASSED");
            System.out.println("Expected: True\nActual: True");
        } else {
            System.out.println("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
            System.out.println("Test Case 1 PASSED!!");
            failedTestCases.add(1);
            System.out.println("Expected: True\nActual: True");
        }

        // Test Case 2: Decryption and displaying their article to the user
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

    /**
     * Test Case 1: Adding Articles
     * @param articleManager The GroupArticlesHelper instance.
     * @return True if the test case passed, false if it failed.
     * @throws SQLException If a database access error occurs.
     */
    private static boolean TestCase1(GroupArticlesHelper articleManager) throws SQLException {
        System.out.println("\n------------------------------------\n");
        System.out.println("Test Case 1: Adding Articles");

        boolean passed1 = false;
        boolean passed2 = false;
        boolean passed3 = false;

        String body1 = "THis is some random chunk of garbage text used to test the encryption $%@# 2748-34 method in the database";
        String body2 = "THis is eiyha hpeuifhwefuewy [wefuyweuf tey[fuwew]] $%@# 2748-34 method in the database";
        String body3 = " uoifpw9fy23 8r9237pr839r71t379rt97 ^_ *&($^(*#$_&*@ $Y(* ye8yqfiuasd afghfua yfoaieuyf[q9FYY AIFH GPHSDI]))) in the database";

        // Article data
        System.out.println("Adding articles...");
        try {
            articleManager.addArticle(1031L, "Fire in AI Research", "Exploring recent developments in artificial intelligence.",
                    "AI, machine learning, neural networks", body1,
                    "advanced", "Dr. A. Smith, Prof. B. Johnson", "public", new ArrayList<>(), new ArrayList<>());

            articleManager.addArticle(1002L, "Environmental Impact of Urbanization", "A review of how urban growth affects ecosystems.",
                    "environment, urbanization, sustainability", body2,
                    "intermediate", "Dr. C. Adams, Dr. D. Lee", "restricted", new ArrayList<>(), new ArrayList<>());

            articleManager.addArticle(1203L, "Quantum Computing Basics", "Introduction to quantum computing concepts and applications.",
                    "quantum computing, qubits, quantum mechanics", body3,
                    "beginner", "Dr. E. Perez", "public", new ArrayList<>(), new ArrayList<>());

            System.out.println("Articles added successfully.");

            System.out.println("Listing all articles in database...");
            ObservableList<Article> allArticles = FXCollections.observableArrayList();
            allArticles = articleManager.listAllArticles();

            System.out.println("Articles listed successfully:");
            for (Article article : allArticles) {

                if(article.getId() == 1031L && !body1.equals(article.getBody())){
                    passed1 = true;
                }

                if(article.getId() == 1002L && !body2.equals(article.getBody())){
                    passed2 = true;
                }

                if(article.getId() == 1203L && !body3.equals(article.getBody())){
                    passed3 = true;
                }
                
            }

            return passed1 && passed2 && passed3;

        } catch (Exception e) {
            System.out.println("Error while adding articles: " + e.getMessage());
            return false;
        }

    }

    /**
     * Test Case 2: Listing Articles
     * @param articleManager The GroupArticlesHelper instance.
     * @return True if the test case passed, false if it failed.
    * @throws Exception 
    */
         private static boolean TestCase2(GroupArticlesHelper articleManager) throws Exception {
        System.out.println("\n------------------------------------\n");
        System.out.println("Test Case 2: Listing Articles as stored in Database");

        EncryptionHelper helper = new EncryptionHelper();

        boolean passed = true;

        try {
            System.out.println("Listing all articles...");
            ObservableList<Article> allArticles = articleManager.listAllArticles();
            

            System.out.println("Articles listed successfully as stored in database:");
            for (Article article : allArticles) {
                if(article.getId() == 1031L){
                    System.out.println(article.getBody());
                }

                if(article.getId() == 1002L){
                    System.out.println(article.getBody());
                }

                if(article.getId() == 1203L){
                    System.out.println(article.getBody());
                }

            }


            System.out.println("Article bodies listed for the user on the interface:");
            for (Article article : allArticles) {
                if(article.getId() == 1031L){
                    System.out.println(helper.decrypt(article.getBody()));
                }

                if(article.getId() == 1002L){
                    System.out.println(helper.decrypt(article.getBody()));
                }

                if(article.getId() == 1203L){
                    System.out.println(helper.decrypt(article.getBody()));
                }

            }

        } catch (Exception e) {
            System.out.println("Error while listing articles: " + e.getMessage());
            passed = false;
        }

        return passed;
    }

}
