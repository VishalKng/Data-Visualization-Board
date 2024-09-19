package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    Connection connect() {
        // MySQL connection string
        String url = "jdbc:mysql://localhost:3306/datavisualization";
        String user = "root";
        String password = "Trigger.11";

        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    public void createTable() {
        // SQL statement for creating a new table
        String sql = "CREATE TABLE IF NOT EXISTS Data ("
                + "id INT AUTO_INCREMENT PRIMARY KEY,"
                + "item VARCHAR(255) NOT NULL,"
                + "amount DOUBLE NOT NULL"
                + ");";

        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement()) {
            // create a new table
            stmt.execute(sql);
            System.out.println("Table created successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertData(String item, double amount) {
        String sql = "INSERT INTO Data(item, amount) VALUES(?,?)";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, item);
            pstmt.setDouble(2, amount);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void deleteAllData() {
        String sql = "DELETE FROM Data";
        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteRow(String itemName) {
        String sql = "DELETE FROM Data WHERE item = ?";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, itemName);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
