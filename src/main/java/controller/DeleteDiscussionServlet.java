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
                // ПЕРЕНАПРАВЛЯЕМ НА ГЛАВНУЮ
                response.sendRedirect(request.getContextPath() + "/main?error=ID_обсуждения_не_указан&section=discussions");
                return;
            }

            Long id = Long.parseLong(idParam);
            System.out.println("👤 Пользователь ID: " + user.getId() + " пытается удалить обсуждение ID: " + id);

            // Проверяем, существует ли обсуждение
            var post = discussionService.getPostById(id);
            if (post == null) {
                System.out.println("❌ Обсуждение с ID " + id + " не найдено");
                // ПЕРЕНАПРАВЛЯЕМ НА ГЛАВНУЮ
                response.sendRedirect(request.getContextPath() + "/main?error=Обсуждение_не_найдено&section=discussions");
                return;
            }

            System.out.println("📝 Автор обсуждения: " + post.getAuthorId() + ", текущий пользователь: " + user.getId());

            // Проверяем, является ли пользователь автором обсуждения
            if (post.getAuthorId().equals(user.getId())) {
                System.out.println("✅ Пользователь является автором, удаляем...");
                boolean deleted = discussionService.deletePost(id, user.getId());
                System.out.println("🗑️ Результат удаления: " + deleted);

                if (deleted) {
                    System.out.println("✅ Обсуждение удалено успешно");
                    // ПЕРЕНАПРАВЛЯЕМ НА ГЛАВНУЮ С УСПЕХОМ
                    response.sendRedirect(request.getContextPath() + "/main?success=discussion_deleted&section=discussions");
                } else {
                    System.out.println("❌ Ошибка при удалении из базы");
                    // ПЕРЕНАПРАВЛЯЕМ НА ГЛАВНУЮ С ОШИБКОЙ
                    response.sendRedirect(request.getContextPath() + "/main?error=Ошибка_при_удалении_обсуждения&section=discussions");
                }
            } else {
                System.out.println("❌ Пользователь НЕ является автором обсуждения");
                // ПЕРЕНАПРАВЛЯЕМ НА ГЛАВНУЮ С ОШИБКОЙ
                response.sendRedirect(request.getContextPath() + "/main?error=Вы_можете_удалять_только_свои_обсуждения&section=discussions");
            }

        } catch (AuthenticationException e) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        } catch (NumberFormatException e) {
            System.out.println("❌ Ошибка парсинга ID: " + e.getMessage());
            // ПЕРЕНАПРАВЛЯЕМ НА ГЛАВНУЮ С ОШИБКОЙ
            response.sendRedirect(request.getContextPath() + "/main?error=Неверный_идентификатор_обсуждения&section=discussions");
        } catch (Exception e) {
            System.out.println("❌ Общая ошибка: " + e.getMessage());
            e.printStackTrace();
            // ПЕРЕНАПРАВЛЯЕМ НА ГЛАВНУЮ С ОШИБКОЙ
            response.sendRedirect(request.getContextPath() + "/main?error=Ошибка_сервера&section=discussions");
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