package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.User;
import service.DiscussionService;
import service.UserService;
import exceptions.AuthenticationException;

import java.io.IOException;

@WebServlet("/discussion/like")
public class DiscussionLikeServlet extends HttpServlet {

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
            String action = request.getParameter("action");

            if (postIdParam == null || action == null) {
                response.sendRedirect(request.getContextPath() + "/main?error=Неверные_параметры&section=discussions");
                return;
            }

            Long postId = Long.parseLong(postIdParam);
            DiscussionService discussionService = new DiscussionService();

            boolean success = false;
            if ("like".equals(action)) {
                success = discussionService.addLike(postId, user.getId());
            } else if ("unlike".equals(action)) {
                success = discussionService.removeLike(postId, user.getId());
            } else if ("toggle".equals(action)) {
                success = discussionService.toggleLike(postId, user.getId());
            }

            if (success) {
                response.sendRedirect(request.getContextPath() + "/main?success=like_updated&section=discussions");
            } else {
                response.sendRedirect(request.getContextPath() + "/main?error=like_failed&section=discussions");
            }

        } catch (AuthenticationException e) {
            response.sendRedirect(request.getContextPath() + "/login");
        } catch (Exception e) {
            System.err.println("Ошибка при обработке лайка: " + e.getMessage());
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