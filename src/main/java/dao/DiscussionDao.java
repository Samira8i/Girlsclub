package dao;

import model.DiscussionPost;
import model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DiscussionDao{
    private final Connection connection;

    private static final String DISCUSSION_TABLE_CREATE_QUERY =
            "CREATE TABLE IF NOT EXISTS discussion_posts (" +
                    "id BIGSERIAL PRIMARY KEY, " +
                    "title VARCHAR(200) NOT NULL, " +
                    "content TEXT NOT NULL, " +
                    "author_id BIGINT NOT NULL, " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (author_id) REFERENCES users(id)" +
                    ");";

    private static final String CREATE_DISCUSSION_QUERY =
            "INSERT INTO discussion_posts (title, content, author_id) " +
                    "VALUES (?, ?, ?) RETURNING id;";

    private static final String FIND_ALL_DISCUSSIONS_QUERY =
            "SELECT dp.*, u.username, u.avatar_url " +
                    "FROM discussion_posts dp " +
                    "LEFT JOIN users u ON dp.author_id = u.id " +
                    "ORDER BY dp.created_at DESC;";

    private static final String FIND_DISCUSSION_BY_ID_QUERY =
            "SELECT dp.*, u.username, u.avatar_url " +
                    "FROM discussion_posts dp " +
                    "LEFT JOIN users u ON dp.author_id = u.id " +
                    "WHERE dp.id = ?;";

    private static final String UPDATE_DISCUSSION_QUERY =
            "UPDATE discussion_posts SET title = ?, content = ? WHERE id = ?;";

    private static final String DELETE_DISCUSSION_QUERY =
            "DELETE FROM discussion_posts WHERE id = ?;";

    public DiscussionDao(Connection connection) {
        this.connection = connection;
        initializeTable();
    }

    /**
     * Инициализация таблицы обсуждений
     */
    private void initializeTable() {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(DISCUSSION_TABLE_CREATE_QUERY);
            System.out.println("✅ Таблица discussion_posts создана или уже существует");
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при создании таблицы discussion_posts", e);
        }
    }

    public boolean create(DiscussionPost post) {
        try (PreparedStatement statement = connection.prepareStatement(CREATE_DISCUSSION_QUERY)) {
            statement.setString(1, post.getTitle());
            statement.setString(2, post.getContent());
            statement.setLong(3, post.getAuthorId());

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                post.setId(resultSet.getLong(1));
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Ошибка при создании обсуждения: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public List<DiscussionPost> findAll() {
        List<DiscussionPost> posts = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(FIND_ALL_DISCUSSIONS_QUERY);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                posts.add(mapResultSetToDiscussionPost(resultSet));
            }
        } catch (SQLException e) {
            System.err.println("❌ Ошибка при получении всех обсуждений: " + e.getMessage());
            e.printStackTrace();
        }
        return posts;
    }

    public DiscussionPost findById(Long id) {
        try (PreparedStatement statement = connection.prepareStatement(FIND_DISCUSSION_BY_ID_QUERY)) {
            statement.setLong(1, id);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return mapResultSetToDiscussionPost(resultSet);
            }
        } catch (SQLException e) {
            System.err.println("❌ Ошибка при поиске обсуждения: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public boolean update(DiscussionPost post) {
        try (PreparedStatement statement = connection.prepareStatement(UPDATE_DISCUSSION_QUERY)) {
            statement.setString(1, post.getTitle());
            statement.setString(2, post.getContent());
            statement.setLong(3, post.getId());

            int affectedRows = statement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("❌ Ошибка при обновлении обсуждения: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(Long id) {
        try (PreparedStatement statement = connection.prepareStatement(DELETE_DISCUSSION_QUERY)) {
            statement.setLong(1, id);

            int affectedRows = statement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("❌ Ошибка при удалении обсуждения: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    private DiscussionPost mapResultSetToDiscussionPost(ResultSet resultSet) throws SQLException {
        DiscussionPost post = new DiscussionPost();
        post.setId(resultSet.getLong("id"));
        post.setTitle(resultSet.getString("title"));
        post.setContent(resultSet.getString("content"));
        post.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
        post.setAuthorId(resultSet.getLong("author_id"));

        User author = new User();
        author.setUsername(resultSet.getString("username"));
        author.setAvatarUrl(resultSet.getString("avatar_url"));
        post.setAuthor(author);

        return post;
    }
}