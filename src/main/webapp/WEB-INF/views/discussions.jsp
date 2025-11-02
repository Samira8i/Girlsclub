
<%-- src/main/webapp/WEB-INF/views/discussions.jsp --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>GirlsClub Kazan - Обсуждения</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link href="https://fonts.googleapis.com/css2?family=Playfair+Display:wght@400;700&family=Montserrat:wght@300;400;500&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
    <style>
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

        .like-form, .comment-form {
            display: inline;
        }

        .like-btn, .comment-btn {
            background: none;
            border: 1px solid rgba(129, 1, 0, 0.5);
            color: #EDEBDD;
            padding: 5px 10px;
            border-radius: 6px;
            cursor: pointer;
            transition: all 0.3s ease;
            font-family: 'Montserrat', sans-serif;
            font-size: 0.9em;
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

        .comment-form-container {
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

        .create-first-discussion {
            display: inline-block;
            margin-top: 15px;
            padding: 10px 20px;
            background: linear-gradient(135deg, #810100 0%, #51080d 100%);
            color: #EDEBDD;
            text-decoration: none;
            border-radius: 8px;
            transition: all 0.3s ease;
        }

        .create-first-discussion:hover {
            background: linear-gradient(135deg, #51080d 0%, #810100 100%);
            transform: translateY(-2px);
        }
    </style>
</head>
<body>
<div class="container">
    <div class="user-header">
        <!-- Сообщения об успехе -->
        <c:if test="${not empty success}">
            <div class="success-message">
                <c:choose>
                    <c:when test="${success == 'discussion_created'}">
                        ✅ Обсуждение успешно создано!
                    </c:when>
                    <c:when test="${success == 'discussion_updated'}">
                        ✅ Обсуждение успешно обновлено!
                    </c:when>
                    <c:when test="${success == 'discussion_deleted'}">
                        ✅ Обсуждение успешно удалено!
                    </c:when>
                    <c:when test="${success == 'comment_updated'}">
                        ✅ Комментарий успешно добавлен!
                    </c:when>
                    <c:when test="${success == 'like_updated'}">
                        ✅ Лайк обновлен!
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
        <a href="${pageContext.request.contextPath}/main" class="logout-btn">На главную</a>
    </div>

    <div class="section-header">
        <h2 class="section-title">Обсуждения</h2>
        <a href="${pageContext.request.contextPath}/discussion/create" class="create-meeting-btn">
            <i class="fas fa-plus"></i> Создать обсуждение
        </a>
    </div>

    <div id="discussions-list">
        <c:forEach items="${posts}" var="discussion">
            <div class="discussion-card">
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
                        <i class="fas fa-comment"></i>
                        <span>${discussion.commentCount} комментариев</span>
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
                    | ${discussion.createdAt}
                </div>

                <!-- Секция комментариев -->
                <div class="comments-section">
                    <form action="${pageContext.request.contextPath}/discussion/comment" method="post" class="comment-form-container">
                        <input type="hidden" name="postId" value="${discussion.id}">
                        <input type="hidden" name="action" value="add">
                        <input type="text" name="content" class="comment-input" placeholder="Напишите комментарий..." required>
                        <button type="submit" class="comment-btn">
                            <i class="fas fa-paper-plane"></i> Отправить
                        </button>
                    </form>

                    <!-- Временное сообщение, пока комментарии не загружаются -->
                    <div class="comment-list">
                        <c:if test="${empty discussion.comments}">
                            <div style="color: #B8B4A6; text-align: center; padding: 10px;">
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
</body>
</html>
