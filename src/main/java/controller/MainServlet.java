package controller;

import service.UserService;
import service.MeetingService;
import service.DiscussionService;
import model.User;
import model.MeetingPost;
import model.DiscussionPost;
import exceptions.AuthenticationException;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/main")
public class MainServlet extends HttpServlet {
    private UserService userService;
    private MeetingService meetingService;
    private DiscussionService discussionService;

    @Override
    public void init() throws ServletException {
        // Получаю сервисы из контекста приложения
        ServletContext context = getServletContext();
        userService = (UserService) context.getAttribute("userService");
        meetingService = (MeetingService) context.getAttribute("meetingService");
        discussionService = (DiscussionService) context.getAttribute("discussionService");

        if (userService == null || meetingService == null || discussionService == null) {
            throw new ServletException("Сервисы не инициализированы в контексте приложения");
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String sessionId = extractSessionId(request.getCookies());
            if (sessionId == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            User user = userService.getUserBySessionId(sessionId);
            request.setAttribute("user", user);

            // Загружаю встречи
            List<MeetingPost> meetings = meetingService.getAllMeetings(user.getId());
            request.setAttribute("meetings", meetings);

            // Загружаю посты с обсуждениями
            List<DiscussionPost> posts = discussionService.getAllPostsWithLikes(user.getId());
            request.setAttribute("posts", posts);

            String success = request.getParameter("success");
            String error = request.getParameter("error");
            String cancel = request.getParameter("cancel");
            if (cancel != null) request.setAttribute("cancel", cancel);
            if (success != null) request.setAttribute("success", success);
            if (error != null) request.setAttribute("error", error);

            request.getRequestDispatcher("/WEB-INF/views/main.jsp").forward(request, response);

        } catch (AuthenticationException e) {
            response.sendRedirect(request.getContextPath() + "/login");
        } catch (Exception e) {
            System.err.println("Ошибка при загрузке главной страницы: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Ошибка при загрузке данных: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/main.jsp").forward(request, response);
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