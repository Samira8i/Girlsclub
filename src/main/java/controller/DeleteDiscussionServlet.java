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

@WebServlet("/discussion/delete")
public class DeleteDiscussionServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("🔍 DeleteDiscussionServlet: начат процесс удаления");
        try {
            UserService userService = ServiceFactory.getUserService();

            String sessionId = extractSessionId(request.getCookies());
            if (sessionId == null) {
                System.out.println("❌ Пользователь не авторизован");
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            User user = userService.getUserBySessionId(sessionId);

            DiscussionService discussionService = ServiceFactory.getDiscussionService();

            String idParam = request.getParameter("id");
            System.out.println("📨 Получен ID параметр: " + idParam);

            if (idParam == null || idParam.trim().isEmpty()) {
                request.setAttribute("error", "ID обсуждения не указан");
                response.sendRedirect(request.getContextPath() + "/discussions");
                return;
            }

            Long id = Long.parseLong(idParam);
            System.out.println("👤 Пользователь ID: " + user.getId() + " пытается удалить обсуждение ID: " + id);

            // Проверяем, существует ли обсуждение
            var post = discussionService.getPostById(id);
            if (post == null) {
                System.out.println("❌ Обсуждение с ID " + id + " не найдено");
                request.setAttribute("error", "Обсуждение не найдено");
                response.sendRedirect(request.getContextPath() + "/discussions");
                return;
            }

            System.out.println("📝 Автор обсуждения: " + post.getAuthorId() + ", текущий пользователь: " + user.getId());

            // Проверяем, является ли пользователь автором обсуждения
            if (post.getAuthorId().equals(user.getId())) {
                System.out.println("✅ Пользователь является автором, удаляем...");
                boolean deleted = discussionService.deletePost(id, user.getId());
                System.out.println("🗑️ Результат удаления: " + deleted);

                if (deleted) {
                    request.setAttribute("message", "Обсуждение успешно удалено!");
                    System.out.println("✅ Обсуждение удалено успешно");
                } else {
                    request.setAttribute("error", "Ошибка при удалении обсуждения из базы данных");
                    System.out.println("❌ Ошибка при удалении из базы");
                }
            } else {
                System.out.println("❌ Пользователь НЕ является автором обсуждения");
                request.setAttribute("error", "Вы можете удалять только свои обсуждения");
            }

        } catch (AuthenticationException e) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        } catch (NumberFormatException e) {
            System.out.println("❌ Ошибка парсинга ID: " + e.getMessage());
            request.setAttribute("error", "Неверный идентификатор обсуждения");
        } catch (Exception e) {
            System.out.println("❌ Общая ошибка: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Ошибка сервера: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/discussions");
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