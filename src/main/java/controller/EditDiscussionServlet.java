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
                response.sendRedirect(request.getContextPath() + "/main?error=ID_обсуждения_не_указан&section=discussions");
                return;
            }

            DiscussionService discussionService = ServiceFactory.getDiscussionService();
            Long discussionId = Long.parseLong(idParam);

            DiscussionPost discussion = discussionService.getPostById(discussionId);

            if (discussion == null) {
                response.sendRedirect(request.getContextPath() + "/main?error=Обсуждение_не_найдено&section=discussions");
                return;
            }

            // Проверяем, является ли пользователь автором
            if (!discussion.getAuthorId().equals(user.getId())) {
                response.sendRedirect(request.getContextPath() + "/main?error=Вы_можете_редактировать_только_свои_обсуждения&section=discussions");
                return;
            }

            request.setAttribute("discussion", discussion);
            request.setAttribute("user", user);
            request.getRequestDispatcher("/WEB-INF/views/edit-discussion.jsp").forward(request, response);

        } catch (AuthenticationException e) {
            response.sendRedirect(request.getContextPath() + "/login");
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/main?error=Неверный_идентификатор_обсуждения&section=discussions");
        } catch (Exception e) {
            System.err.println("❌ Ошибка при загрузке обсуждения для редактирования: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/main?error=Ошибка_при_загрузке_обсуждения&section=discussions");
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

            if (idParam == null || idParam.trim().isEmpty() ||
                    title == null || title.trim().isEmpty() ||
                    content == null || content.trim().isEmpty()) {

                request.setAttribute("error", "Все поля обязательны для заполнения");

                // Восстанавливаем данные для формы
                DiscussionPost discussion = new DiscussionPost();
                discussion.setId(Long.parseLong(idParam));
                discussion.setTitle(title);
                discussion.setContent(content);

                request.setAttribute("discussion", discussion);
                request.setAttribute("user", user);
                request.getRequestDispatcher("/WEB-INF/views/edit-discussion.jsp").forward(request, response);
                return;
            }

            DiscussionService discussionService = ServiceFactory.getDiscussionService();
            Long discussionId = Long.parseLong(idParam);
            DiscussionPost existingDiscussion = discussionService.getPostById(discussionId);
            if (existingDiscussion == null) {
                response.sendRedirect(request.getContextPath() + "/main?error=Обсуждение_не_найдено&section=discussions");
                return;
            }

            if (!existingDiscussion.getAuthorId().equals(user.getId())) {
                response.sendRedirect(request.getContextPath() + "/main?error=Вы_можете_редактировать_только_свои_обсуждения&section=discussions");
                return;
            }

            // Обновляем обсуждение
            boolean success = discussionService.updatePost(
                    discussionId,
                    title.trim(),
                    content.trim(),
                    user.getId()
            );

            if (success) {
                System.out.println("✅ Обсуждение успешно обновлено!");
                response.sendRedirect(request.getContextPath() + "/main?success=discussion_updated&section=discussions");
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

            try {
                DiscussionPost discussion = new DiscussionPost();
                discussion.setId(Long.parseLong(request.getParameter("id")));
                discussion.setTitle(request.getParameter("title"));
                discussion.setContent(request.getParameter("content"));
                request.setAttribute("discussion", discussion);
                request.setAttribute("user", ServiceFactory.getUserService().getUserBySessionId(extractSessionId(request.getCookies())));
            } catch (Exception ex) {
            }

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