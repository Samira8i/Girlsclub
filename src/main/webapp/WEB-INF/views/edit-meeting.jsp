<%-- src/main/webapp/WEB-INF/views/edit-meeting.jsp --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Редактировать встречу - GirlsClub Kazan</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link href="https://fonts.googleapis.com/css2?family=Playfair+Display:wght@400;700&family=Montserrat:wght@300;400;500&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/create-meeting.css">
    <style>
        body {
            background: linear-gradient(135deg, #19171b 0%, #2b0307 100%);
            min-height: 100vh;
            color: #EDEBDD;
        }

        .form-container {
            background: linear-gradient(135deg, rgba(24, 23, 23, 0.9) 0%, rgba(43, 3, 7, 0.9) 100%);
            border-radius: 15px;
            border: 1px solid rgba(129, 1, 0, 0.3);
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.5);
        }

        .header h1 {
            color: #EDEBDD;
            font-family: 'Playfair Display', serif;
        }

        .back-btn {
            color: #EDEBDD;
            background: rgba(129, 1, 0, 0.3);
            padding: 10px 20px;
            border-radius: 8px;
            border: 1px solid rgba(129, 1, 0, 0.5);
        }

        .back-btn:hover {
            background: rgba(129, 1, 0, 0.5);
            color: #EDEBDD;
        }

        .form-group label {
            color: #EDEBDD;
        }

        .form-group input,
        .form-group textarea {
            background: rgba(237, 235, 221, 0.1);
            border: 2px solid rgba(129, 1, 0, 0.5);
            color: #EDEBDD;
        }

        .form-group input:focus,
        .form-group textarea:focus {
            border-color: #810100;
            background: rgba(237, 235, 221, 0.15);
        }

        .form-group input::placeholder,
        .form-group textarea::placeholder {
            color: rgba(237, 235, 221, 0.6);
        }
    </style>
</head>
<body>
<div class="page active">
    <div class="container">
        <div class="header">
            <a href="${pageContext.request.contextPath}/main" class="back-btn">
                <i class="fas fa-arrow-left"></i> Назад к встречам
            </a>
            <h1><i class="fas fa-edit"></i> Редактировать встречу</h1>
        </div>

        <div class="form-container">
            <!-- Сообщения об ошибках -->
            <c:if test="${not empty error}">
                <div class="error-message">
                    <i class="fas fa-exclamation-circle"></i> ${error}
                </div>
            </c:if>

            <c:if test="${not empty meeting}">
                <form action="${pageContext.request.contextPath}/meeting/edit" method="post" class="meeting-form">
                    <input type="hidden" name="id" value="${meeting.id}">

                    <div class="form-group">
                        <label for="title">Название встречи *</label>
                        <input type="text" id="title" name="title" value="${meeting.title}" required
                               placeholder="Введите название встречи">
                    </div>

                    <div class="form-group">
                        <label for="description">Описание *</label>
                        <textarea id="description" name="description" rows="5" required
                                  placeholder="Опишите цель и программу встречи">${meeting.description}</textarea>
                    </div>

                    <div class="form-group">
                        <label for="eventDate">Дата и время встречи *</label>
                        <input type="datetime-local" id="eventDate" name="eventDate" required
                               value="${meeting.eventDate.toString().replace(' ', 'T').substring(0, 16)}">
                    </div>

                    <div class="form-group">
                        <label for="maxAttendance">Максимальное количество участников *</label>
                        <input type="number" id="maxAttendance" name="maxAttendance"
                               value="${meeting.maxAttendance}" min="2" max="100" required
                               placeholder="От 2 до 100 человек">
                    </div>

                    <div class="form-group">
                        <label for="location">Место проведения *</label>
                        <input type="text" id="location" name="location" value="${meeting.location}" required
                               placeholder="Укажите место встречи">
                    </div>

                    <div class="form-actions">
                        <button type="submit" class="submit-btn">
                            <i class="fas fa-save"></i> Сохранить изменения
                        </button>
                        <a href="${pageContext.request.contextPath}/main" class="cancel-btn">
                            <i class="fas fa-times"></i> Отмена
                        </a>
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
</div>

<script>
    // Предварительная валидация формы
    document.addEventListener('DOMContentLoaded', function() {
        const form = document.querySelector('.meeting-form');
        if (form) {
            form.addEventListener('submit', function(e) {
                const title = document.getElementById('title').value.trim();
                const description = document.getElementById('description').value.trim();
                const eventDate = document.getElementById('eventDate').value;
                const maxAttendance = document.getElementById('maxAttendance').value;
                const location = document.getElementById('location').value.trim();

                if (!title || !description || !eventDate || !maxAttendance || !location) {
                    e.preventDefault();
                    alert('❌ Пожалуйста, заполните все обязательные поля');
                    return;
                }

                if (maxAttendance < 2 || maxAttendance > 100) {
                    e.preventDefault();
                    alert('❌ Количество участников должно быть от 2 до 100');
                    return;
                }

                // Проверяем, что дата в будущем
                const selectedDate = new Date(eventDate);
                const now = new Date();
                if (selectedDate <= now) {
                    e.preventDefault();
                    alert('❌ Дата встречи должна быть в будущем');
                    return;
                }
            });
        }

        // Устанавливаем минимальную дату как сегодня
        const now = new Date();
        const year = now.getFullYear();
        const month = String(now.getMonth() + 1).padStart(2, '0');
        const day = String(now.getDate()).padStart(2, '0');
        const hours = String(now.getHours()).padStart(2, '0');
        const minutes = String(now.getMinutes()).padStart(2, '0');

        const minDateTime = `${year}-${month}-${day}T${hours}:${minutes}`;
        const dateInput = document.getElementById('eventDate');
        if (dateInput) {
            dateInput.min = minDateTime;
        }
    });
</script>
</body>
</html>