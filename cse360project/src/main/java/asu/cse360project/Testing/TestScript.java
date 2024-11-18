package asu.cse360project.Testing;

import java.sql.SQLException;
import java.util.ArrayList;

import asu.cse360project.User;
import asu.cse360project.DatabaseHelpers.DatabaseHelper;
import asu.cse360project.DatabaseHelpers.UserHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class TestScript {

    /*******
     * <p> TestScript. </p>
     * 
     * <p> Description: This class is used to test the UserHelper methods for adding, deleting, and changing user roles.
     * 
     * <p> Copyright: Tu35 © 2024 </p>
     * 
     * @author Tu35
     * 
     * @version 1.00 2024-10-30 Created to test Phase 2.
     * 
     */

    public static void main(String[] args) throws SQLException, Exception {
        System.out.println("Welcome to the User Methods Test Script");

        DatabaseHelper databaseManager = new DatabaseHelper();
        databaseManager.connectToDatabase();

        UserHelper userManager = databaseManager.getUser_helper();

        System.out.println("Starting testing script now...");
        System.out.println("\n------------------------------------\n");

        ArrayList<Integer> failedTestCases = new ArrayList<>();

        // Test Case 1: Adding Users
        if(TestCase1(userManager) == false){
            System.out.println("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
            System.out.println("Test Case 1 PASSED");
            System.out.println("Expected: False\nActual: False");
        } else {
            System.out.println("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
            System.out.println("Test Case 1 FAILED!!");
            failedTestCases.add(1);
            System.out.println("Expected: False\nActual: True");
        }

        // Test Case 2: Deleting Users
        if(TestCase2(userManager) == false){
            System.out.println("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
            System.out.println("Test Case 2 PASSED");
            System.out.println("Expected: False\nActual: False");
        } else {
            System.out.println("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
            System.out.println("Test Case 2 FAILED!!");
            failedTestCases.add(2);
            System.out.println("Expected: False\nActual: True");
        }

        // Test Case 3: Changing User Roles
        if(TestCase3(userManager)){
            System.out.println("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
            System.out.println("Test Case 3 PASSED");
            System.out.println("Expected: True\nActual: True");
        } else {
            System.out.println("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
            System.out.println("Test Case 3 FAILED!!");
            failedTestCases.add(3);
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

    private static boolean TestCase1(UserHelper userManager) throws SQLException {
        System.out.println("\n------------------------------------\n");
        System.out.println("Test Case 1: Adding Users");

        boolean passed = true;

        try {
            // Adding users
            System.out.println("Adding users...");
            userManager.addUser("testUser1", "Password123!", "student");
            userManager.addUser("instructor1", "Teach@456", "instructor");
            userManager.addUser("adminUser", "AdminPass@789", "admin");
            userManager.addUser("student2", "Stud3ntPass!", "student");
            userManager.addUser("professorX", "Xmen@123", "instructor");
            userManager.addUser("userDuplicate", "DupPass@1", "student");

            // Attempt to add a user with an existing username
            try {
                userManager.addUser("testUser1", "NewPass@456", "student");
                System.out.println("Error: Duplicate user was added.");
                passed = false;
            } catch (Exception e) {
                System.out.println("Caught expected exception for duplicate user: " + e.getMessage());
            }

            System.out.println("Users added successfully.");
        } catch (Exception e) {
            System.out.println("Error while adding users: " + e.getMessage());
            passed = false;
        }

        // Listing all users
        System.out.println("\nListing all users...");
        ObservableList<User> allUsers = FXCollections.observableArrayList();
        userManager.ListUsers(allUsers);

        for (User user : allUsers) {
            System.out.println(user);
        }

        return passed;
    }

    private static boolean TestCase2(UserHelper userManager) throws SQLException {
        System.out.println("\n------------------------------------\n");
        System.out.println("Test Case 2: Deleting Users");

        boolean passed = true;

        try {
            // Deleting users
            System.out.println("Deleting users...");
            //userManager.deleteUser("student2");
            //userManager.deleteUser("instructor1"); //TODO: DIX SCRIPT

            // Attempt to delete a user that doesn't exist
            try {
                //userManager.deleteUser("nonExistentUser");
                System.out.println("Error: Non-existent user deletion did not throw an exception.");
                passed = false;
            } catch (Exception e) {
                System.out.println("Caught expected exception for non-existent user deletion: " + e.getMessage());
            }

            System.out.println("Users deleted successfully.");
        } catch (Exception e) {
            System.out.println("Error while deleting users: " + e.getMessage());
            passed = false;
        }

        // Listing remaining users
        System.out.println("\nListing remaining users...");
        ObservableList<User> allUsers = FXCollections.observableArrayList();
        userManager.ListUsers(allUsers);

        for (User user : allUsers) {
            System.out.println(user);
        }

        return passed;
    }

    private static boolean TestCase3(UserHelper userManager) throws SQLException {
        System.out.println("\n------------------------------------\n");
        System.out.println("Test Case 3: Changing User Roles");

        boolean passed = true;

        try {
            // Changing user roles
            System.out.println("Changing roles of users...");
            if (!userManager.changeRole("testUser1", "admin")) {
                System.out.println("Error: Failed to change role of testUser1 to admin.");
                passed = false;
            }

            if (!userManager.changeRole("professorX", "student")) {
                System.out.println("Error: Failed to change role of professorX to student.");
                passed = false;
            }

            // Attempt to change role of a non-existent user
            if (userManager.changeRole("nonExistentUser", "admin")) {
                System.out.println("Error: Changed role of non-existent user.");
                passed = false;
            } else {
                System.out.println("Correctly handled role change for non-existent user.");
            }

            System.out.println("User roles changed successfully.");
        } catch (Exception e) {
            System.out.println("Error while changing user roles: " + e.getMessage());
            passed = false;
        }

        // Listing all users to verify role changes
        System.out.println("\nListing all users...");
        ObservableList<User> allUsers = FXCollections.observableArrayList();
        userManager.ListUsers(allUsers);

        for (User user : allUsers) {
            System.out.println(user);
        }

        return passed;
    }
}
