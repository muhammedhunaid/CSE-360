package asu.cse360project.Testing;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;

import asu.cse360project.Group;
import asu.cse360project.Singleton;
import asu.cse360project.User;
import asu.cse360project.DashboardControllers.SearchArticlesController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.mockito.Mockito;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BackupRestoreTesting {


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
            mockUser1 = new User("test_user", null, null, null, 0);
            mockUser2 = new User("mock_user", null, null, null, 0);
            mockUser3 = new User("another_user", null, null, null, 0);
            dataMock.setAppUser(mockUser1);
            groupsList = FXCollections.observableArrayList();
            searchArticlesController.setGroups_list(groupsList); // Assign groups_list mock
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
        public void testGetAdminGroups_NullAdminUsers() throws SQLException {
            groupsList.add(new Group(null, 1)); // admin_users is null
            List<Integer> result = searchArticlesController.getAdminGroups();
            assertTrue(result.isEmpty(), "Result should be empty when admin_users is null");
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
