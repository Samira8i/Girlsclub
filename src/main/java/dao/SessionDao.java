// SessionDao.java
package dao;

import model.Session;
import java.sql.*;
import java.time.LocalDateTime;

public class SessionDao {
    private final Connection connection;

    private static final String CREATE_TABLE_QUERY =
            "CREATE TABLE IF NOT EXISTS sessions (" +
                    "session_id VARCHAR(36) PRIMARY KEY, " + //использую uuid так как для публичных id безопаснее
                    "user_id BIGINT NOT NULL, " +
                    "expire_at TIMESTAMP NOT NULL, " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE" +
                    ");"; //сделала on delete чтобы когда пользователь удаляется из таблицы users, все его сессии автоматически удалялись из таблицы sessions

    private static final String ADD_SESSION_QUERY =
            "INSERT INTO sessions (session_id, user_id, expire_at) VALUES (?, ?, ?);";

    private static final String FIND_BY_SESSION_ID_QUERY =
            "SELECT * FROM sessions WHERE session_id = ?;";

    private static final String DELETE_SESSION_QUERY =
            "DELETE FROM sessions WHERE session_id = ?;";

    private static final String DELETE_EXPIRED_SESSIONS_QUERY =
            "DELETE FROM sessions WHERE expire_at < CURRENT_TIMESTAMP;";

    public SessionDao(Connection connection) {
        this.connection = connection;
        initializeTable();
    }

    private void initializeTable() {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(CREATE_TABLE_QUERY);
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при создании таблицы sessions", e);
        }
    }

    public void addSession(Session session) {
        try (PreparedStatement statement = connection.prepareStatement(ADD_SESSION_QUERY)) {
            statement.setString(1, session.getSessionId());
            statement.setLong(2, session.getUserId());
            statement.setTimestamp(3, Timestamp.valueOf(session.getExpireAt()));
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при добавлении сессии", e);
        }
    }

    public Session findBySessionId(String sessionId) {
        try (PreparedStatement statement = connection.prepareStatement(FIND_BY_SESSION_ID_QUERY)) {
            statement.setString(1, sessionId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return mapResultSetToSession(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при поиске сессии", e);
        }
        return null;
    }

    public void deleteSession(String sessionId) {
        try (PreparedStatement statement = connection.prepareStatement(DELETE_SESSION_QUERY)) {
            statement.setString(1, sessionId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при удалении сессии", e);
        }
    }

    public void cleanupExpiredSessions() {
        try (PreparedStatement statement = connection.prepareStatement(DELETE_EXPIRED_SESSIONS_QUERY)) {
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при очистке просроченных сессий", e);
        }
    }

    private Session mapResultSetToSession(ResultSet resultSet) throws SQLException {
        return new Session(
                resultSet.getString("session_id"),
                resultSet.getLong("user_id"),
                resultSet.getTimestamp("expire_at").toLocalDateTime()
        );
    }
}