package dao;

import model.EventRegistration;
import model.User;
import util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EventRegistrationDao {

    private static final String CREATE_TABLE_QUERY =
            "CREATE TABLE IF NOT EXISTS event_registrations (" +
                    "id BIGSERIAL PRIMARY KEY, " +
                    "post_id BIGINT NOT NULL, " +
                    "user_id BIGINT NOT NULL, " +
                    "status VARCHAR(20) DEFAULT 'registered', " +
                    "registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (post_id) REFERENCES meeting_posts(id) ON DELETE CASCADE, " +
                    "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE, " +
                    "UNIQUE(post_id, user_id)" +
                    ");";

    private static final String REGISTER_QUERY =
            "INSERT INTO event_registrations (post_id, user_id, status) VALUES (?, ?, 'registered') " +
                    "ON CONFLICT (post_id, user_id) DO UPDATE SET status = 'registered';";

    private static final String CANCEL_REGISTRATION_QUERY =
            "UPDATE event_registrations SET status = 'cancelled' WHERE post_id = ? AND user_id = ?;";

    private static final String FIND_BY_POST_AND_USER_QUERY =
            "SELECT * FROM event_registrations WHERE post_id = ? AND user_id = ?;";

    private static final String FIND_REGISTERED_USERS_BY_POST_QUERY =
            "SELECT er.*, u.username " +
                    "FROM event_registrations er " +
                    "JOIN users u ON er.user_id = u.id " +
                    "WHERE er.post_id = ? AND er.status = 'registered' " +
                    "ORDER BY er.registration_date;";

    private static final String COUNT_REGISTERED_USERS_QUERY =
            "SELECT COUNT(*) FROM event_registrations WHERE post_id = ? AND status = 'registered';";

    private static final String DELETE_REGISTRATION_QUERY =
            "DELETE FROM event_registrations WHERE post_id = ? AND user_id = ?;";

    public EventRegistrationDao() {
        initializeTable();
    }

    private void initializeTable() {
        try (Connection connection = DatabaseUtil.getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(CREATE_TABLE_QUERY);
            System.out.println("Таблица event_registrations создана или уже существует");
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при создании таблицы event_registrations", e);
        }
    }

    public boolean registerForEvent(Long postId, Long userId) {
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(REGISTER_QUERY)) {
            statement.setLong(1, postId);
            statement.setLong(2, userId);
            int affectedRows = statement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Ошибка при записи на событие: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean cancelRegistration(Long postId, Long userId) {
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(CANCEL_REGISTRATION_QUERY)) {
            statement.setLong(1, postId);
            statement.setLong(2, userId);
            int affectedRows = statement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Ошибка при отмене записи: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public EventRegistration findByPostAndUser(Long postId, Long userId) {
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_POST_AND_USER_QUERY)) {
            statement.setLong(1, postId);
            statement.setLong(2, userId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return mapResultSetToEventRegistration(resultSet);
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при поиске записи: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public List<EventRegistration> findRegisteredUsersByPost(Long postId) {
        List<EventRegistration> registrations = new ArrayList<>();
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_REGISTERED_USERS_BY_POST_QUERY)) {
            statement.setLong(1, postId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                EventRegistration registration = mapResultSetToEventRegistration(resultSet);
                User user = new User();
                user.setId(resultSet.getLong("user_id"));
                user.setUsername(resultSet.getString("username"));
                registration.setUser(user);

                registrations.add(registration);
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при получении списка участников: " + e.getMessage());
            e.printStackTrace();
        }
        return registrations;
    }

    public int countRegisteredUsers(Long postId) {
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(COUNT_REGISTERED_USERS_QUERY)) {
            statement.setLong(1, postId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при подсчете участников: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    //по факту при отписывания от события у нас статус меняется и смысла в этом методе нет, но пока пусть будет
    public boolean deleteRegistration(Long postId, Long userId) {
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_REGISTRATION_QUERY)) {
            statement.setLong(1, postId);
            statement.setLong(2, userId);
            int affectedRows = statement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Ошибка при удалении записи: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private EventRegistration mapResultSetToEventRegistration(ResultSet resultSet) throws SQLException {
        EventRegistration registration = new EventRegistration();
        registration.setId(resultSet.getLong("id"));
        registration.setPostId(resultSet.getLong("post_id"));
        registration.setUserId(resultSet.getLong("user_id"));
        registration.setStatus(resultSet.getString("status"));
        registration.setRegistrationDate(resultSet.getTimestamp("registration_date").toLocalDateTime());
        return registration;
    }
}