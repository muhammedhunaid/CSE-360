package asu.cse360project.Testing;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;

import asu.cse360project.Group;
import asu.cse360project.Singleton;
import asu.cse360project.User;
import asu.cse360project.DashboardControllers.SearchArticlesController;
import asu.cse360project.DatabaseHelpers.DatabaseHelper;
import asu.cse360project.DatabaseHelpers.GroupArticlesHelper;
import asu.cse360project.DatabaseHelpers.UserHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Group 9
 * @version 1.0
 * @since 2022-02-20
 */
public class BackupRestoreTest {

    @Nested
    class BackUpAllTest
    {
        private SearchArticlesController searchArticlesController; //mock controller
        private Singleton dataMock; // Mock for the `Singleton` object
        private ObservableList<Group> groupsList; // Mock data for `groups_list`
        private User mockUser1;
        private User mockUser2;
        private User mockUser3;

        @BeforeEach
        public void setUp() {
            searchArticlesController = new SearchArticlesController(); // Replace with the actual class name
            dataMock = Singleton.getInstance();
            mockUser1 = new User("test_user", "Test", "", "User", "Test", "test@test.com", "student", "", 1);
            mockUser2 = new User("mock_user", "Mock", "", "User", "Mock", "mock@test.com", "instructor", "", 2);
            mockUser3 = new User("another_user", "Another", "", "User", "Another", "another@test.com", "admin", "", 3);
            dataMock.setAppUser(mockUser1);
            groupsList = FXCollections.observableArrayList();
            searchArticlesController.setGroups_list(groupsList);
            searchArticlesController.setData(dataMock);
        }

        @Test
        public void testGetAdminGroups_EmptyGroupList() throws SQLException {
            List<Integer> result = searchArticlesController.getAdminGroups();
            assertTrue(result.isEmpty(), "Result should be empty when groups_list is empty");
        }

        @Test
        public void testGetAdminGroups_GroupWithIdZero() throws SQLException {
            Group zerogroup = new Group(null, 0);
            groupsList.add(zerogroup); // Add a group with id = 0

            List<Integer> result = searchArticlesController.getAdminGroups();
            assertEquals(Collections.singletonList(0), result, "Result should contain only 0");
        }

        @Test
        public void testGetAdminGroups_UserIsAdmin() throws SQLException {
            ArrayList<User> g1_users = new ArrayList<>(); g1_users.add(mockUser1); g1_users.add(mockUser2);
            ArrayList<User> g2_users = new ArrayList<>(); g2_users.add(mockUser1); g1_users.add(mockUser3);
            Group g1 = new Group(null,1); g1.setAdmin_users(g1_users);
            Group g2 = new Group(null,2); g2.setAdmin_users(g2_users);
            groupsList.add(g1);
            groupsList.add(g2);

            List<Integer> result = searchArticlesController.getAdminGroups();
            assertEquals(Arrays.asList(1,2), result, "Result should contain groups where mockUser1 is an admin id = 1 and id = 2");
        }

        @Test
        public void testGetAdminGroups_MixedCase() throws SQLException {
            ArrayList<User> g1_users = new ArrayList<>(); g1_users.add(mockUser1); g1_users.add(mockUser2);
            ArrayList<User> g2_users = new ArrayList<>(); g2_users.add(mockUser2); g1_users.add(mockUser3);
            Group g0 = new Group(null,0);
            Group g1 = new Group(null,1); g1.setAdmin_users(g1_users);
            Group g2 = new Group(null,2); g2.setAdmin_users(g2_users);
            groupsList.add(g0);
            groupsList.add(g1);
            groupsList.add(g2); // User is not an admin

            List<Integer> result = searchArticlesController.getAdminGroups();
            assertEquals(Arrays.asList(0,1), result, "Result should contain id=0 and id=1");
        }
    }

    @Nested
    class GroupRestoreTest {

        DatabaseHelper dbh;
        GroupArticlesHelper gah;
        UserHelper uh;
        String backup_file = "bk1";
        Singleton data = Singleton.getInstance();

        @BeforeEach
        void setUp() throws Exception {
            dbh = new DatabaseHelper();
            dbh.connectToDatabase();
            gah = dbh.getGroupArticlesHelper();
            uh = dbh.getUser_helper();
            dbh.clearDatabase();     
            dbh.createTables();
            data.setDbHelpers(dbh);      
        }

