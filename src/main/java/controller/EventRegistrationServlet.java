package controller;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.User;
import service.EventRegistrationService;
import service.MeetingService;
import service.UserService;

import java.io.IOException;

@WebServlet("/meeting/register")
public class EventRegistrationServlet extends HttpServlet {
    private UserService userService;
    private EventRegistrationService registrationService;
    private MeetingService meetingService;

    @Override
    public void init() throws ServletException {
        ServletContext context = getServletContext();
        userService = (UserService) context.getAttribute("userService");
        registrationService = (EventRegistrationService) context.getAttribute("eventRegistrationService");
        meetingService = (MeetingService) context.getAttribute("meetingService");

        if (userService == null || registrationService == null || meetingService == null) {
            throw new ServletException("Сервисы не инициализированы в контексте приложения");
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String sessionId = extractSessionId(request.getCookies());
        if (sessionId == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        try {
            User user = userService.getUserBySessionId(sessionId);
            String postIdParam = request.getParameter("postId");
            String action = request.getParameter("action");
            if (postIdParam == null || action == null) {
                response.sendRedirect(request.getContextPath() + "/main");
                return;
            }

            Long postId = Long.parseLong(postIdParam);

            var meeting = meetingService.getMeetingById(postId);
            if (meeting == null) {
                response.sendRedirect(request.getContextPath() + "/main?error=meeting_not_found");
                return;
            }

            boolean success = false;
            if ("register".equals(action)) {
                // Проверяем, есть ли свободные места
                int registeredCount = registrationService.getRegisteredUsersCount(postId);
                if (registeredCount >= meeting.getMaxAttendance()) {
                    response.sendRedirect(request.getContextPath() + "/main?error=event_full");
                    return;
                }
                success = registrationService.registerForEvent(postId, user.getId());
            } else if ("cancel".equals(action)) {
                success = registrationService.cancelRegistration(postId, user.getId());
            }
            if (success) {
                response.sendRedirect(request.getContextPath() + "/main?success=registration_updated");
            } else {
                response.sendRedirect(request.getContextPath() + "/main?error=registration_failed");
            }

        } catch (Exception e) {
            System.err.println("Ошибка при обработке записи: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/main?error=server_error");
        }
    }

    private String extractSessionId(jakarta.servlet.http.Cookie[] cookies) {
        if (cookies != null) {
            for (jakarta.servlet.http.Cookie cookie : cookies) {
                if ("sessionId".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}