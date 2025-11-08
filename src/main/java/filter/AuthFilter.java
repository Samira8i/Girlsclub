package filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.UserService;
import exceptions.AuthenticationException;
import util.DatabaseUtil;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;

@WebFilter("/*")
public class AuthFilter implements Filter {

    private final List<String> publicPaths = List.of(
            "/login", "/register", "/css/", "/js/", "/images/", "/test-db", "/"
    );

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String path = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());

        // Пропускаем публичные пути
        if (isPublicPath(path)) {
            chain.doFilter(request, response);
            return;
        }

        // Извлекаем sessionId из куки
        String sessionId = extractSessionId(httpRequest.getCookies());

        if (sessionId == null) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
            return;
        }

        try (Connection conn = DatabaseUtil.getConnection()) {
            UserService userService = new UserService(conn);

            // Получаем пользователя по sessionId
            var user = userService.getUserBySessionId(sessionId);
            httpRequest.setAttribute("user", user);
            chain.doFilter(request, response);

        } catch (AuthenticationException e) {
            // Если сессия невалидна, перенаправляем на логин
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
            return;
        } catch (Exception e) {
            System.err.println("Ошибка в AuthFilter: " + e.getMessage());
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
            return;
        }
    }

    private boolean isPublicPath(String path) {
        return publicPaths.stream().anyMatch(path::startsWith);
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