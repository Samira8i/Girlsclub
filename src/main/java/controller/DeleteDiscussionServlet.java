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
        try {
            UserService userService = ServiceFactory.getUserService();
            String sessionId = extractSessionId(request.getCookies());
            if (sessionId == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            User user = userService.getUserBySessionId(sessionId);
            DiscussionService discussionService = ServiceFactory.getDiscussionService();
            String idParam = request.getParameter("id");

            if (idParam == null || idParam.trim().isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/main?error=ID_обсуждения_не_указан&section=discussions");
                return;
            }

            Long id = Long.parseLong(idParam);
            var post = discussionService.getPostById(id);
            if (post == null) {
                response.sendRedirect(request.getContextPath() + "/main?error=Обсуждение_не_найдено&section=discussions");
                return;
            }
            if (post.getAuthorId().equals(user.getId())) {
                boolean deleted = discussionService.deletePost(id, user.getId());
                if (deleted) {
                    response.sendRedirect(request.getContextPath() + "/main?success=discussion_deleted&section=discussions");
                } else {
                    response.sendRedirect(request.getContextPath() + "/main?error=Ошибка_при_удалении_обсуждения&section=discussions");
                }
            } else {
                response.sendRedirect(request.getContextPath() + "/main?error=Вы_можете_удалять_только_свои_обсуждения&section=discussions");
            }

        } catch (AuthenticationException e) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        } catch (NumberFormatException e) {
            System.out.println(" Ошибка парсинга ID: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/main?error=Неверный_идентификатор_обсуждения&section=discussions");
        } catch (Exception e) {
            System.out.println("Общая ошибка: " + e.getMessage());
            e.printStackTrace();
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