<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Редактировать встречу - GirlsClub Kazan</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link href="https://fonts.googleapis.com/css2?family=Playfair+Display:wght@400;700&family=Montserrat:wght@300;400;500&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/edit-meeting.css">
    <script src="${pageContext.request.contextPath}/js/edit-meeting.js"></script>
</head>
<body>
<div class="container">
    <div class="user-header">
        <div class="user-info">
            <div class="user-avatar">
                <i class="fas fa-user"></i>
            </div>
            <div class="user-name">
                ${user.username}
            </div>
        </div>
        <a href="${pageContext.request.contextPath}/main" class="back-btn">
            <i class="fas fa-arrow-left"></i> Назад к встречам
        </a>
    </div>

    <div class="form-container">
        <h1 class="form-title">Редактировать встречу</h1>

        <!-- Сообщения об ошибках -->
        <c:if test="${not empty error}">
            <div class="error-message">
                ❌ ${error}
            </div>
        </c:if>

        <!-- Информация о встрече -->
        <c:if test="${not empty meeting}">
            <div class="meeting-info">
                <div class="info-item">
                    <span class="info-label">Автор:</span>
                    <span class="info-value">${user.username}</span>
                </div>
                <c:if test="${not empty meeting.createdAt}">
                    <div class="info-item">
                        <span class="info-label">Создано:</span>
                        <span class="info-value">${meeting.createdAt}</span>
                    </div>
                </c:if>
                <div class="info-item">
                    <span class="info-label">Текущее время встречи:</span>
                    <span class="info-value">${meeting.eventDate}</span>
                </div>
            </div>

            <form action="${pageContext.request.contextPath}/meeting/edit" method="post" class="meeting-form">
                <input type="hidden" name="id" value="${meeting.id}">

                <div class="form-group">
                    <label for="title" class="form-label">
                        <i class="fas fa-heading"></i> Название встречи
                    </label>
                    <input type="text"
                           id="title"
                           name="title"
                           class="form-input"
                           placeholder="Введите название встречи..."
                           value="${meeting.title}"
                           maxlength="200"
                           required>
                    <div class="character-count">
                        <span id="title-count">${fn:length(meeting.title)}</span>/200 символов
                    </div>
                </div>

                <div class="form-group">
                    <label for="description" class="form-label">
                        <i class="fas fa-align-left"></i> Описание встречи
                    </label>
                    <textarea id="description"
                              name="description"
                              class="form-textarea"
                              placeholder="Опишите цель и программу встречи..."
                              maxlength="1000"
                              required>${meeting.description}</textarea>
                    <div class="character-count">
                        <span id="description-count">${fn:length(meeting.description)}</span>/1000 символов
                    </div>
                </div>

                <div class="form-group">
                    <label for="eventDate" class="form-label">
                        <i class="fas fa-calendar-alt"></i> Дата и время встречи
                    </label>
                    <input type="datetime-local"
                           id="eventDate"
                           name="eventDate"
                           class="form-input"
                           value="${meeting.eventDate.toString().replace(' ', 'T').substring(0, 16)}"
                           required>
                    <div class="form-hint">
                        💡 Выберите дату и время проведения встречи
                    </div>
                </div>

                <div class="form-group">
                    <label for="maxAttendance" class="form-label">
                        <i class="fas fa-users"></i> Максимальное количество участников
                    </label>
                    <input type="number"
                           id="maxAttendance"
                           name="maxAttendance"
                           class="form-input"
                           value="${meeting.maxAttendance}"
                           min="2"
                           max="100"
                           required
                           placeholder="От 2 до 100 человек">
                    <div class="form-hint">
                        👥 Укажите максимальное количество участников
                    </div>
                </div>

                <div class="form-group">
                    <label for="location" class="form-label">
                        <i class="fas fa-map-marker-alt"></i> Место проведения
                    </label>
                    <input type="text"
                           id="location"
                           name="location"
                           class="form-input"
                           placeholder="Укажите место встречи..."
                           value="${meeting.location}"
                           maxlength="300"
                           required>
                    <div class="character-count">
                        <span id="location-count">${fn:length(meeting.location)}</span>/300 символов
                    </div>
                </div>

                <div class="form-actions">
                    <form action="${pageContext.request.contextPath}/meeting/delete" method="post" class="delete-form">
                        <input type="hidden" name="id" value="${meeting.id}">
                        <button type="submit" class="delete-btn" onclick="return confirm('Вы уверены, что хотите удалить эту встречу? Это действие нельзя отменить.')">
                            <i class="fas fa-trash"></i> Удалить встречу
                        </button>
                    </form>
                    <div>
                        <button type="submit" class="submit-btn">
                            <i class="fas fa-save"></i> Сохранить изменения
                        </button>
                    </div>
                </div>
            </form>
        </c:if>

        <c:if test="${empty meeting}">
            <div class="error-message">
                <i class="fas fa-exclamation-triangle"></i>
                Встреча не найдена или у вас нет прав для ее редактирования.
            </div>
            <div style="text-align: center; margin-top: 20px;">
                <a href="${pageContext.request.contextPath}/main" class="back-btn">
                    <i class="fas fa-arrow-left"></i> Вернуться к встречам
                </a>
            </div>
        </c:if>
    </div>
</div>
</body>
</html>