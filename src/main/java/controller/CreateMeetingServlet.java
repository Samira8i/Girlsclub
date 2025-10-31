package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.User;
import service.ServiceFactory;
import service.MeetingService;
import service.UserService;
import exceptions.AuthenticationException;

import java.io.IOException;

@WebServlet("/meeting/create")
public class CreateMeetingServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            UserService userService = ServiceFactory.getUserService();

            String sessionId = extractSessionId(request.getCookies());
            if (sessionId == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            User user = userService.getUserBySessionId(sessionId);
            request.setAttribute("user", user);
            request.getRequestDispatcher("/WEB-INF/views/create-meeting.jsp").forward(request, response);

        } catch (AuthenticationException e) {
            response.sendRedirect(request.getContextPath() + "/login");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/login");
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

            String title = request.getParameter("title");
            String description = request.getParameter("description");
            String eventDate = request.getParameter("eventDate");
            String maxAttendanceStr = request.getParameter("maxAttendance");
            String location = request.getParameter("location");

            // Логируем полученные данные для отладки
            System.out.println("=== ДЕБАГ ИНФОРМАЦИЯ ===");
            System.out.println("Title: " + title);
            System.out.println("Description: " + description);
            System.out.println("EventDate: " + eventDate);
            System.out.println("MaxAttendance: " + maxAttendanceStr);
            System.out.println("Location: " + location);
            System.out.println("AuthorId: " + user.getId());
            System.out.println("======================");

            // Валидация
            if (title == null || title.trim().isEmpty() ||
                    description == null || description.trim().isEmpty() ||
                    eventDate == null || eventDate.trim().isEmpty() ||
                    maxAttendanceStr == null || maxAttendanceStr.trim().isEmpty() ||
                    location == null || location.trim().isEmpty()) {

                request.setAttribute("error", "❌ Все поля обязательны для заполнения");
                request.setAttribute("user", user);
                request.getRequestDispatcher("/WEB-INF/views/create-meeting.jsp").forward(request, response);
                return;
            }

            MeetingService meetingService = ServiceFactory.getMeetingService();
            int maxAttendance = Integer.parseInt(maxAttendanceStr);

            // Преобразуем дату в правильный формат
            String formattedDate = eventDate.replace("T", " ") + ":00";
            System.out.println("📅 Форматированная дата: " + formattedDate);

            boolean success = meetingService.createMeeting(
                    title.trim(),
                    description.trim(),
                    formattedDate,
                    maxAttendance,
                    location.trim(),
                    user.getId()
            );

            if (success) {
                System.out.println("✅ Встреча успешно создана!");
                response.sendRedirect(request.getContextPath() + "/main?success=meeting_created");
            } else {
                System.err.println("❌ Ошибка при создании встречи в сервисе");
                request.setAttribute("error", "❌ Ошибка при создании встречи. Проверьте введенные данные.");
                request.setAttribute("user", user);
                request.getRequestDispatcher("/WEB-INF/views/create-meeting.jsp").forward(request, response);
            }
        } catch (AuthenticationException e) {
            response.sendRedirect(request.getContextPath() + "/login");
        } catch (NumberFormatException e) {
            System.err.println("❌ Ошибка парсинга числа: " + e.getMessage());
            request.setAttribute("error", "❌ Некорректное значение для количества участников");
            request.getRequestDispatcher("/WEB-INF/views/create-meeting.jsp").forward(request, response);
        } catch (Exception e) {
            System.err.println("❌ Системная ошибка: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "❌ Системная ошибка: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/create-meeting.jsp").forward(request, response);
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