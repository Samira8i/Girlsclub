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

    public boolean createPost(String title, String content, Long authorId) {
        try {
            DiscussionPost post = new DiscussionPost();
            post.setTitle(title);
            post.setContent(content);
            post.setAuthorId(authorId);

            return discussionDao.create(post);
        } catch (Exception e) {
            System.err.println("Ошибка при создании поста: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<DiscussionPost> getAllPostsWithLikes(Long currentUserId) {
        return discussionDao.findAllWithLikes(currentUserId);
    }

    public DiscussionPost getPostById(Long id) {
        return discussionDao.findById(id);
    }

    public DiscussionPost getPostByIdWithLikes(Long id, Long currentUserId) {
        return discussionDao.findByIdWithLikes(id, currentUserId);
    }

    public DiscussionPost getPostWithComments(Long postId, Long currentUserId) {
        return discussionDao.findPostWithComments(postId, currentUserId);
    }

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
            System.err.println("Ошибка при обновлении поста: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deletePost(Long id, Long authorId) {
        try {
            DiscussionPost post = discussionDao.findById(id);
            if (post == null || !post.getAuthorId().equals(authorId)) {
                return false;
            }
            return discussionDao.delete(id);
        } catch (Exception e) {
            System.err.println("Ошибка при удалении поста: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean addLike(Long postId, Long userId) {
        return discussionDao.addLike(postId, userId);
    }

    public boolean removeLike(Long postId, Long userId) {
        return discussionDao.removeLike(postId, userId);
    }

    public boolean toggleLike(Long postId, Long userId) {
        return discussionDao.toggleLike(postId, userId);
    }

    // Новые методы для AJAX функциональности
    public boolean hasUserLiked(Long postId, Long userId) {
        return discussionDao.hasUserLiked(postId, userId);
    }

    public int getLikeCount(Long postId) {
        return discussionDao.getLikeCount(postId);
    }

    public boolean addComment(Long postId, Long userId, String content) {
        try {
            DiscussionComment comment = new DiscussionComment();
            comment.setPostId(postId);
            comment.setUserId(userId);
            comment.setContent(content);

            return discussionDao.addComment(comment);
        } catch (Exception e) {
            System.err.println("Ошибка при добавлении комментария: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteComment(Long commentId, Long userId) {
        return discussionDao.deleteComment(commentId, userId);
    }
}