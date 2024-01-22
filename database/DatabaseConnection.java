package database;

import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static final String url = "jdbc:mysql://localhost:3306/data";
    private static final String username = "root";
    private static final String password = "lethitham";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Unable to connect to database. Please run the application again!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            e.printStackTrace();
            System.exit(1);
        }
    }
    private static Connection instance;

    static {
        instance = DatabaseConnection.getConnection();
    }

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("select * from users where full_name like '%L_%'")) {

            while (resultSet.next()) {
                System.out.println(resultSet.getString("full_name"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static Connection getConnectionInstance() {
        return instance;
    }
}