        @Test //Test if restored group gives access to new user
        void testRestoreGeneralGroup1() throws Exception {
            ArrayList<User> allUser = new ArrayList<>();

            String uname = "TestUser1";
            uh.addUser(uname , "Password123#", "admin"); //create user
            User user1 = uh.getUser(uname); allUser.add(user1);

            gah.createGroup("Viewing Group1", false, user1.getId()); //make general group
            Group group = gah.getGroup("Viewing Group1");
            //this is were you would backup, we will assume that backing up the group will return it the same way that it started

            String uname2 = "TestUser2";
            uh.addUser(uname2 , "Password123#", "admin"); //create user
            User user2 = uh.getUser(uname2); allUser.add(user2);

            gah.deleteGroup(group.getId());

            gah.restoreGroup(group);
            Group restoredGroup = gah.getGroup(group.getId());
            ArrayList<User> usersAfterBackup = restoredGroup.getAdmin_users(); 
            usersAfterBackup.addAll(restoredGroup.getViewer_users());

            assertEquals(allUser, usersAfterBackup);
        }

        @Test //Test if restored group removes access from deleted users
        void testRestoreGeneralGroup2() throws Exception {
            ArrayList<User> allUser = new ArrayList<>();

            String uname = "TestUser1";
            uh.addUser(uname , "Password123#", "admin"); //create user
            User user1 = uh.getUser(uname); allUser.add(user1);

            String uname2 = "TestUser2";
            uh.addUser(uname2 , "Password123#", "admin"); //create user
            User user2 = uh.getUser(uname2);

            gah.createGroup("Viewing Group1", false, user1.getId()); //make general group
            Group group = gah.getGroup("Viewing Group1");

            gah.deleteGroup(group.getId());
            uh.deleteUser(user2);

            gah.restoreGroup(group);
            Group restoredGroup = gah.getGroup(group.getId());
            ArrayList<User> usersAfterBackup = restoredGroup.getAdmin_users(); 
            usersAfterBackup.addAll(restoredGroup.getViewer_users());

            assertEquals(allUser, usersAfterBackup);
        }

        @Test //Test if restored group remains the same if no change in users
        void testRestoreGeneralGroup4() throws SQLException {
            ArrayList<User> allUser = new ArrayList<>();

            String uname = "TestUser1";
            uh.addUser(uname , "Password123#", "admin"); //create user
            User user1 = uh.getUser(uname); allUser.add(user1);

            String uname2 = "TestUser2";
            uh.addUser(uname2 , "Password123#", "admin"); //create user
            User user2 = uh.getUser(uname2); allUser.add(user2);

            gah.createGroup("Viewing Group1", false, user1.getId()); //make general group
            Group group = gah.getGroup("Viewing Group1");

            gah.deleteGroup(group.getId());
            gah.restoreGroup(group);

            Group restoredGroup = gah.getGroup(group.getId());
            ArrayList<User> usersAfterBackup = restoredGroup.getAdmin_users(); 
            usersAfterBackup.addAll(restoredGroup.getViewer_users());

            assertEquals(allUser, usersAfterBackup);
        }

        @Test //Test combinations of adding and removing users with different access types
        void testRestoreGenralGroup4() throws SQLException {
            ArrayList<User> allUser = new ArrayList<>();

            String uname = "TestUser1";
            uh.addUser(uname , "Password123#", "admin"); //create user
            User user1 = uh.getUser(uname); allUser.add(user1);

            String uname2 = "TestUser2";
            uh.addUser(uname2 , "Password123#", "student"); //create user
            User user2 = uh.getUser(uname2);

            String uname3 = "TestUser3";
            uh.addUser(uname3 , "Password123#", "instructor"); //create user
            User user3 = uh.getUser(uname3); allUser.add(user3);

            gah.createGroup("Viewing Group1", false, user1.getId()); //make general group
            Group group = gah.getGroup("Viewing Group1");

            String uname4 = "TestUser4";
            uh.addUser(uname4 , "Password123#", "student"); //create user
            User user4 = uh.getUser(uname4); allUser.add(user4);

            //delete user
            uh.deleteUser(user2);

            gah.deleteGroup(group.getId());
            gah.restoreGroup(group);
            
            Group restoredGroup = gah.getGroup(group.getId());
            ArrayList<User> usersAfterBackup = restoredGroup.getAdmin_users(); 
            usersAfterBackup.addAll(restoredGroup.getViewer_users());

            assertEquals(allUser, usersAfterBackup);
        }

