package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.User;
import service.MeetingService;
import service.UserService;
import exceptions.AuthenticationException;

import java.io.IOException;

@WebServlet("/meeting/delete")
public class DeleteMeetingServlet extends HttpServlet {

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

            MeetingService meetingService = new MeetingService();

            String idParam = request.getParameter("id");

            if (idParam == null || idParam.trim().isEmpty()) {
                request.setAttribute("error", "ID встречи не указан");
                response.sendRedirect(request.getContextPath() + "/main");
                return;
            }

            Long id = Long.parseLong(idParam);

            var meeting = meetingService.getMeetingById(id);
            if (meeting == null) {
                System.out.println(" Встреча с ID " + id + " не найдена");
                request.setAttribute("error", "Встреча не найдена");
                response.sendRedirect(request.getContextPath() + "/main");
                return;
            }
            if (meeting.getAuthorId().equals(user.getId())) {
                boolean deleted = meetingService.deleteMeeting(id);
                if (deleted) {
                    request.setAttribute("message", "Встреча успешно удалена!");
                } else {
                    request.setAttribute("error", "Ошибка при удалении встречи из базы данных");
                }
            } else {
                request.setAttribute("error", "Вы можете удалять только свои встречи");
            }

        } catch (AuthenticationException e) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Неверный идентификатор встречи");
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Ошибка сервера: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/main");
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