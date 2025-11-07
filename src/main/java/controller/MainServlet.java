package controller;

import model.DiscussionComment;
import service.ServiceFactory;
import service.UserService;
import service.MeetingService;
import service.EventRegistrationService;
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

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            UserService userService = ServiceFactory.getUserService();
            String sessionId = extractSessionId(request.getCookies());
            if (sessionId == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }
            User user = userService.getUserBySessionId(sessionId);
            request.setAttribute("user", user);
            // Загружаю встречи
            MeetingService meetingService = ServiceFactory.getMeetingService();
            EventRegistrationService registrationService = ServiceFactory.getEventRegistrationService();
            List<MeetingPost> meetings = meetingService.getAllMeetings();
            for (MeetingPost meeting : meetings) {
                boolean isRegistered = registrationService.isUserRegistered(meeting.getId(), user.getId());
                meeting.setUserRegistered(isRegistered);

                var participants = registrationService.getRegisteredUsers(meeting.getId());
                meeting.setParticipants(participants);

                int availableSpots = registrationService.getAvailableSpots(meeting.getId(), meeting.getMaxAttendance());
                meeting.setAvailableSpots(availableSpots);

                boolean isFull = registrationService.isEventFull(meeting.getId(), meeting.getMaxAttendance());
                meeting.setFull(isFull);
            }
            request.setAttribute("meetings", meetings);
            // загружаю посты с обсуждениями
            DiscussionService discussionService = ServiceFactory.getDiscussionService();
            List<DiscussionPost> posts = discussionService.getAllPostsWithLikes(user.getId());
            request.setAttribute("posts", posts);
            String success = request.getParameter("success");
            String error = request.getParameter("error");
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