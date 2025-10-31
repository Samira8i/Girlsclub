package controller;

import service.ServiceFactory;
import service.UserService;
import exceptions.AuthenticationException;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        try {
            // ✅ ПРАВИЛЬНО
            UserService userService = ServiceFactory.getUserService();
            String sessionId = userService.loginUser(username, password);

            Cookie sessionCookie = new Cookie("sessionId", sessionId);
            sessionCookie.setMaxAge(30 * 24 * 60 * 60);
            sessionCookie.setPath("/");
            response.addCookie(sessionCookie);

            response.sendRedirect(request.getContextPath() + "/main");

        } catch (AuthenticationException e) {
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Ошибка сервера: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
        }
    }
}