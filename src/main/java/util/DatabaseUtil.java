package util;

import config.DatabaseProperties;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseUtil {
    private static final DatabaseProperties properties = new DatabaseProperties();

    static {
        try {
            Class.forName(properties.getDriver());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Драйвер БД не найден", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                properties.getUrl(),
                properties.getUsername(),
                properties.getPassword()
        );
    }
}