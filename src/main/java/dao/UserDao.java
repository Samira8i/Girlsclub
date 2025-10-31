package dao;

import model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDao {
    private final Connection connection;

    private static final String USER_TABLE_CREATE_QUERY =
            "CREATE TABLE IF NOT EXISTS users (" +
                    "id BIGSERIAL PRIMARY KEY, " +
                    "username VARCHAR(100) NOT NULL UNIQUE, " +
                    "password_hash VARCHAR(512) NOT NULL, " +
                    "salt VARCHAR(50) NOT NULL, " + // Добавляем поле для соли
                    "avatar_url VARCHAR(500)" +
                    ");";

    private static final String CREATE_USER_QUERY =
            "INSERT INTO users (username, password_hash, salt, avatar_url) " +
                    "VALUES (?, ?, ?, ?) RETURNING id;";

    private static final String FIND_BY_USERNAME_QUERY =
            "SELECT * FROM users WHERE username = ?;";

    private static final String FIND_BY_ID_QUERY =
            "SELECT * FROM users WHERE id = ?;";

    private static final String FIND_ALL_QUERY =
            "SELECT * FROM users ORDER BY id;";

    private static final String UPDATE_AVATAR_QUERY =
            "UPDATE users SET avatar_url = ? WHERE id = ?;";

    private static final String DELETE_USER_QUERY =
            "DELETE FROM users WHERE id = ?;";

    public UserDao(Connection connection) {
        this.connection = connection;
        initializeTable();
    }

    private void initializeTable() {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(USER_TABLE_CREATE_QUERY);
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при создании таблицы users", e);
        }
    }

    public boolean create(User user) {
        try (PreparedStatement statement = connection.prepareStatement(CREATE_USER_QUERY)) {
            // Пароль УЖЕ хэширован с солью в UserService
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPasswordHash());
            statement.setString(3, user.getSalt());
            statement.setString(4, user.getAvatarUrl());

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                user.setId(resultSet.getLong(1));
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при создании пользователя: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public User findByUsername(String username) {
        try (PreparedStatement statement = connection.prepareStatement(FIND_BY_USERNAME_QUERY)) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return mapResultSetToUser(resultSet);
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при поиске пользователя: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public User findById(Long id) {
        try (PreparedStatement statement = connection.prepareStatement(FIND_BY_ID_QUERY)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return mapResultSetToUser(resultSet);
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при поиске пользователя по ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public List<User> findAll() {
        List<User> users = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(FIND_ALL_QUERY);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                users.add(mapResultSetToUser(resultSet));
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при получении всех пользователей: " + e.getMessage());
            e.printStackTrace();
        }
        return users;
    }

    public boolean updateAvatar(Long userId, String avatarUrl) {
        try (PreparedStatement statement = connection.prepareStatement(UPDATE_AVATAR_QUERY)) {
            statement.setString(1, avatarUrl);
            statement.setLong(2, userId);

            int affectedRows = statement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Ошибка при обновлении аватара: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(Long userId) {
        try (PreparedStatement statement = connection.prepareStatement(DELETE_USER_QUERY)) {
            statement.setLong(1, userId);

            int affectedRows = statement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Ошибка при удалении пользователя: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    private User mapResultSetToUser(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setId(resultSet.getLong("id"));
        user.setUsername(resultSet.getString("username"));
        user.setPasswordHash(resultSet.getString("password_hash"));
        user.setSalt(resultSet.getString("salt")); // Добавляем получение соли
        user.setAvatarUrl(resultSet.getString("avatar_url"));
        return user;
    }
}