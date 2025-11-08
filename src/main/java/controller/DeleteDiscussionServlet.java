package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.User;
import service.DiscussionService;
import service.UserService;
import exceptions.AuthenticationException;

import java.io.IOException;

@WebServlet("/discussion/delete")
public class DeleteDiscussionServlet extends HttpServlet {

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
            DiscussionService discussionService = new DiscussionService();
            String idParam = request.getParameter("id");

            if (idParam == null || idParam.trim().isEmpty()) {
                request.setAttribute("error", "ID обсуждения не указан");
                response.sendRedirect(request.getContextPath() + "/main?section=discussions");
                return;
            }

            Long id = Long.parseLong(idParam);
            var post = discussionService.getPostById(id);
            if (post == null) {
                System.out.println(" Обсуждение с ID " + id + " не найдена");
                request.setAttribute("error", "Обсуждение не найдено");
                response.sendRedirect(request.getContextPath() + "/main?section=discussions");
                return;
            }

            if (post.getAuthorId().equals(user.getId())) {
                boolean deleted = discussionService.deletePost(id, user.getId());
                if (deleted) {
                    request.setAttribute("success", "discussion_deleted");
                } else {
                    request.setAttribute("error", "Ошибка при удалении обсуждения");
                }
            } else {
                request.setAttribute("error", "Вы можете удалять только свои обсуждения");
            }

        } catch (AuthenticationException e) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        } catch (NumberFormatException e) {
            System.out.println(" Ошибка парсинга ID: " + e.getMessage());
            request.setAttribute("error", "Неверный идентификатор обсуждения");
        } catch (Exception e) {
            System.out.println("Общая ошибка: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Ошибка сервера: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/main?section=discussions");
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