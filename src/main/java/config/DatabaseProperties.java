package config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DatabaseProperties {
    private final Properties properties;

    public DatabaseProperties() {
        this.properties = loadProperties();
    }

    private Properties loadProperties() {
        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream("database.properties")) {

            if (input == null) {
                throw new RuntimeException("Файл database.properties не найден в classpath");
            }

            props.load(input);
            validateProperties(props);

        } catch (IOException e) {
            throw new RuntimeException("Ошибка загрузки database.properties", e);
        }
        return props;
    }

    private void validateProperties(Properties props) {
        String[] required = {"db.url", "db.username", "db.password", "db.driver"};
        for (String key : required) {
            if (props.getProperty(key) == null) {
                throw new RuntimeException("Обязательная настройка отсутствует: " + key);
            }
        }
    }

    public String getUrl() {
        return properties.getProperty("db.url");
    }

    public String getUsername() {
        return properties.getProperty("db.username");
    }

    public String getPassword() {
        return properties.getProperty("db.password");
    }

    public String getDriver() {
        return properties.getProperty("db.driver");
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }
}