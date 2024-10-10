package asu.cse360project;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.io.IOException;
//
public class Database {
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/cse360project";
    private static final String USER = "root";
    private static final String PASSWORD = "12345678";
    public static PreparedStatement pstmt;
    //
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
    //
    public static void resetDatabase() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            // Drop existing tables
            stmt.execute("DROP TABLE IF EXISTS users");

            // Create users table with the correct schema
            stmt.execute("CREATE TABLE users (" +
                         "id INT AUTO_INCREMENT PRIMARY KEY, " +
                         "username VARCHAR(255) NOT NULL, " +
                         "password BLOB NOT NULL, " +
                         "email VARCHAR(255), " +
                         "one_time_flag TINYINT(1), " +
                         "one_time_expiry DATETIME, " +
                         "first_name VARCHAR(255), " +
                         "middle_name VARCHAR(255), " +
                         "last_name VARCHAR(255), " +
                         "preferred_name VARCHAR(255), " +
                         "role_admin TINYINT(1), " +
                         "role_student TINYINT(1), " +
                         "role_instructor TINYINT(1), " +
                         "topics ENUM('beginner', 'intermediate', 'advanced', 'expert'), " +
                         "invite_code VARCHAR(255))");

            // Insert initial admin user
            stmt.execute("INSERT INTO users (username, password, email, role_admin, one_time_flag) " +
                         "VALUES ('admin', 's!IAS@05', 'admin@example.com', TRUE, FALSE)");

             // Query to set the App.user
             String sql = "SELECT username, password FROM users WHERE role_admin = 1 LIMIT 1";
             pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery();

             if (rs.next()) {
                 App.user = new User(rs.getString("username"), rs.getString("password"));
             }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}