/**
     
<p> User Class. </p>
<p> Description: This class represents a user in the system. It contains the user's username, role, and first name.
This also contains methods to check if the user is an admin, instructor, or student.
It contains the getter and setter methods for the user's username, role, and first name. </p>
<p> Copyright: Tu35 Team © 2024 </p>
@author Tu35 Team
@version 1.00        2024-10-09 Phase 1 implementation of the user management controller
*/

package asu.cse360project;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.IOException;

//
public class User {
    private String username;
    private String password;
    private String loginRole;

    //Create a default constructor
    public User() {
        this.username = null;
    }

    public User(String username) {
        this.username = username;
        loadUserFromDatabase();
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User(String username, String password, String loginRole) {
        this.username = username;
        this.password = password;
        this.loginRole = loginRole;
    }

    private void loadUserFromDatabase() {
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT password FROM users WHERE username = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                this.password = rs.getString("password");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean isAdmin() {
        return checkRole("role_admin");
    }

    public boolean isInstructor() {
        return checkRole("role_instructor");
    }

    public boolean isStudent() {
        return checkRole("role_student");
    }

    private boolean checkRole(String roleColumn) {
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT " + roleColumn + " FROM users WHERE username = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getBoolean(roleColumn);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

public void resetLoginRole() {
    try (Connection conn = Database.getConnection()) {
        String sql = "UPDATE users SET login_role = NULL WHERE username = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, this.username);
        pstmt.executeUpdate();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

public void setLoginRole(String role) {
    try (Connection conn = Database.getConnection()) {
        String sql = "UPDATE users SET login_role = ? WHERE username = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, role);
        pstmt.setString(2, this.username);
        pstmt.executeUpdate();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

public String getLoginRole() {
    try (Connection conn = Database.getConnection()) {
        String sql = "SELECT login_role FROM users WHERE username = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, this.username);
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            return rs.getString("login_role");
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return "";
}
}