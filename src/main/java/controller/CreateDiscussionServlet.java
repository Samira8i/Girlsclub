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

            // Логируем полученные данные для отладки
            System.out.println("=== ДЕБАГ ИНФОРМАЦИЯ СОЗДАНИЯ ОБСУЖДЕНИЯ ===");
            System.out.println("Title: " + title);
            System.out.println("Content: " + content);
            System.out.println("AuthorId: " + user.getId());
            System.out.println("======================");

            // Валидация
            if (title == null || title.trim().isEmpty() ||
                    content == null || content.trim().isEmpty()) {

                request.setAttribute("error", "❌ Все поля обязательны для заполнения");
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
                System.out.println("✅ Пост обсуждения успешно создан!");
                response.sendRedirect(request.getContextPath() + "/discussions?success=discussion_created");
            } else {
                System.err.println("❌ Ошибка при создании поста обсуждения в сервисе");
                request.setAttribute("error", "❌ Ошибка при создании обсуждения. Проверьте введенные данные.");
                request.setAttribute("user", user);
                request.getRequestDispatcher("/WEB-INF/views/create-discussion.jsp").forward(request, response);
            }
        } catch (AuthenticationException e) {
            response.sendRedirect(request.getContextPath() + "/login");
        } catch (Exception e) {
            System.err.println("❌ Системная ошибка: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "❌ Системная ошибка: " + e.getMessage());
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