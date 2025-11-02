<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>GirlsClub Kazan - Главная</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link href="https://fonts.googleapis.com/css2?family=Playfair+Display:wght@400;700&family=Montserrat:wght@300;400;500&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
    <style>
        /* Стили для красивого отображения даты */
        .meeting-date {
            font-weight: 500;
            color: #EDEBDD;
        }

        .date-picker-container {
            margin: 20px 0;
            padding: 15px;
            background: rgba(237, 235, 221, 0.1);
            border-radius: 10px;
            border: 1px solid rgba(129, 1, 0, 0.3);
        }

        .date-filter {
            display: flex;
            gap: 15px;
            align-items: center;
            flex-wrap: wrap;
        }

        .date-filter label {
            color: #EDEBDD;
            font-weight: 500;
        }

        .date-filter input {
            background: rgba(237, 235, 221, 0.1);
            border: 2px solid rgba(129, 1, 0, 0.5);
            border-radius: 8px;
            padding: 8px 12px;
            color: #EDEBDD;
            font-family: 'Montserrat', sans-serif;
        }

        .date-filter input:focus {
            outline: none;
            border-color: #810100;
            background: rgba(237, 235, 221, 0.15);
        }

        .filter-btn {
            background: linear-gradient(135deg, #810100 0%, #51080d 100%);
            color: #EDEBDD;
            border: none;
            border-radius: 8px;
            padding: 8px 16px;
            cursor: pointer;
            font-family: 'Montserrat', sans-serif;
            transition: all 0.3s ease;
        }

        .filter-btn:hover {
            background: linear-gradient(135deg, #51080d 0%, #810100 100%);
            transform: translateY(-2px);
        }

        /* Стили для секции обсуждений */
        .discussion-card {
            background: rgba(237, 235, 221, 0.05);
            border: 1px solid rgba(129, 1, 0, 0.3);
            border-radius: 12px;
            padding: 20px;
            margin-bottom: 20px;
            transition: all 0.3s ease;
        }

        .discussion-card:hover {
            border-color: rgba(129, 1, 0, 0.6);
            transform: translateY(-2px);
        }

        .discussion-header {
            display: flex;
            justify-content: space-between;
            align-items: flex-start;
            margin-bottom: 15px;
        }

        .discussion-title {
            color: #EDEBDD;
            font-size: 1.4em;
            margin: 0;
            flex: 1;
        }

        .discussion-actions {
            display: flex;
            gap: 10px;
        }

        .discussion-content {
            color: #EDEBDD;
            line-height: 1.6;
            margin-bottom: 15px;
            white-space: pre-wrap;
        }

        .discussion-stats {
            display: flex;
            gap: 20px;
            color: #B8B4A6;
            font-size: 0.9em;
            margin-bottom: 15px;
        }

        .discussion-stat {
            display: flex;
            align-items: center;
            gap: 5px;
        }

        .like-btn, .comment-btn {
            background: none;
            border: 1px solid rgba(129, 1, 0, 0.5);
            color: #EDEBDD;
            padding: 5px 10px;
            border-radius: 6px;
            cursor: pointer;
            transition: all 0.3s ease;
        }

        .like-btn:hover, .comment-btn:hover {
            background: rgba(129, 1, 0, 0.2);
        }

        .like-btn.liked {
            background: rgba(129, 1, 0, 0.5);
            color: #EDEBDD;
        }

        .comments-section {
            margin-top: 15px;
            border-top: 1px solid rgba(129, 1, 0, 0.3);
            padding-top: 15px;
        }

        .comment-form {
            display: flex;
            gap: 10px;
            margin-bottom: 15px;
        }

        .comment-input {
            flex: 1;
            background: rgba(237, 235, 221, 0.1);
            border: 1px solid rgba(129, 1, 0, 0.5);
            border-radius: 6px;
            padding: 8px 12px;
            color: #EDEBDD;
            font-family: 'Montserrat', sans-serif;
        }

        .comment-list {
            space-y: 10px;
        }

        .comment-item {
            background: rgba(237, 235, 221, 0.05);
            border-radius: 8px;
            padding: 10px;
            margin-bottom: 10px;
        }

        .comment-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 5px;
        }

        .comment-author {
            font-weight: 500;
            color: #EDEBDD;
        }

        .comment-content {
            color: #B8B4A6;
            line-height: 1.4;
        }

        .delete-comment-btn {
            background: none;
            border: none;
            color: #ff6b6b;
            cursor: pointer;
            font-size: 0.8em;
        }

        .discussion-author {
            color: #B8B4A6;
            font-size: 0.9em;
            margin-top: 10px;
        }

        .no-discussions {
            text-align: center;
            padding: 40px;
            color: #B8B4A6;
        }

        .no-discussions i {
            font-size: 3em;
            margin-bottom: 20px;
            color: rgba(129, 1, 0, 0.5);
        }
    </style>
</head>
<body>
<div id="main-page" class="page active">
    <div class="container">
        <div class="user-header">
            <!-- Сообщения об успехе -->
            <c:if test="${not empty success}">
                <div class="success-message">
                    <c:choose>
                        <c:when test="${success == 'meeting_created'}">
                            ✅ Встреча успешно создана!
                        </c:when>
                        <c:when test="${success == 'meeting_updated'}">
                            ✅ Встреча успешно обновлена!
                        </c:when>
                        <c:when test="${success == 'meeting_deleted'}">
                            ✅ Встреча успешно удалена!
                        </c:when>
                        <c:when test="${success == 'registration_updated'}">
                            ✅ Регистрация обновлена!
                        </c:when>
                        <c:otherwise>
                            ✅ Операция выполнена успешно!
                        </c:otherwise>
                    </c:choose>
                </div>
            </c:if>

            <!-- Сообщения об ошибках -->
            <c:if test="${not empty error}">
                <div class="error-message">
                    ❌ ${error}
                </div>
            </c:if>

            <div class="user-info">
                <div class="user-avatar">
                    <i class="fas fa-user"></i>
                </div>
                <div class="user-name">
                    ${user.username}
                </div>
            </div>
            <a href="${pageContext.request.contextPath}/logout" class="logout-btn">Выйти</a>
        </div>

        <div class="main-content">
            <div class="sidebar">
                <h3 class="sidebar-title">Чат комнаты</h3>
                <div class="nav-item active" onclick="showSection('meetings')">
                    <i class="fas fa-users"></i> Встречи
                </div>
                <div class="nav-item" onclick="showSection('discussions')">
                    <i class="fas fa-comments"></i> Обсуждения
                </div>
                <div class="nav-item" onclick="showSection('quotes')">
                    <i class="fas fa-quote-right"></i> Цитаты дня
                </div>
            </div>

            <div class="content">
                <!-- Секция встреч -->
                <div id="meetings-section" class="content-section active">
                    <div class="section-header">
                        <h2 class="section-title">Предстоящие встречи</h2>
                        <a href="${pageContext.request.contextPath}/meeting/create" class="create-meeting-btn">
                            <i class="fas fa-plus"></i> Создать встречу
                        </a>
                    </div>

                    <!-- Фильтр по дате -->
                    <div class="date-picker-container">
                        <div class="date-filter">
                            <label for="dateFilter">Показать встречи начиная с:</label>
                            <input type="datetime-local" id="dateFilter" name="dateFilter">
                            <button class="filter-btn" onclick="filterMeetings()">
                                <i class="fas fa-filter"></i> Применить фильтр
                            </button>
                            <button class="filter-btn" onclick="clearFilter()">
                                <i class="fas fa-times"></i> Сбросить
                            </button>
                        </div>
                    </div>

                    <div id="meetings-container">
                        <c:forEach items="${meetings}" var="meeting">
                            <div class="meeting-card" data-meeting-id="${meeting.id}" data-event-date="${meeting.eventDate}">
                                <div class="meeting-header">
                                    <h3 class="meeting-title">${meeting.title}</h3>
                                    <c:if test="${user.id == meeting.authorId}">
                                        <div class="meeting-actions">
                                            <a href="${pageContext.request.contextPath}/meeting/edit?id=${meeting.id}"
                                               class="edit-btn" title="Редактировать">
                                                <i class="fas fa-edit"></i>
                                            </a>
                                            <button class="delete-btn" onclick="deleteMeeting(${meeting.id})" title="Удалить">
                                                <i class="fas fa-trash"></i>
                                            </button>
                                        </div>
                                    </c:if>
                                </div>

                                <div class="meeting-description">${meeting.description}</div>

                                <!-- Блок регистрации и участников -->
                                <div class="meeting-participation">
                                    <div class="spots-info">
                                        <i class="fas fa-users"></i>
                                        <span>Свободно мест: ${meeting.availableSpots} из ${meeting.maxAttendance}</span>
                                    </div>

                                    <c:if test="${not meeting.full && user.id != meeting.authorId}">
                                        <c:choose>
                                            <c:when test="${meeting.userRegistered}">
                                                <form action="${pageContext.request.contextPath}/meeting/register" method="post" style="display: inline;">
                                                    <input type="hidden" name="postId" value="${meeting.id}">
                                                    <input type="hidden" name="action" value="cancel">
                                                    <button type="submit" class="cancel-registration-btn">
                                                        <i class="fas fa-times"></i> Отменить запись
                                                    </button>
                                                </form>
                                            </c:when>
                                            <c:otherwise>
                                                <form action="${pageContext.request.contextPath}/meeting/register" method="post" style="display: inline;">
                                                    <input type="hidden" name="postId" value="${meeting.id}">
                                                    <input type="hidden" name="action" value="register">
                                                    <button type="submit" class="register-btn">
                                                        <i class="fas fa-check"></i> Записаться
                                                    </button>
                                                </form>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:if>

                                    <c:if test="${meeting.full && not meeting.userRegistered}">
                                        <span class="event-full">❌ Мест нет</span>
                                    </c:if>
                                </div>

                                <!-- Список участников -->
                                <div class="participants-list">
                                    <h4>Участницы (${fn:length(meeting.participants)}):</h4>
                                    <div class="participants">
                                        <c:forEach items="${meeting.participants}" var="participant">
                                            <div class="participant">
                                                <div class="participant-avatar">
                                                    <i class="fas fa-user"></i>
                                                </div>
                                                <span class="participant-name">${participant.user.username}</span>
                                            </div>
                                        </c:forEach>
                                        <c:if test="${empty meeting.participants}">
                                            <p class="no-participants">Пока никто не записался</p>
                                        </c:if>
                                    </div>
                                </div>

                                <div class="meeting-details">
                                    <div class="meeting-detail">
                                        <i class="fas fa-calendar"></i>
                                        <span class="meeting-date">
                                                ${meeting.formattedEventDate}
                                        </span>
                                    </div>
                                    <div class="meeting-detail">
                                        <i class="fas fa-map-marker-alt"></i>
                                        <span>${meeting.location}</span>
                                    </div>
                                    <div class="meeting-detail">
                                        <i class="fas fa-users"></i>
                                        <span>Макс. участников: ${meeting.maxAttendance}</span>
                                    </div>
                                </div>

                                <div class="meeting-author">
                                    <i class="fas fa-user"></i>
                                    Организатор:
                                    <c:choose>
                                        <c:when test="${not empty meeting.author}">
                                            ${meeting.author.username}
                                        </c:when>
                                        <c:otherwise>
                                            Неизвестен
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>
                        </c:forEach>

                        <c:if test="${empty meetings}">
                            <div class="no-meetings">
                                <i class="fas fa-calendar-times"></i>
                                <p>Пока нет предстоящих встреч</p>
                                <a href="${pageContext.request.contextPath}/meeting/create" class="create-first-meeting">
                                    Создайте первую встречу!
                                </a>
                            </div>
                        </c:if>
                    </div>
                </div>

                <!-- Секция обсуждений -->
                <div id="discussions-section" class="content-section">
                    <div class="section-header">
                        <h2 class="section-title">Обсуждения</h2>
                        <a href="${pageContext.request.contextPath}/discussion/create" class="create-meeting-btn">
                            <i class="fas fa-plus"></i> Создать обсуждение
                        </a>
                    </div>
                    <div id="discussions-container">
                        <div class="no-content">
                            <i class="fas fa-comments"></i>
                            <p>Для просмотра обсуждений перейдите в раздел "Обсуждения"</p>
                            <a href="${pageContext.request.contextPath}/discussions" class="create-first-meeting">
                                Перейти к обсуждениям
                            </a>
                        </div>
                    </div>
                </div>

                <!-- Секция цитат -->
                <div id="quotes-section" class="content-section">
                    <div class="quote-display">
                        <div class="main-quote">"Сегодня твой день сиять и быть счастливой!"</div>
                        <div class="quote-author">- Ваше настроение</div>
                        <div class="quote-controls">
                            <button class="next-quote" onclick="nextQuote()">Следующая цитата</button>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <footer>
            <p>© 2025 GirlsClub Kazan. Все права защищены.</p>
        </footer>
    </div>
</div>

<script>
    const contextPath = "${pageContext.request.contextPath}";

    // Функция для удаления встречи
    function deleteMeeting(meetingId) {
        if (confirm('Вы уверены, что хотите удалить эту встречу?')) {
            fetch(contextPath + '/meeting/delete', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: 'id=' + meetingId
            })
                .then(response => {
                    if (response.ok) {
                        window.location.href = contextPath + '/main?success=meeting_deleted';
                    } else {
                        alert('Ошибка при удалении встречи');
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    alert('Ошибка при удалении встречи');
                });
        }
    }

    // Функции для переключения секций
    function showSection(sectionName) {
        // Скрываем все секции
        document.querySelectorAll('.content-section').forEach(section => {
            section.classList.remove('active');
        });

        // Убираем активный класс у всех пунктов меню
        document.querySelectorAll('.nav-item').forEach(item => {
            item.classList.remove('active');
        });

        // Показываем выбранную секцию
        document.getElementById(sectionName + '-section').classList.add('active');

        // Активируем соответствующий пункт меню
        event.currentTarget.classList.add('active');

        // Если выбрана секция обсуждений, перенаправляем на страницу обсуждений
        if (sectionName === 'discussions') {
            window.location.href = contextPath + '/discussions';
        }
    }

    function nextQuote() {
        // Простая реализация смены цитат
        const quotes = [
            {text: "Сила женщины не в том, чтобы быть слабой, а в том, чтобы быть собой!", author: "Неизвестный автор"},
            {text: "Ты уникальна, и в этом твоя сила!", author: "Неизвестный автор"},
            {text: "Сегодня - прекрасный день, чтобы стать лучшей версией себя!", author: "Неизвестный автор"}
        ];

        const randomIndex = Math.floor(Math.random() * quotes.length);
        const quote = quotes[randomIndex];

        document.querySelector('.main-quote').textContent = '"' + quote.text + '"';
        document.querySelector('.quote-author').textContent = '- ' + quote.author;
    }

    // Функции для фильтрации встреч по дате
    function filterMeetings() {
        const dateFilter = document.getElementById('dateFilter').value;
        if (!dateFilter) {
            alert('❌ Пожалуйста, выберите дату для фильтра');
            return;
        }

        const selectedDate = new Date(dateFilter);
        const now = new Date();

        // Проверяем, что выбранная дата в будущем
        if (selectedDate <= now) {
            alert('❌ Выберите дату в будущем для фильтрации');
            return;
        }

        const meetingCards = document.querySelectorAll('.meeting-card');
        let visibleCount = 0;

        meetingCards.forEach(card => {
            const eventDateStr = card.getAttribute('data-event-date');
            // Преобразуем строку даты в Date объект
            const eventDate = new Date(eventDateStr.replace(' ', 'T'));

            if (eventDate >= selectedDate) {
                card.style.display = 'block';
                visibleCount++;
            } else {
                card.style.display = 'none';
            }
        });

        if (visibleCount === 0) {
            alert('🤷‍♀️ Нет встреч после выбранной даты');
        }
    }

    function clearFilter() {
        document.getElementById('dateFilter').value = '';
        const meetingCards = document.querySelectorAll('.meeting-card');
        meetingCards.forEach(card => {
            card.style.display = 'block';
        });
    }

    // Устанавливаем минимальную дату для фильтра как сегодня
    document.addEventListener('DOMContentLoaded', function() {
        const now = new Date();
        const year = now.getFullYear();
        const month = String(now.getMonth() + 1).padStart(2, '0');
        const day = String(now.getDate()).padStart(2, '0');
        const hours = String(now.getHours()).padStart(2, '0');
        const minutes = String(now.getMinutes()).padStart(2, '0');

        const minDateTime = `${year}-${month}-${day}T${hours}:${minutes}`;
        const dateFilter = document.getElementById('dateFilter');
        if (dateFilter) {
            dateFilter.min = minDateTime;
        }
    });
</script>
</body>
</html>