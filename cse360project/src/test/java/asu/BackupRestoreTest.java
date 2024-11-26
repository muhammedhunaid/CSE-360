package asu;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.mockito.Mockito;

import asu.cse360project.Group;
import asu.cse360project.Singleton;
import asu.cse360project.User;
import asu.cse360project.DashboardControllers.SearchArticlesController;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GroupTest {

    @Test
    void testAdminOfGroups_AllGroupsAdmin() throws SQLException {
        // Mock 'groups_list' as an array
        ArrayList<Group> mockGroupsArray = new ArrayList<>();
        Group group1 = mock(Group.class);
        Group group2 = mock(Group.class);

        // Mock admin users for each group
        User currentUser = mock(User.class);
        when(currentUser.getUsername()).thenReturn("user1");

        ArrayList<User> adminUsers = new ArrayList<>(List.of(currentUser));
        when(group1.getAdmin_users()).thenReturn(adminUsers);
        when(group2.getAdmin_users()).thenReturn(adminUsers);

        // Add groups to the mock array
        mockGroupsArray.add(group1);
        mockGroupsArray.add(group2);

        // Mock 'data.getAppUser()' to return the current user
        Singleton dataMock = mock(Singleton.class);
        when(dataMock.getAppUser()).thenReturn(currentUser);

        // Act
        SearchArticlesController controller = new SearchArticlesController();
        boolean result = controller.adminOfGroups();

        // Assert
        assertTrue(result, "Expected true since the user is an admin of all groups.");
    }

    @Test
    void testAdminOfGroups_OneGroupNotAdmin() throws SQLException {
        // Mock 'groups_list' as an array
        ArrayList<Group> mockGroupsArray = new ArrayList<>();
        Group group1 = mock(Group.class);
        Group group2 = mock(Group.class);

        // Mock admin users for each group
        User currentUser = mock(User.class);
        when(currentUser.getUsername()).thenReturn("user1");

        ArrayList<User> adminUsers = new ArrayList<>(List.of(currentUser));
        ArrayList<User> adminUsers2 = new ArrayList<>(); // Empty admin list for group2
        when(group1.getAdmin_users()).thenReturn(adminUsers);
        when(group2.getAdmin_users()).thenReturn(adminUsers2);

        // Add groups to the mock array
        mockGroupsArray.add(group1);
        mockGroupsArray.add(group2);

        // Mock 'data.getAppUser()' to return the current user
        Singleton dataMock = mock(Singleton.class);
        when(dataMock.getAppUser()).thenReturn(currentUser);

        // Act
        SearchArticlesController controller = new SearchArticlesController();
        boolean result = controller.adminOfGroups();

        assertFalse(result, "Expected false since the user is not an admin of all groups.");
    }

    @Test
    void testAdminOfGroups_NullAdminUsers() throws SQLException {
        // Mock 'groups_list' as an array
        ArrayList<Group> mockGroupsArray = new ArrayList<>();
        Group group1 = mock(Group.class);
        Group group2 = mock(Group.class);

        // Mock admin users for each group
        User currentUser = mock(User.class);
        when(currentUser.getUsername()).thenReturn("user1");

        ArrayList<User> adminUsers2 = new ArrayList<>(List.of(currentUser));
        when(group1.getAdmin_users()).thenReturn(null);
        when(group2.getAdmin_users()).thenReturn(adminUsers2);

        // Add groups to the mock array
        mockGroupsArray.add(group1);
        mockGroupsArray.add(group2);

        // Mock 'data.getAppUser()' to return the current user
        Singleton dataMock = mock(Singleton.class);
        when(dataMock.getAppUser()).thenReturn(currentUser);

        // Act
        SearchArticlesController controller = new SearchArticlesController();
        boolean result = controller.adminOfGroups();

        // Assert
        assertFalse(result, "Expected false since one group has null admin_users.");
    }

    @Test
    void testAdminOfGroups_EmptyGroupsList() throws SQLException {

        // Mock 'groups_list' as an array
        ArrayList<Group> mockGroupsArray = new ArrayList<>();

        // Mock admin users for each group
        User currentUser = mock(User.class);
        when(currentUser.getUsername()).thenReturn("user1");

        // Mock 'data.getAppUser()' to return the current user
        Singleton dataMock = mock(Singleton.class);
        when(dataMock.getAppUser()).thenReturn(currentUser);

        // Act
        SearchArticlesController controller = new SearchArticlesController();
        boolean result = controller.adminOfGroups();

        assertTrue(result, "Expected false since the groups_list is empty.");
    }
}
