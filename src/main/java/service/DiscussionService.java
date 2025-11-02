package service;

import dao.DiscussionDao;
import model.DiscussionPost;
import model.DiscussionComment;
import java.sql.Connection;
import java.util.List;

public class DiscussionService {
    private DiscussionDao discussionDao;

    public DiscussionService(Connection connection) {
        this.discussionDao = new DiscussionDao(connection);
    }

    /**
     * Создать новый пост обсуждения
     */
    public boolean createPost(String title, String content, Long authorId) {
        try {
            DiscussionPost post = new DiscussionPost();
            post.setTitle(title);
            post.setContent(content);
            post.setAuthorId(authorId);

            return discussionDao.create(post);
        } catch (Exception e) {
            System.err.println("❌ Ошибка при создании поста: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Получить все посты (без информации о лайках)
     */
    public List<DiscussionPost> getAllPosts() {
        return discussionDao.findAll();
    }

    /**
     * Получить все посты с информацией о лайках текущего пользователя
     */
    public List<DiscussionPost> getAllPostsWithLikes(Long currentUserId) {
        return discussionDao.findAllWithLikes(currentUserId);
    }

    /**
     * Найти пост по ID
     */
    public DiscussionPost getPostById(Long id) {
        return discussionDao.findById(id);
    }

    /**
     * Найти пост по ID с информацией о лайках
     */
    public DiscussionPost getPostByIdWithLikes(Long id, Long currentUserId) {
        return discussionDao.findByIdWithLikes(id, currentUserId);
    }

    /**
     * Получить пост со всеми комментариями
     */
    public DiscussionPost getPostWithComments(Long postId, Long currentUserId) {
        return discussionDao.findPostWithComments(postId, currentUserId);
    }

    /**
     * Обновить пост
     */
    public boolean updatePost(Long id, String title, String content, Long authorId) {
        try {
            DiscussionPost post = discussionDao.findById(id);
            if (post == null || !post.getAuthorId().equals(authorId)) {
                return false;
            }

            post.setTitle(title);
            post.setContent(content);
            return discussionDao.update(post);
        } catch (Exception e) {
            System.err.println("❌ Ошибка при обновлении поста: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Удалить пост
     */
    public boolean deletePost(Long id, Long authorId) {
        try {
            DiscussionPost post = discussionDao.findById(id);
            if (post == null || !post.getAuthorId().equals(authorId)) {
                return false;
            }
            return discussionDao.delete(id);
        } catch (Exception e) {
            System.err.println("❌ Ошибка при удалении поста: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Проверить, является ли пользователь автором поста
     */
    public boolean isAuthor(Long postId, Long userId) {
        DiscussionPost post = discussionDao.findById(postId);
        return post != null && post.getAuthorId().equals(userId);
    }

    /**
     * Добавить лайк к посту
     */
    public boolean addLike(Long postId, Long userId) {
        return discussionDao.addLike(postId, userId);
    }

    /**
     * Удалить лайк с поста
     */
    public boolean removeLike(Long postId, Long userId) {
        return discussionDao.removeLike(postId, userId);
    }

    /**
     * Переключить лайк (добавить/удалить)
     */
    public boolean toggleLike(Long postId, Long userId) {
        return discussionDao.toggleLike(postId, userId);
    }

    /**
     * Проверить, лайкнул ли пользователь пост
     */
    public boolean hasUserLiked(Long postId, Long userId) {
        return discussionDao.hasUserLiked(postId, userId);
    }

    /**
     * Получить количество лайков поста
     */
    public int getLikeCount(Long postId) {
        return discussionDao.getLikeCount(postId);
    }

    /**
     * Добавить комментарий к посту
     */
    public boolean addComment(Long postId, Long userId, String content) {
        try {
            DiscussionComment comment = new DiscussionComment();
            comment.setPostId(postId);
            comment.setUserId(userId);
            comment.setContent(content);

            return discussionDao.addComment(comment);
        } catch (Exception e) {
            System.err.println("❌ Ошибка при добавлении комментария: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Получить все комментарии поста
     */
    public List<DiscussionComment> getCommentsByPost(Long postId) {
        return discussionDao.getCommentsByPost(postId);
    }

    /**
     * Удалить комментарий
     */
    public boolean deleteComment(Long commentId, Long userId) {
        return discussionDao.deleteComment(commentId, userId);
    }

    /**
     * Получить количество комментариев поста
     */
    public int getCommentCount(Long postId) {
        return discussionDao.getCommentCount(postId);
    }

    /**
     * Найти комментарий по ID
     */
    public DiscussionComment getCommentById(Long commentId) {
        return discussionDao.findCommentById(commentId);
    }

    /**
     * Проверить, является ли пользователь автором комментария
     */
    public boolean isCommentAuthor(Long commentId, Long userId) {
        DiscussionComment comment = discussionDao.findCommentById(commentId);
        return comment != null && comment.getUserId().equals(userId);
    }
}