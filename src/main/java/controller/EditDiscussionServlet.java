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

@WebServlet("/discussion/edit")
public class EditDiscussionServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            UserService userService = ServiceFactory.getUserService();

            String sessionId = extractSessionId(request.getCookies());
            if (sessionId == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            User user = userService.getUserBySessionId(sessionId);

            String idParam = request.getParameter("id");
            if (idParam == null || idParam.trim().isEmpty()) {
                request.setAttribute("error", "ID обсуждения не указан");
                response.sendRedirect(request.getContextPath() + "/discussions");
                return;
            }

            DiscussionService discussionService = ServiceFactory.getDiscussionService();
            Long discussionId = Long.parseLong(idParam);

            DiscussionPost discussion = discussionService.getPostById(discussionId);

            if (discussion == null) {
                request.setAttribute("error", "Обсуждение не найдено");
                response.sendRedirect(request.getContextPath() + "/discussions");
                return;
            }

            // Проверяем, является ли пользователь автором
            if (!discussion.getAuthorId().equals(user.getId())) {
                request.setAttribute("error", "Вы можете редактировать только свои обсуждения");
                response.sendRedirect(request.getContextPath() + "/discussions");
                return;
            }

            request.setAttribute("discussion", discussion);
            request.setAttribute("user", user);
            request.getRequestDispatcher("/WEB-INF/views/edit-discussion.jsp").forward(request, response);

        } catch (AuthenticationException e) {
            response.sendRedirect(request.getContextPath() + "/login");
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Неверный идентификатор обсуждения");
            response.sendRedirect(request.getContextPath() + "/discussions");
        } catch (Exception e) {
            System.err.println("❌ Ошибка при загрузке обсуждения для редактирования: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Ошибка при загрузке обсуждения");
            response.sendRedirect(request.getContextPath() + "/discussions");
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

            String idParam = request.getParameter("id");
            String title = request.getParameter("title");
            String content = request.getParameter("content");

            // Логируем полученные данные для отладки
            System.out.println("=== ДЕБАГ ИНФОРМАЦИЯ РЕДАКТИРОВАНИЯ ОБСУЖДЕНИЯ ===");
            System.out.println("ID: " + idParam);
            System.out.println("Title: " + title);
            System.out.println("Content: " + content);
            System.out.println("User ID: " + user.getId());
            System.out.println("======================");

            // Валидация
            if (idParam == null || idParam.trim().isEmpty() ||
                    title == null || title.trim().isEmpty() ||
                    content == null || content.trim().isEmpty()) {

                request.setAttribute("error", "❌ Все поля обязательны для заполнения");
                request.setAttribute("user", user);
                request.getRequestDispatcher("/WEB-INF/views/edit-discussion.jsp").forward(request, response);
                return;
            }

            DiscussionService discussionService = ServiceFactory.getDiscussionService();
            Long discussionId = Long.parseLong(idParam);

            // Проверяем существование обсуждения и авторство
            DiscussionPost existingDiscussion = discussionService.getPostById(discussionId);
            if (existingDiscussion == null) {
                request.setAttribute("error", "Обсуждение не найдено");
                response.sendRedirect(request.getContextPath() + "/discussions");
                return;
            }

            if (!existingDiscussion.getAuthorId().equals(user.getId())) {
                request.setAttribute("error", "Вы можете редактировать только свои обсуждения");
                response.sendRedirect(request.getContextPath() + "/discussions");
                return;
            }

            // Исправленный вызов метода - добавляем authorId
            boolean success = discussionService.updatePost(
                    discussionId,
                    title.trim(),
                    content.trim(),
                    user.getId()  // Добавляем authorId
            );

            if (success) {
                System.out.println("✅ Обсуждение успешно обновлено!");
                response.sendRedirect(request.getContextPath() + "/discussions?success=discussion_updated");
            } else {
                System.err.println("❌ Ошибка при обновлении обсуждения в сервисе");
                request.setAttribute("error", "❌ Ошибка при обновлении обсуждения. Проверьте введенные данные.");
                request.setAttribute("discussion", existingDiscussion);
                request.setAttribute("user", user);
                request.getRequestDispatcher("/WEB-INF/views/edit-discussion.jsp").forward(request, response);
            }
        } catch (AuthenticationException e) {
            response.sendRedirect(request.getContextPath() + "/login");
        } catch (Exception e) {
            System.err.println("❌ Системная ошибка: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "❌ Системная ошибка: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/edit-discussion.jsp").forward(request, response);
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