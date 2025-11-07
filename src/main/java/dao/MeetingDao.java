package dao;

import model.MeetingPost;
import model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MeetingDao {
    private final Connection connection;

    private static final String MEETING_TABLE_CREATE_QUERY =
            "CREATE TABLE IF NOT EXISTS meeting_posts (" +
                    "id BIGSERIAL PRIMARY KEY, " +
                    "title VARCHAR(200) NOT NULL, " +
                    "description TEXT, " +
                    "event_date TIMESTAMP NOT NULL, " +
                    "max_attendance INTEGER, " +
                    "location VARCHAR(300), " +
                    "author_id BIGINT NOT NULL, " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (author_id) REFERENCES users(id)" +
                    ");";

    private static final String CREATE_MEETING_QUERY =
            "INSERT INTO meeting_posts (title, description, event_date, max_attendance, location, author_id) " +
                    "VALUES (?, ?, ?, ?, ?, ?) RETURNING id;";

    private static final String FIND_ALL_MEETINGS_QUERY =
            "SELECT mp.*, u.username " +
                    "FROM meeting_posts mp " +
                    "LEFT JOIN users u ON mp.author_id = u.id " +
                    "ORDER BY mp.event_date ASC;";

    private static final String FIND_MEETING_BY_ID_QUERY =
            "SELECT mp.*, u.username " +
                    "FROM meeting_posts mp " +
                    "LEFT JOIN users u ON mp.author_id = u.id " +
                    "WHERE mp.id = ?;";

    private static final String UPDATE_MEETING_QUERY =
            "UPDATE meeting_posts SET title = ?, description = ?, event_date = ?, max_attendance = ?, location = ? " +
                    "WHERE id = ?;";

    private static final String DELETE_MEETING_QUERY =
            "DELETE FROM meeting_posts WHERE id = ?;";

    public MeetingDao(Connection connection) {
        this.connection = connection;
        initializeTable();
    }

    private void initializeTable() {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(MEETING_TABLE_CREATE_QUERY);
            System.out.println("Таблица meeting_posts создана или уже существует");
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при создании таблицы meeting_posts", e);
        }
    }

    public boolean create(MeetingPost meeting) {
        try (PreparedStatement statement = connection.prepareStatement(CREATE_MEETING_QUERY)) {
            statement.setString(1, meeting.getTitle());
            statement.setString(2, meeting.getDescription());
            statement.setTimestamp(3, Timestamp.valueOf(meeting.getEventDate()));
            statement.setInt(4, meeting.getMaxAttendance());
            statement.setString(5, meeting.getLocation());
            statement.setLong(6, meeting.getAuthorId());

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                meeting.setId(resultSet.getLong(1));
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при создании встречи: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public List<MeetingPost> findAll() {
        List<MeetingPost> meetings = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(FIND_ALL_MEETINGS_QUERY);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                meetings.add(mapResultSetToMeetingPost(resultSet));
            }
        } catch (SQLException e) {
            System.err.println("ошибка при получении всех встреч: " + e.getMessage());
            e.printStackTrace();
        }
        return meetings;
    }

    public MeetingPost findById(Long id) {
        try (PreparedStatement statement = connection.prepareStatement(FIND_MEETING_BY_ID_QUERY)) {
            statement.setLong(1, id);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return mapResultSetToMeetingPost(resultSet);
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при поиске встречи: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public boolean update(MeetingPost meeting) {
        try (PreparedStatement statement = connection.prepareStatement(UPDATE_MEETING_QUERY)) {
            statement.setString(1, meeting.getTitle());
            statement.setString(2, meeting.getDescription());
            statement.setTimestamp(3, Timestamp.valueOf(meeting.getEventDate()));
            statement.setInt(4, meeting.getMaxAttendance());
            statement.setString(5, meeting.getLocation());
            statement.setLong(6, meeting.getId());

            int affectedRows = statement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Ошибка при обновлении встречи: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(Long id) {
        try (PreparedStatement statement = connection.prepareStatement(DELETE_MEETING_QUERY)) {
            statement.setLong(1, id);

            int affectedRows = statement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Ошибка при удалении встречи: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    private MeetingPost mapResultSetToMeetingPost(ResultSet resultSet) throws SQLException {
        MeetingPost meeting = new MeetingPost();
        meeting.setId(resultSet.getLong("id"));
        meeting.setTitle(resultSet.getString("title"));
        meeting.setDescription(resultSet.getString("description"));
        meeting.setEventDate(resultSet.getTimestamp("event_date").toLocalDateTime());
        meeting.setMaxAttendance(resultSet.getInt("max_attendance"));
        meeting.setLocation(resultSet.getString("location"));
        meeting.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
        meeting.setAuthorId(resultSet.getLong("author_id"));

        User author = new User();
        author.setUsername(resultSet.getString("username"));
        meeting.setAuthor(author);

        return meeting;
    }
}