package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DatabaseUtil {

    private static final Properties properties = new Properties();
    private static String URL;
    private static String USERNAME;
    private static String PASSWORD;
    private static String DRIVER;

    static {
        loadProperties();
        initializeDriver();
    }

    private static void loadProperties() {
        try (InputStream input = DatabaseUtil.class.getClassLoader()
                .getResourceAsStream("database.properties")) {

            if (input == null) {
                throw new RuntimeException("Файл database.properties не найден в classpath");
            }

            properties.load(input);
            URL = properties.getProperty("db.url");
            USERNAME = properties.getProperty("db.username");
            PASSWORD = properties.getProperty("db.password");
            DRIVER = properties.getProperty("db.driver");
            if (URL == null || USERNAME == null || PASSWORD == null || DRIVER == null) {
                throw new RuntimeException("Не все обязательные настройки БД указаны в database.properties");
            }

            System.out.println(" Настройки БД загружены из properties файла");

        } catch (IOException e) {
            throw new RuntimeException("Ошибка загрузки database.properties", e);
        }
    }


    private static void initializeDriver() {
        try {
            Class.forName(DRIVER);
            System.out.println("Драйвер БД зарегистрирован: " + DRIVER);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Драйвер БД не найден: " + DRIVER, e);
        }
    }


    public static Connection getConnection() throws SQLException {
        try {
            Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println(" Подключение к БД установлено");
            return connection;
        } catch (SQLException e) {
            System.err.println("Ошибка подключения к БД: " + e.getMessage());
            throw e;
        }
    }

    public static void initialize() {
        System.out.println("DatabaseUtil инициализирован");
    }

    public static void cleanup() {
        System.out.println(" DatabaseUtil очищен");
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
}