<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="my" tagdir="/WEB-INF/tags" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>GirlsClub Kazan - Создать встречу</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link href="https://fonts.googleapis.com/css2?family=Playfair+Display:wght@400;700&family=Montserrat:wght@300;400;500&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/create-meeting.css">
    <script src="${pageContext.request.contextPath}/js/create-meeting.js"></script>
</head>
<body>
<div id="create-meeting-page" class="page active">
    <div class="container">
        <my:userHeader
                username="${user.username}"
                backUrl="${pageContext.request.contextPath}/main"
                backText="Вернуться на главную"
        />

        <div class="welcome-section">
            <div class="create-meeting-container">
                <h2 class="form-title">
                    <i class="fas fa-calendar-plus" style="margin-right: 15px;"></i>
                    Создать встречу
                </h2>

                <% if (request.getAttribute("error") != null) { %>
                <div class="error-message">
                    <i class="fas fa-exclamation-circle" style="margin-right: 10px;"></i>
                    <%= request.getAttribute("error") %>
                </div>
                <% } %>

                <% if (request.getParameter("success") != null) { %>
                <div class="success-message">
                    <i class="fas fa-check-circle" style="margin-right: 10px;"></i>
                    Встреча успешно создана!
                </div>
                <% } %>

                <form action="${pageContext.request.contextPath}/meeting/create" method="post" id="meetingForm">
                    <div class="form-group">
                        <label class="form-label">
                            <i class="fas fa-heading" style="margin-right: 8px;"></i>
                            Название встречи *
                        </label>
                        <input type="text" class="form-input" name="title" placeholder="Введите название встречи" required>
                        <div class="input-icon">
                            <i class="fas fa-pencil-alt"></i>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="form-label">
                            <i class="fas fa-align-left" style="margin-right: 8px;"></i>
                            Описание встречи *
                        </label>
                        <textarea class="form-textarea" name="description" placeholder="Опишите вашу встречу, цели, программу..." required></textarea>
                        <div class="form-hint">Расскажите участникам, что их ждет на встрече</div>
                    </div>

                    <div class="form-group">
                        <label class="form-label">
                            <i class="fas fa-clock" style="margin-right: 8px;"></i>
                            Дата и время события *
                        </label>
                        <input type="datetime-local" class="form-input" id="eventDate" name="eventDate" required>
                        <div class="date-validation-message" id="dateValidationMessage">
                            ❌ Дата встречи должна быть в будущем
                        </div>
                        <div class="form-hint">Выберите дату и время проведения встречи</div>
                    </div>

                    <div class="form-group">
                        <label class="form-label">
                            <i class="fas fa-users" style="margin-right: 8px;"></i>
                            Максимальное количество участников *
                        </label>
                        <input type="number" class="form-input" name="maxAttendance" min="2" max="50" placeholder="Например: 10" required>
                        <div class="form-hint">Рекомендуем от 2 до 50 участников</div>
                    </div>

                    <div class="form-group">
                        <label class="form-label">
                            <i class="fas fa-map-marker-alt" style="margin-right: 8px;"></i>
                            Место проведения *
                        </label>
                        <input type="text" class="form-input" name="location" placeholder="Укажите место встречи (адрес или онлайн-платформа)" required>
                    </div>

                    <button type="submit" class="form-submit">
                        <i class="fas fa-plus-circle" style="margin-right: 10px;"></i>
                        Создать встречу
                    </button>
                </form>
            </div>
        </div>

        <footer>
            <p>© 2025 GirlsClub Kazan. Все права защищены.</p>
        </footer>
    </div>
</div>
</body>
</html>