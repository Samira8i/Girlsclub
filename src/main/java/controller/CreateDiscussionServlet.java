package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.User;
import service.ServiceFactory;
import service.DiscussionService;
import service.UserService;
import exceptions.AuthenticationException;

import java.io.IOException;

@WebServlet("/discussion/create")
public class CreateDiscussionServlet extends HttpServlet {

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
            request.getRequestDispatcher("/WEB-INF/views/create-discussion.jsp").forward(request, response);

        } catch (AuthenticationException e) {
            response.sendRedirect(request.getContextPath() + "/login");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/login");
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            UserService userService = ServiceFactory.getUserService();

            String sessionId = extractSessionId(request.getCookies());
            if (sessionId == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            User user = userService.getUserBySessionId(sessionId);

            String title = request.getParameter("title");
            String content = request.getParameter("content");
            if (title == null || title.trim().isEmpty() ||
                    content == null || content.trim().isEmpty()) {

                request.setAttribute("error", "Все поля обязательны для заполнения");
                request.setAttribute("user", user);
                request.getRequestDispatcher("/WEB-INF/views/create-discussion.jsp").forward(request, response);
                return;
            }

            DiscussionService discussionService = ServiceFactory.getDiscussionService();

            boolean success = discussionService.createPost(
                    title.trim(),
                    content.trim(),
                    user.getId()
            );

            if (success) {
                response.sendRedirect(request.getContextPath() + "/main?success=discussion_created&section=discussions");
            } else {
                System.err.println(" Ошибка при создании поста обсуждения в сервисе");
                request.setAttribute("error", "Ошибка при создании обсуждения. Проверьте введенные данные.");
                request.setAttribute("user", user);
                request.getRequestDispatcher("/WEB-INF/views/create-discussion.jsp").forward(request, response);
            }
        } catch (AuthenticationException e) {
            response.sendRedirect(request.getContextPath() + "/login");
        } catch (Exception e) {
            System.err.println("Системная ошибка: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Системная ошибка: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/create-discussion.jsp").forward(request, response);
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