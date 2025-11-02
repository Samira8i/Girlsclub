package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.DiscussionPost;
import model.User;
import service.ServiceFactory;
import service.DiscussionService;
import service.UserService;
import exceptions.AuthenticationException;

import java.io.IOException;
import java.util.List;

@WebServlet("/discussions")
public class DiscussionServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            UserService userService = ServiceFactory.getUserService();

            String sessionId = extractSessionId(request.getCookies());
            if (sessionId == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            User user = userService.getUserBySessionId(sessionId);
            request.setAttribute("user", user);

            DiscussionService discussionService = ServiceFactory.getDiscussionService();

            // Используем правильный метод сервиса
            List<DiscussionPost> posts = discussionService.getAllPostsWithLikes(user.getId());

            request.setAttribute("posts", posts);

            String success = request.getParameter("success");
            String error = request.getParameter("error");
            if (success != null) request.setAttribute("success", success);
            if (error != null) request.setAttribute("error", error);

            request.getRequestDispatcher("/WEB-INF/views/discussions.jsp").forward(request, response);

        } catch (AuthenticationException e) {
            response.sendRedirect(request.getContextPath() + "/login");
        } catch (Exception e) {
            System.err.println("❌ Ошибка при загрузке обсуждений: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Ошибка при загрузке обсуждений: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/discussions.jsp").forward(request, response);
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