        @Test //restores orginal users if no change in system 2 admins and 1 viewer
        void testRestoreSpecialGroup1() throws SQLException {
            ArrayList<User> allUser = new ArrayList<>();

            String uname = "ATestUser1";
            uh.addUser(uname , "Password123#", "admin"); //create user
            User user1 = uh.getUser(uname); allUser.add(user1);

            String uname2 = "BTestUser2";
            uh.addUser(uname2 , "Password123#", "student"); //create user
            User user2 = uh.getUser(uname2); allUser.add(user2);

            String uname3 = "CTestUser3";
            uh.addUser(uname3 , "Password123#", "instructor"); //create user
            User user3 = uh.getUser(uname3); allUser.add(user3);

            //add group
            gah.createGroup("SAG", true, user1.getId());
            Group group = gah.getGroup("SAG");
            gah.linkSAG(user2.getId(), group.getId(), false); 
            gah.linkSAG(user3.getId(), group.getId(), true); 
            group = gah.getGroup("SAG"); //update users

            //delete and restore group
            gah.deleteGroup(group.getId());
            gah.restoreGroup(group);
            
            Group restoredGroup = gah.getGroup("SAG");
            ArrayList<User> usersAfterBackup = restoredGroup.getAdmin_users(); 
            usersAfterBackup.addAll(restoredGroup.getViewer_users());

            Collections.sort(allUser);
            Collections.sort(usersAfterBackup);

            assertEquals(allUser, usersAfterBackup);
        }

        @Test //restores orginal users if except ones deleted - 1 admin deleted of 2 admins and 1 viewer
        void testRestoreSpecialGroup2() throws SQLException {
            ArrayList<User> allUser = new ArrayList<>();

            String uname = "ATestUser1";
            uh.addUser(uname , "Password123#", "admin"); //create user
            User user1 = uh.getUser(uname);

            String uname2 = "BTestUser2";
            uh.addUser(uname2 , "Password123#", "student"); //create user
            User user2 = uh.getUser(uname2); allUser.add(user2);

            String uname3 = "CTestUser3";
            uh.addUser(uname3 , "Password123#", "instructor"); //create user
            User user3 = uh.getUser(uname3); allUser.add(user3);

            //add group
            gah.createGroup("SAG", true, user1.getId());
            Group group = gah.getGroup("SAG");
            gah.linkSAG(user2.getId(), group.getId(), false); 
            gah.linkSAG(user3.getId(), group.getId(), true); 
            group = gah.getGroup("SAG"); //update users

            //delete and restore group
            gah.deleteGroup(group.getId());

            uh.deleteUser(user1);

            gah.restoreGroup(group);
            
            Group restoredGroup = gah.getGroup("SAG");
            ArrayList<User> usersAfterBackup = restoredGroup.getAdmin_users(); 
            usersAfterBackup.addAll(restoredGroup.getViewer_users());

            Collections.sort(allUser);
            Collections.sort(usersAfterBackup);

            assertEquals(allUser, usersAfterBackup);
        }

        @Test //restores orginal users when users added to system
        void testRestoreSpecialGroup3() throws SQLException {
            ArrayList<User> allUser = new ArrayList<>();

            String uname = "ATestUser1";
            uh.addUser(uname , "Password123#", "admin"); //create user
            User user1 = uh.getUser(uname); allUser.add(user1);

            String uname2 = "BTestUser2";
            uh.addUser(uname2 , "Password123#", "student"); //create user
            User user2 = uh.getUser(uname2); allUser.add(user2);

            //add group
            gah.createGroup("SAG", true, user1.getId());
            Group group = gah.getGroup("SAG");
            gah.linkSAG(user2.getId(), group.getId(), false); 
            group = gah.getGroup("SAG"); //update users

            //delete and restore group
            gah.deleteGroup(group.getId());
            
            String uname3 = "CTestUser3";
            uh.addUser(uname3 , "Password123#", "instructor"); //create user

            gah.restoreGroup(group);
            
            Group restoredGroup = gah.getGroup("SAG");
            ArrayList<User> usersAfterBackup = restoredGroup.getAdmin_users(); 
            usersAfterBackup.addAll(restoredGroup.getViewer_users());

            Collections.sort(allUser);
            Collections.sort(usersAfterBackup);

            assertEquals(allUser, usersAfterBackup);
        }

    }
}
