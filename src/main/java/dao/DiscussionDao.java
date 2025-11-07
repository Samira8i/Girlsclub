package dao;

import model.DiscussionPost;
import model.DiscussionComment;
import model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DiscussionDao {
    private final Connection connection;

    private static final String DISCUSSION_TABLE_CREATE_QUERY =
            "CREATE TABLE IF NOT EXISTS discussion_posts (" +
                    "id BIGSERIAL PRIMARY KEY, " +
                    "title VARCHAR(200) NOT NULL, " +
                    "content TEXT NOT NULL, " +
                    "author_id BIGINT NOT NULL, " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "like_count INTEGER DEFAULT 0, " +
                    "comment_count INTEGER DEFAULT 0, " +
                    "FOREIGN KEY (author_id) REFERENCES users(id)" +
                    ");";

    private static final String CREATE_DISCUSSION_QUERY =
            "INSERT INTO discussion_posts (title, content, author_id) " +
                    "VALUES (?, ?, ?) RETURNING id;";

    private static final String FIND_ALL_DISCUSSIONS_QUERY =
            "SELECT dp.*, u.username " +
                    "FROM discussion_posts dp " +
                    "LEFT JOIN users u ON dp.author_id = u.id " +
                    "ORDER BY dp.created_at DESC;";

    private static final String FIND_DISCUSSION_BY_ID_QUERY =
            "SELECT dp.*, u.username " +
                    "FROM discussion_posts dp " +
                    "LEFT JOIN users u ON dp.author_id = u.id " +
                    "WHERE dp.id = ?;";

    private static final String UPDATE_DISCUSSION_QUERY =
            "UPDATE discussion_posts SET title = ?, content = ? WHERE id = ?;";

    private static final String DELETE_DISCUSSION_QUERY =
            "DELETE FROM discussion_posts WHERE id = ?;";

    // для таблицы лайков
    private static final String ADD_LIKE_QUERY =
            "INSERT INTO discussion_likes (post_id, user_id) VALUES (?, ?) " +
                    "ON CONFLICT (post_id, user_id) DO NOTHING;";

    private static final String REMOVE_LIKE_QUERY =
            "DELETE FROM discussion_likes WHERE post_id = ? AND user_id = ?;";

    private static final String COUNT_LIKES_QUERY =
            "SELECT COUNT(*) FROM discussion_likes WHERE post_id = ?;";

    private static final String CHECK_USER_LIKE_QUERY =
            "SELECT 1 FROM discussion_likes WHERE post_id = ? AND user_id = ?;";

    // для таблицы комментариев
    private static final String ADD_COMMENT_QUERY =
            "INSERT INTO discussion_comments (post_id, user_id, content) VALUES (?, ?, ?) RETURNING id;";

    private static final String GET_COMMENTS_BY_POST_QUERY =
            "SELECT dc.*, u.username " +
                    "FROM discussion_comments dc " +
                    "JOIN users u ON dc.user_id = u.id " +
                    "WHERE dc.post_id = ? " +
                    "ORDER BY dc.created_at ASC;";

    private static final String COUNT_COMMENTS_QUERY =
            "SELECT COUNT(*) FROM discussion_comments WHERE post_id = ?;";

    private static final String DELETE_COMMENT_QUERY =
            "DELETE FROM discussion_comments WHERE id = ? AND user_id = ?;";

    private static final String UPDATE_POST_COUNTS_QUERY =
            "UPDATE discussion_posts SET like_count = ?, comment_count = ? WHERE id = ?;";

    private static final String FIND_COMMENT_BY_ID_QUERY =
            "SELECT * FROM discussion_comments WHERE id = ?;";

    public DiscussionDao(Connection connection) {
        this.connection = connection;
        initializeTable();
    }
    private void initializeTable() {
        try (Statement statement = connection.createStatement()) {
            // Создаю таблицу постов
            statement.executeUpdate(DISCUSSION_TABLE_CREATE_QUERY);

            // Создаюю таблицу лайков
            statement.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS discussion_likes (" +
                            "id BIGSERIAL PRIMARY KEY, " +
                            "post_id BIGINT NOT NULL, " +
                            "user_id BIGINT NOT NULL, " +
                            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                            "FOREIGN KEY (post_id) REFERENCES discussion_posts(id) ON DELETE CASCADE, " +
                            "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE, " +
                            "UNIQUE(post_id, user_id)" +
                            ");"
            );

            // Создаею таблицу комментариев
            statement.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS discussion_comments (" +
                            "id BIGSERIAL PRIMARY KEY, " +
                            "post_id BIGINT NOT NULL, " +
                            "user_id BIGINT NOT NULL, " +
                            "content TEXT NOT NULL, " +
                            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                            "FOREIGN KEY (post_id) REFERENCES discussion_posts(id) ON DELETE CASCADE, " +
                            "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE" +
                            ");"
            );

            // Создаем индексы для оптимизации (прочитала где то что нужно но возможно излишне (особенно для моего сайта...)
            statement.executeUpdate("CREATE INDEX IF NOT EXISTS idx_discussion_likes_post_id ON discussion_likes(post_id);");
            statement.executeUpdate("CREATE INDEX IF NOT EXISTS idx_discussion_likes_user_id ON discussion_likes(user_id);");
            statement.executeUpdate("CREATE INDEX IF NOT EXISTS idx_discussion_comments_post_id ON discussion_comments(post_id);");
            statement.executeUpdate("CREATE INDEX IF NOT EXISTS idx_discussion_comments_user_id ON discussion_comments(user_id);");

            System.out.println("Таблицы discussion_posts, discussion_likes и discussion_comments созданы или уже существуют");
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при создании таблиц обсуждений", e);
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
            System.err.println("Ошибка при создании обсуждения: " + e.getMessage());
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
            System.err.println("Ошибка при получении всех обсуждений: " + e.getMessage());
            e.printStackTrace();
        }
        return posts;
    }

    public List<DiscussionPost> findAllWithLikes(Long currentUserId) {
        List<DiscussionPost> posts = new ArrayList<>();
        String query = "SELECT dp.*, u.username, " +
                "EXISTS(SELECT 1 FROM discussion_likes dl WHERE dl.post_id = dp.id AND dl.user_id = ?) as user_liked " +
                "FROM discussion_posts dp " +
                "LEFT JOIN users u ON dp.author_id = u.id " +
                "ORDER BY dp.created_at DESC";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, currentUserId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                DiscussionPost post = mapResultSetToDiscussionPost(resultSet);
                post.setUserLiked(resultSet.getBoolean("user_liked"));
                List<DiscussionComment> comments = getCommentsByPost(post.getId());
                post.setComments(comments);

                posts.add(post);
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при получении постов с лайками: " + e.getMessage());
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
            System.err.println("Ошибка при поиске обсуждения: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public DiscussionPost findByIdWithLikes(Long id, Long currentUserId) {
        String query = "SELECT dp.*, u.username, " +
                "EXISTS(SELECT 1 FROM discussion_likes dl WHERE dl.post_id = dp.id AND dl.user_id = ?) as user_liked " +
                "FROM discussion_posts dp " +
                "LEFT JOIN users u ON dp.author_id = u.id " +
                "WHERE dp.id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, currentUserId);
            statement.setLong(2, id);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                DiscussionPost post = mapResultSetToDiscussionPost(resultSet);
                post.setUserLiked(resultSet.getBoolean("user_liked"));
                return post;
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при поиске обсуждения с лайками: " + e.getMessage());
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
            System.err.println("Ошибка при обновлении обсуждения: " + e.getMessage());
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
            System.err.println("Ошибка при удалении обсуждения: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean addLike(Long postId, Long userId) {
        try (PreparedStatement statement = connection.prepareStatement(ADD_LIKE_QUERY)) {
            statement.setLong(1, postId);
            statement.setLong(2, userId);
            int affectedRows = statement.executeUpdate();

            if (affectedRows > 0) {
                updatePostCounts(postId);
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при добавлении лайка: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean removeLike(Long postId, Long userId) {
        try (PreparedStatement statement = connection.prepareStatement(REMOVE_LIKE_QUERY)) {
            statement.setLong(1, postId);
            statement.setLong(2, userId);
            int affectedRows = statement.executeUpdate();

            if (affectedRows > 0) {
                updatePostCounts(postId);
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при удалении лайка: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean hasUserLiked(Long postId, Long userId) {
        try (PreparedStatement statement = connection.prepareStatement(CHECK_USER_LIKE_QUERY)) {
            statement.setLong(1, postId);
            statement.setLong(2, userId);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            System.err.println("Ошибка при проверке лайка: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public int getLikeCount(Long postId) {
        try (PreparedStatement statement = connection.prepareStatement(COUNT_LIKES_QUERY)) {
            statement.setLong(1, postId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при подсчете лайков: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }
    public boolean addComment(DiscussionComment comment) {
        try (PreparedStatement statement = connection.prepareStatement(ADD_COMMENT_QUERY)) {
            statement.setLong(1, comment.getPostId());
            statement.setLong(2, comment.getUserId());
            statement.setString(3, comment.getContent());

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                comment.setId(resultSet.getLong(1));
                updatePostCounts(comment.getPostId());
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при добавлении комментария: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public List<DiscussionComment> getCommentsByPost(Long postId) {
        List<DiscussionComment> comments = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(GET_COMMENTS_BY_POST_QUERY)) {
            statement.setLong(1, postId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                DiscussionComment comment = mapResultSetToDiscussionComment(resultSet);
                comments.add(comment);
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при получении комментариев: " + e.getMessage());
            e.printStackTrace();
        }
        return comments;
    }

    public boolean deleteComment(Long commentId, Long userId) {
        try (PreparedStatement statement = connection.prepareStatement(DELETE_COMMENT_QUERY)) {
            statement.setLong(1, commentId);
            statement.setLong(2, userId);
            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                DiscussionComment comment = findCommentById(commentId);
                if (comment != null) {
                    updatePostCounts(comment.getPostId());
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при удалении комментария: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public DiscussionComment findCommentById(Long commentId) {
        try (PreparedStatement statement = connection.prepareStatement(FIND_COMMENT_BY_ID_QUERY)) {
            statement.setLong(1, commentId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return mapResultSetToDiscussionComment(resultSet);
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при поиске комментария: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public int getCommentCount(Long postId) {
        try (PreparedStatement statement = connection.prepareStatement(COUNT_COMMENTS_QUERY)) {
            statement.setLong(1, postId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при подсчете комментариев: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }
    private void updatePostCounts(Long postId) {
        try (PreparedStatement statement = connection.prepareStatement(UPDATE_POST_COUNTS_QUERY)) {
            int likeCount = getLikeCount(postId);
            int commentCount = getCommentCount(postId);

            statement.setInt(1, likeCount);
            statement.setInt(2, commentCount);
            statement.setLong(3, postId);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Ошибка при обновлении счетчиков поста: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private DiscussionPost mapResultSetToDiscussionPost(ResultSet resultSet) throws SQLException {
        DiscussionPost post = new DiscussionPost();
        post.setId(resultSet.getLong("id"));
        post.setTitle(resultSet.getString("title"));
        post.setContent(resultSet.getString("content"));
        post.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
        post.setAuthorId(resultSet.getLong("author_id"));
        post.setLikeCount(resultSet.getInt("like_count"));
        post.setCommentCount(resultSet.getInt("comment_count"));

        User author = new User();
        author.setUsername(resultSet.getString("username"));
        post.setAuthor(author);

        return post;
    }

    private DiscussionComment mapResultSetToDiscussionComment(ResultSet resultSet) throws SQLException {
        DiscussionComment comment = new DiscussionComment();
        comment.setId(resultSet.getLong("id"));
        comment.setPostId(resultSet.getLong("post_id"));
        comment.setUserId(resultSet.getLong("user_id"));
        comment.setContent(resultSet.getString("content"));
        comment.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());

        User user = new User();
        user.setId(resultSet.getLong("user_id"));
        user.setUsername(resultSet.getString("username"));
        comment.setUser(user);

        return comment;
    }

    public DiscussionPost findPostWithComments(Long postId, Long currentUserId) {
        DiscussionPost post = findByIdWithLikes(postId, currentUserId);
        if (post != null) {
            List<DiscussionComment> comments = getCommentsByPost(postId);
            post.setComments(comments);
        }
        return post;
    }

    public boolean toggleLike(Long postId, Long userId) {
        if (hasUserLiked(postId, userId)) {
            return removeLike(postId, userId);
        } else {
            return addLike(postId, userId);
        }
    }
}