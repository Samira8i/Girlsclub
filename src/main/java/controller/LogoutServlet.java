package controller;

import service.ServiceFactory;
import service.UserService;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            UserService userService = ServiceFactory.getUserService();

            // Удаляем sessionId из кук
            String sessionId = extractSessionId(request.getCookies());
            if (sessionId != null) {
                userService.logoutUser(sessionId); // Удаляем сессию из БД

                // Удаляем куку
                Cookie sessionCookie = new Cookie("sessionId", "");
                sessionCookie.setMaxAge(0); // Удаляем куку
                sessionCookie.setPath("/");
                response.addCookie(sessionCookie);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        response.sendRedirect(request.getContextPath() + "/login");
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