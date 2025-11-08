<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
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
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/discussions.css">
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
                        <c:when test="${success == 'discussion_created'}">
                            ✅ Обсуждение успешно создано!
                        </c:when>
                        <c:when test="${success == 'discussion_updated'}">
                            ✅ Обсуждение успешно обновлено!
                        </c:when>
                        <c:when test="${success == 'discussion_deleted'}">
                            ✅ Обсуждение успешно удалено!
                        </c:when>
                        <c:when test="${success == 'comment_added'}">
                            ✅ Комментарий успешно добавлен!
                        </c:when>
                        <c:when test="${success == 'like_updated'}">
                            ✅ Лайк обновлен!
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
            <c:if test="${not empty error}">
                <div class="error-message">
                     ${error}
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
                                            <form action="${pageContext.request.contextPath}/meeting/delete" method="post" class="delete-form" style="display: inline;">
                                                <input type="hidden" name="id" value="${meeting.id}">
                                                <button type="submit" class="delete-btn" title="Удалить"
                                                        onclick="return confirm('Вы уверены, что хотите удалить эту встречу?')">
                                                    <i class="fas fa-trash"></i>
                                                </button>
                                            </form>
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
                        <c:forEach items="${posts}" var="discussion">
                            <div class="discussion-card" data-discussion-id="${discussion.id}">
                                <div class="discussion-header">
                                    <h3 class="discussion-title">${discussion.title}</h3>
                                    <c:if test="${user.id == discussion.authorId}">
                                        <div class="discussion-actions">
                                            <a href="${pageContext.request.contextPath}/discussion/edit?id=${discussion.id}"
                                               class="edit-btn" title="Редактировать">
                                                <i class="fas fa-edit"></i>
                                            </a>
                                            <form action="${pageContext.request.contextPath}/discussion/delete" method="post" class="delete-form" style="display: inline;">
                                                <input type="hidden" name="id" value="${discussion.id}">
                                                <button type="submit" class="delete-btn" title="Удалить" onclick="return confirm('Вы уверены, что хотите удалить это обсуждение?')">
                                                    <i class="fas fa-trash"></i>
                                                </button>
                                            </form>
                                        </div>
                                    </c:if>
                                </div>

                                <div class="discussion-content">${discussion.content}</div>

                                <div class="discussion-stats">
                                    <div class="discussion-stat">
                                        <form action="${pageContext.request.contextPath}/discussion/like" method="post" class="like-form">
                                            <input type="hidden" name="postId" value="${discussion.id}">
                                            <c:choose>
                                                <c:when test="${discussion.userLiked}">
                                                    <input type="hidden" name="action" value="unlike">
                                                    <button type="submit" class="like-btn liked">
                                                        <i class="fas fa-heart"></i>
                                                        <span>${discussion.likeCount}</span>
                                                    </button>
                                                </c:when>
                                                <c:otherwise>
                                                    <input type="hidden" name="action" value="like">
                                                    <button type="submit" class="like-btn">
                                                        <i class="far fa-heart"></i>
                                                        <span>${discussion.likeCount}</span>
                                                    </button>
                                                </c:otherwise>
                                            </c:choose>
                                        </form>
                                    </div>
                                    <div class="discussion-stat">
                                        <button type="button" class="toggle-comments-btn" onclick="toggleComments(${discussion.id})">
                                            <i class="fas fa-comment"></i>
                                            <span>${discussion.commentCount} комментариев</span>
                                            <i class="fas fa-chevron-down" id="comment-icon-${discussion.id}"></i>
                                        </button>
                                    </div>
                                </div>

                                <div class="discussion-author">
                                    <i class="fas fa-user"></i>
                                    Автор:
                                    <c:choose>
                                        <c:when test="${not empty discussion.author}">
                                            ${discussion.author.username}
                                        </c:when>
                                        <c:otherwise>
                                            Неизвестен
                                        </c:otherwise>
                                    </c:choose>
                                    | ${discussion.formattedCreatedAt}
                                </div>

                                <!-- Секция комментариев (изначально скрыта) -->
                                <div class="comments-section" id="comments-${discussion.id}" style="display: none;">
                                    <form action="${pageContext.request.contextPath}/discussion/comment" method="post" class="comment-form-container">
                                        <input type="hidden" name="postId" value="${discussion.id}">
                                        <input type="hidden" name="action" value="add">
                                        <input type="text" name="content" class="comment-input" placeholder="Напишите комментарий..." required>
                                        <button type="submit" class="comment-btn">
                                            <i class="fas fa-paper-plane"></i> Отправить
                                        </button>
                                    </form>

                                    <div class="comment-list">
                                        <c:forEach items="${discussion.comments}" var="comment">
                                            <div class="comment-item">
                                                <div class="comment-header">
                                                    <span class="comment-author">${comment.user.username}</span>
                                                    <c:if test="${user.id == comment.userId || user.id == discussion.authorId}">
                                                        <form action="${pageContext.request.contextPath}/discussion/comment" method="post" style="display: inline;">
                                                            <input type="hidden" name="postId" value="${discussion.id}">
                                                            <input type="hidden" name="commentId" value="${comment.id}">
                                                            <input type="hidden" name="action" value="delete">
                                                            <button type="submit" class="delete-comment-btn" onclick="return confirm('Удалить комментарий?')">
                                                                <i class="fas fa-trash"></i>
                                                            </button>
                                                        </form>
                                                    </c:if>
                                                </div>
                                                <div class="comment-content">${comment.content}</div>
                                                <div class="comment-date">
                                                        ${comment.createdAt}
                                                </div>
                                            </div>
                                        </c:forEach>
                                        <c:if test="${empty discussion.comments}">
                                            <div class="no-comments">
                                                <i class="fas fa-comment-slash"></i>
                                                <span>Пока нет комментариев</span>
                                            </div>
                                        </c:if>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>

                        <c:if test="${empty posts}">
                            <div class="no-discussions">
                                <i class="fas fa-comments"></i>
                                <p>Пока нет обсуждений</p>
                                <a href="${pageContext.request.contextPath}/discussion/create" class="create-first-discussion">
                                    Создайте первое обсуждение!
                                </a>
                            </div>
                        </c:if>
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

<script src="${pageContext.request.contextPath}/js/main.js"></script>
</body>
</html>