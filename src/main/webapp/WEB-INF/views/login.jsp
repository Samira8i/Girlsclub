<%-- src/main/webapp/WEB-INF/views/login.jsp --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>GirlsClub Kazan - Вход</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/auth.css">
</head>
<body>
<div id="login-page" class="page active">
    <div class="container">
        <header>
            <div class="logo">GirlsClub Kazan</div>
            <div class="tagline">Вход в сообщество</div>
        </header>

        <div class="welcome-section">
            <div class="form-container">
                <h2 class="form-title">Вход в клуб</h2>

                <% if (request.getAttribute("error") != null) { %>
                <div class="error-message"><%= request.getAttribute("error") %></div>
                <% } %>

                <form action="${pageContext.request.contextPath}/login" method="post">
                    <div class="form-group">
                        <label class="form-label">Имя пользователя</label>
                        <input type="text" class="form-input" name="username" placeholder="Ваше имя пользователя" required>
                    </div>

                    <div class="form-group">
                        <label class="form-label">Пароль</label>
                        <input type="password" class="form-input" name="password" placeholder="Ваш пароль" required>
                    </div>

                    <button type="submit" class="form-submit">Войти</button>
                </form>

                <div class="form-footer">
                    <p>Еще нет аккаунта? <a href="${pageContext.request.contextPath}/register" class="form-link">Зарегистрироваться</a></p>
                </div>
            </div>
        </div>

        <footer>
            <p>© 2025 GirlsClub Kazan. Все права защищены.</p>
        </footer>
    </div>
</div>
</body>
</html>