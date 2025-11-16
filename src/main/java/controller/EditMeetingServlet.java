package controller;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.MeetingPost;
import model.User;
import service.MeetingService;
import service.UserService;
import exceptions.AuthenticationException;

import java.io.IOException;

@WebServlet("/meeting/edit")
public class EditMeetingServlet extends HttpServlet {
    private UserService userService;
    private MeetingService meetingService;

    @Override
    public void init() throws ServletException {
        ServletContext context = getServletContext();
        userService = (UserService) context.getAttribute("userService");
        meetingService = (MeetingService) context.getAttribute("meetingService");

        if (userService == null || meetingService == null) {
            throw new ServletException("Сервисы не инициализированы в контексте приложения");
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String sessionId = extractSessionId(request.getCookies());
            if (sessionId == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            User user = userService.getUserBySessionId(sessionId);

            String idParam = request.getParameter("id");
            if (idParam == null || idParam.trim().isEmpty()) {
                request.setAttribute("error", "ID встречи не указан");
                response.sendRedirect(request.getContextPath() + "/main");
                return;
            }

            Long meetingId = Long.parseLong(idParam);

            MeetingPost meeting = meetingService.getMeetingById(meetingId);

            if (meeting == null) {
                request.setAttribute("error", "Встреча не найдена");
                response.sendRedirect(request.getContextPath() + "/main");
                return;
            }
            if (!meeting.getAuthorId().equals(user.getId())) {
                request.setAttribute("error", "Вы можете редактировать только свои встречи");
                response.sendRedirect(request.getContextPath() + "/main");
                return;
            }

            request.setAttribute("meeting", meeting);
            request.setAttribute("user", user);
            request.getRequestDispatcher("/WEB-INF/views/edit-meeting.jsp").forward(request, response);

        } catch (AuthenticationException e) {
            response.sendRedirect(request.getContextPath() + "/login");
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Неверный идентификатор встречи");
            response.sendRedirect(request.getContextPath() + "/main");
        } catch (Exception e) {
            System.err.println("Ошибка при загрузке встречи для редактирования: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Ошибка при загрузке встречи");
            response.sendRedirect(request.getContextPath() + "/main");
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String sessionId = extractSessionId(request.getCookies());
            if (sessionId == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            User user = userService.getUserBySessionId(sessionId);

            String idParam = request.getParameter("id");
            String title = request.getParameter("title");
            String description = request.getParameter("description");
            String eventDate = request.getParameter("eventDate");
            String maxAttendanceStr = request.getParameter("maxAttendance");
            String location = request.getParameter("location");

            if (idParam == null || idParam.trim().isEmpty() ||
                    title == null || title.trim().isEmpty() ||
                    description == null || description.trim().isEmpty() ||
                    eventDate == null || eventDate.trim().isEmpty() ||
                    maxAttendanceStr == null || maxAttendanceStr.trim().isEmpty() ||
                    location == null || location.trim().isEmpty()) {

                request.setAttribute("error", "Все поля обязательны для заполнения");
                request.setAttribute("user", user);
                request.getRequestDispatcher("/WEB-INF/views/edit-meeting.jsp").forward(request, response);
                return;
            }

            Long meetingId = Long.parseLong(idParam);
            //TODO:повторная проверка в post и get, нужно ли?
            MeetingPost existingMeeting = meetingService.getMeetingById(meetingId);
            if (existingMeeting == null) {
                request.setAttribute("error", "Встреча не найдена");
                response.sendRedirect(request.getContextPath() + "/main");
                return;
            }

            if (!existingMeeting.getAuthorId().equals(user.getId())) {
                request.setAttribute("error", "Вы можете редактировать только свои встречи");
                response.sendRedirect(request.getContextPath() + "/main");
                return;
            }

            int maxAttendance = Integer.parseInt(maxAttendanceStr);
            // Преобразуем дату в правильный формат
            String formattedDate = eventDate.replace("T", " ") + ":00";

            boolean success = meetingService.updateMeeting(
                    meetingId,
                    title.trim(),
                    description.trim(),
                    formattedDate,
                    maxAttendance,
                    location.trim()
            );

            if (success) {
                System.out.println("Встреча успешно обновлена!");
                response.sendRedirect(request.getContextPath() + "/main?success=meeting_updated");
            } else {
                System.err.println("Ошибка при обновлении встречи в сервисе");
                request.setAttribute("error", "Ошибка при обновлении встречи. Проверьте введенные данные.");
                request.setAttribute("meeting", existingMeeting);
                request.setAttribute("user", user);
                request.getRequestDispatcher("/WEB-INF/views/edit-meeting.jsp").forward(request, response);
            }
        } catch (AuthenticationException e) {
            response.sendRedirect(request.getContextPath() + "/login");
        } catch (NumberFormatException e) {
            System.err.println("Ошибка парсинга числа: " + e.getMessage());
            request.setAttribute("error", "Некорректное значение для количества участников");
            request.getRequestDispatcher("/WEB-INF/views/edit-meeting.jsp").forward(request, response);
        } catch (Exception e) {
            System.err.println("Системная ошибка: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Системная ошибка: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/edit-meeting.jsp").forward(request, response);
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