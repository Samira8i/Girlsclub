package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.User;
import service.DiscussionService;
import service.UserService;
import exceptions.AuthenticationException;

import java.io.IOException;

@WebServlet("/discussion/comment")
public class DiscussionCommentServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            UserService userService = new UserService();

            String sessionId = extractSessionId(request.getCookies());
            if (sessionId == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            User user = userService.getUserBySessionId(sessionId);

            String postIdParam = request.getParameter("postId");
            String content = request.getParameter("content");
            String action = request.getParameter("action");
            String commentIdParam = request.getParameter("commentId");

            if (postIdParam == null) {
                response.sendRedirect(request.getContextPath() + "/main?error=ID_поста_не_указан&section=discussions");
                return;
            }

            Long postId = Long.parseLong(postIdParam);
            DiscussionService discussionService = new DiscussionService();

            boolean success = false;

            if ("add".equals(action) && content != null && !content.trim().isEmpty()) {
                success = discussionService.addComment(postId, user.getId(), content.trim());
            } else if ("delete".equals(action) && commentIdParam != null) {
                Long commentId = Long.parseLong(commentIdParam);
                success = discussionService.deleteComment(commentId, user.getId());
            }

            if (success) {
                response.sendRedirect(request.getContextPath() + "/main?success=comment_added&section=discussions");
            } else {
                response.sendRedirect(request.getContextPath() + "/main?error=comment_failed&section=discussions");
            }

        } catch (AuthenticationException e) {
            response.sendRedirect(request.getContextPath() + "/login");
        } catch (Exception e) {
            System.err.println("Ошибка при обработке комментария: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/main?error=server_error&section=discussions");
        }
    }

    private String extractSessionId(Cookie[] cookies) {
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("sessionId".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}