package controller;

import service.UserService;
import service.ServiceFactory; // ← ИМПОРТИРУЕМ ФАБРИКУ
import exceptions.AuthenticationException;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
// ❌ УБИРАЕМ import java.sql.Connection; и DatabaseUtil

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        try {
            // ✅ ПРАВИЛЬНО: используем фабрику вместо Connection
            UserService userService = ServiceFactory.getUserService();

            String sessionId = userService.registerUser(username, password, confirmPassword);

            Cookie sessionCookie = new Cookie("sessionId", sessionId);
            sessionCookie.setMaxAge(30 * 24 * 60 * 60);
            sessionCookie.setPath("/");
            response.addCookie(sessionCookie);

            response.sendRedirect(request.getContextPath() + "/main");

        } catch (AuthenticationException e) {
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Ошибка сервера: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
        }
    }
}