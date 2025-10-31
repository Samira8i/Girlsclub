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

@WebServlet("/meeting/delete")
public class DeleteMeetingServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("🔍 DeleteMeetingServlet: начат процесс удаления");
        try {
            UserService userService = ServiceFactory.getUserService();

            String sessionId = extractSessionId(request.getCookies());
            if (sessionId == null) {
                System.out.println("❌ Пользователь не авторизован");
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            User user = userService.getUserBySessionId(sessionId);

            MeetingService meetingService = ServiceFactory.getMeetingService();

            String idParam = request.getParameter("id");
            System.out.println("📨 Получен ID параметр: " + idParam);

            if (idParam == null || idParam.trim().isEmpty()) {
                request.setAttribute("error", "ID встречи не указан");
                response.sendRedirect(request.getContextPath() + "/main");
                return;
            }

            Long id = Long.parseLong(idParam);
            System.out.println("👤 Пользователь ID: " + user.getId() + " пытается удалить встречу ID: " + id);

            // Проверяем, существует ли встреча
            var meeting = meetingService.getMeetingById(id);
            if (meeting == null) {
                System.out.println("❌ Встреча с ID " + id + " не найдена");
                request.setAttribute("error", "Встреча не найдена");
                response.sendRedirect(request.getContextPath() + "/main");
                return;
            }

            System.out.println("📝 Автор встречи: " + meeting.getAuthorId() + ", текущий пользователь: " + user.getId());

            // Проверяем, является ли пользователь автором встречи
            if (meeting.getAuthorId().equals(user.getId())) {
                System.out.println("✅ Пользователь является автором, удаляем...");
                boolean deleted = meetingService.deleteMeeting(id);
                System.out.println("🗑️ Результат удаления: " + deleted);

                if (deleted) {
                    request.setAttribute("message", "Встреча успешно удалена!");
                    System.out.println("✅ Встреча удалена успешно");
                } else {
                    request.setAttribute("error", "Ошибка при удалении встречи из базы данных");
                    System.out.println("❌ Ошибка при удалении из базы");
                }
            } else {
                System.out.println("❌ Пользователь НЕ является автором встречи");
                request.setAttribute("error", "Вы можете удалять только свои встречи");
            }

        } catch (AuthenticationException e) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        } catch (NumberFormatException e) {
            System.out.println("❌ Ошибка парсинга ID: " + e.getMessage());
            request.setAttribute("error", "Неверный идентификатор встречи");
        } catch (Exception e) {
            System.out.println("❌ Общая ошибка: " + e.getMessage());
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