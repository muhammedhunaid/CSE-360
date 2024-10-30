package asu.cse360project;

import asu.cse360project.DatabaseHelpers.DatabaseHelper;
import asu.cse360project.DatabaseHelpers.GroupArticlesHelper;
import asu.cse360project.DatabaseHelpers.UserHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.SQLException;
import java.util.*;

public class TestScript {

    public static void main(String[] args) throws SQLException {
        System.out.println("Welcome to Test Script");

        DatabaseHelper databaseManager = new DatabaseHelper();
        databaseManager.connectToDatabase();

        UserHelper userManager = databaseManager.getUser_helper();
        User user = new User("adminUser", "adminFirstname", "admin", "abc@12343");

        GroupArticlesHelper articleManager = databaseManager.getGroupArticlesHelper();

        System.out.println("Starting testing script now...");
        System.out.println("\n------------------------------------\n");



//        int i = 0;
        boolean start = true;
        int passed = 0;
        int failed = 0;
        ArrayList<Integer> failedTestCases = new ArrayList<>();

        System.out.println("Inserting User");
//        databaseManager.insertUser("INVITE", "user");

        if(TestCase1(databaseManager, userManager)){
            System.out.println("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
            System.out.println("Test case PASSED");
        }else {
            System.out.println("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
            System.out.println("Test case FAILED!!");
            failedTestCases.add(1);
        }

        if(TestCase2(databaseManager, userManager)){
            System.out.println("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
            System.out.println("Test case PASSED");
        }else {
            System.out.println("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
            System.out.println("Test case FAILED!!");
            failedTestCases.add(2);
        }

        if(TestCase3(databaseManager, articleManager)){
            System.out.println("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
            System.out.println("Test case PASSED");
        }else {
            System.out.println("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
            System.out.println("Test case FAILED!!");
            failedTestCases.add(3);
        }



        System.out.println("List of Failed Test Cases: ");
        for(Integer i : failedTestCases){
            System.out.println(i);
        }

    }


    private static boolean TestCase1(DatabaseHelper databaseManager, UserHelper userManager) throws SQLException {
        System.out.println("\n------------------------------------\n");
        System.out.println("Adding user...");
        userManager.addUser("testname1", "Strong_Pass123", "student");
        userManager.addUser("Lynn Carter", "abc@128_ghi", "instructor");
        userManager.addUser("student2", "abcdeg@1234", "student");
        userManager.addUser("adminName", "strong1234_", "admin");
        userManager.addUser("skyPilot", "Fi$h12Frog!", "admin");
        userManager.addUser("bookworm92", "Red@22Drum", "student");
        userManager.addUser("techGuru", "W@terM44rk", "instructor");
        userManager.addUser("swiftLearner", "QuickS@lver9", "student");
        userManager.addUser("codeMaster", "Bl@ckHawk4!", "admin");
        userManager.addUser("profSmith", "Alpha@127", "instructor");



        System.out.println("\n------------------------------------\n");
        System.out.println("Printing all the current active users");
        ObservableList<User> all_user = FXCollections.observableArrayList();
        userManager.ListUsers(all_user);

        for (User user : all_user) {
            System.out.println(user);
        }

        //deleting Users
        System.out.println("\n------------------------------------\n");
        System.out.println("Deleting the users...");
        userManager.deleteUser("student3");
        userManager.deleteUser("student2");
        userManager.deleteUser("adminName");
        userManager.deleteUser("Lynn Carter");
        userManager.deleteUser("student2");
        userManager.deleteUser("testname1");
        userManager.deleteUser("seaSurfer");
        userManager.deleteUser("testname1");
        userManager.deleteUser("dragonSoul");
        userManager.deleteUser("techGuru");
        userManager.deleteUser("jungleRider");


        System.out.println("\n------------------------------------\n");
        System.out.println("Printing all the current active users");
        all_user = FXCollections.observableArrayList();
        userManager.ListUsers(all_user);

        for (User user : all_user) {
            System.out.println(user);
        }

        return databaseManager.isDatabaseEmpty();
    }

    private static boolean TestCase2(DatabaseHelper databaseManager, UserHelper userManager) throws SQLException {
        System.out.println("\n------------------------------------\n");
        System.out.println("Adding user...");
        userManager.addUser("techGuru", "W@terM44rk", "instructor");
        userManager.addUser("student1", "Strong_Pass123", "student");
        userManager.addUser("professor1", "abc@128_ghi", "instructor");
        userManager.addUser("student2", "abcdeg@1234", "student");
        userManager.addUser("mountainEcho", "Tiger@88Peak", "student");
        userManager.addUser("student4", "isk834eyi&bkF", "student");
        userManager.addUser("forestWhisper", "Leaf#33Wind", "student");
        userManager.addUser("profJones", "Logic@99Base", "instructor");
        userManager.addUser("oceanWave", "Blue$42Sky", "student");
        userManager.addUser("desertFox", "Sand@45Dune", "student");
        userManager.addUser("cityWanderer", "Light$22Path", "student");
        userManager.addUser("profJones", "Logic@99Base", "instructor");


        System.out.println("\n------------------------------------\n");
        System.out.println("Printing all the current active users");
        ObservableList<User> all_user = FXCollections.observableArrayList();
        userManager.ListUsers(all_user);
        for (User user : all_user) {
            System.out.println(user);
        }

        //Displaying all the Users seen by the Admin
        System.out.println("\n------------------------------------\n");
        System.out.println("Printing all the information seen by the admins...");
        userManager.displayUsersByAdmin();
        for (User user : all_user) {
            System.out.println(user);
        }

        //change role of instructor
        System.out.println("\n------------------------------------\n");
        System.out.println("Changing the role of the users...");
        if(!userManager.changeRole("profJones", "admin")){
            return false;
        }

        //false expected
        if(userManager.changeRole("Lynn Carter", "student")){
            return false;
        }

        if(!userManager.changeRole("student2", "admin")){
            return false;
        }

        if(!userManager.changeRole("professor1", "admin")){
            return false;
        }

        if(!userManager.changeRole("jungleRider", "student")){
            return false;
        }

        if(!userManager.changeRole("professor1", "admin")){
            return false;
        }

        System.out.println("\n------------------------------------\n");
        System.out.println("Printing all the current active users");
        all_user = FXCollections.observableArrayList();
        userManager.ListUsers(all_user);

        for (User user : all_user) {
            System.out.println(user);
        }

        return true;
    }

    private static boolean TestCase3(DatabaseHelper databaseManager, GroupArticlesHelper articleManager) throws SQLException {
        System.out.println("\n------------------------------------\n");
        System.out.println("Adding user...");

        articleManager.addArticle(1001L, "Advances in AI Research", "Exploring recent developments in artificial intelligence.", "AI, machine learning, neural networks", "The field of artificial intelligence has seen rapid advancements in recent years...", "advanced", "Dr. A. Smith, Prof. B. Johnson", "public", new ArrayList<>(Arrays.asList(1, 2, 3)), new ArrayList<>(Arrays.asList(101L, 102L)));
        articleManager.addArticle(1002L, "Environmental Impact of Urbanization", "A review of how urban growth affects ecosystems.", "environment, urbanization, sustainability", "Urbanization is one of the most significant drivers of environmental change today...", "intermediate", "Dr. C. Adams, Dr. D. Lee", "restricted", new ArrayList<>(Arrays.asList(4, 5)), new ArrayList<>(Arrays.asList(103L, 104L)));
        articleManager.addArticle(1003L, "Quantum Computing Basics", "Introduction to quantum computing concepts and applications.", "quantum computing, qubits, quantum mechanics", "Quantum computing offers a revolutionary approach to solving complex problems...", "beginner", "Dr. E. Perez", "public", new ArrayList<>(List.of(6)), new ArrayList<>(Arrays.asList(105L, 106L)));
        articleManager.addArticle(1004L, "Renewable Energy Sources", "Examining different renewable energy sources for a sustainable future.", "renewable energy, solar, wind, hydro", "Renewable energy is a critical component of the global strategy to combat climate change...", "advanced", "Prof. F. Morgan", "public", new ArrayList<>(Arrays.asList(1, 7, 8)), new ArrayList<>(List.of(107L)));
        articleManager.addArticle(1005L, "Cybersecurity in Modern Infrastructure", "Challenges and solutions in protecting digital infrastructure.", "cybersecurity, digital security, encryption", "As technology integrates into every aspect of modern infrastructure, cybersecurity becomes paramount...", "advanced", "Dr. G. White, Dr. H. Yamada", "restricted", new ArrayList<>(Arrays.asList(2, 9)), new ArrayList<>(Arrays.asList(108L, 109L)));
        articleManager.addArticle(1006L, "Health Benefits of Regular Exercise", "A look at how exercise promotes long-term health.", "health, exercise, fitness", "Regular exercise has been shown to improve physical and mental health in various studies...", "beginner", "Dr. I. Khan", "public", new ArrayList<>(List.of(10)), new ArrayList<>(List.of(110L)));
        articleManager.addArticle(1007L, "Blockchain Technology Applications", "Applications of blockchain beyond cryptocurrency.", "blockchain, technology, decentralized", "Blockchain technology offers a decentralized approach to digital transactions and data management...", "intermediate", "Prof. J. Nguyen", "public", new ArrayList<>(Arrays.asList(3, 4)), new ArrayList<>(Arrays.asList(111L, 112L)));
        articleManager.addArticle(1008L, "Space Exploration Milestones", "Historical achievements in space exploration.", "space, NASA, exploration", "Humanity's venture into space has achieved significant milestones over the decades...", "intermediate", "Dr. K. Patel, Dr. L. Simmons", "restricted", new ArrayList<>(Arrays.asList(5, 6, 7)), new ArrayList<>(List.of(113L)));
        articleManager.addArticle(1009L, "Genetic Engineering and Ethics", "Ethical considerations in genetic modification.", "genetics, ethics, CRISPR", "With advancements in genetic engineering, ethical debates have emerged on its applications...", "advanced", "Dr. M. Chen", "restricted", new ArrayList<>(Arrays.asList(8, 9)), new ArrayList<>(Arrays.asList(114L, 115L)));
        articleManager.addArticle(1010L, "Impact of Social Media on Communication", "How social media changes interpersonal communication.", "social media, communication, digital", "Social media platforms have transformed how individuals communicate and interact globally...", "beginner", "Prof. N. Wilson", "public", new ArrayList<>(List.of(10)), new ArrayList<>(Arrays.asList(116L, 117L)));


        System.out.println("\n------------------------------------\n");
        System.out.println("Printing all the current active users");
        ObservableList<Article> all_article = FXCollections.observableArrayList();
        articleManager.ListArticles(all_article);
        for (Article article : all_article) {
            System.out.println(article);
        }



        return true;
    }

}
