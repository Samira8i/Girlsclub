<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="my" tagdir="/WEB-INF/tags" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>GirlsClub Kazan - Редактировать обсуждение</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link href="https://fonts.googleapis.com/css2?family=Playfair+Display:wght@400;700&family=Montserrat:wght@300;400;500&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/edit-discussion.css">
    <script src="${pageContext.request.contextPath}/js/edit-discussion.js.js"></script>
</head>
<body>
<div class="container">
    <my:userHeader
            username="${user.username}"
            backUrl="${pageContext.request.contextPath}/main"
            backText="Назад к обсуждениям"
    />

    <div class="form-container">
        <h1 class="form-title">Редактировать обсуждение</h1>

        <!-- Сообщения об ошибках -->
        <c:if test="${not empty error}">
            <div class="error-message">
                ❌ ${error}
            </div>
        </c:if>

        <!-- Информация об обсуждении -->
        <div class="discussion-info">
            <div class="info-item">
                <span class="info-label">Автор:</span>
                <span class="info-value">${user.username}</span>
            </div>
            <c:if test="${not empty discussion.createdAt}">
                <div class="info-item">
                    <span class="info-label">Создано:</span>
                    <span class="info-value">${discussion.createdAt}</span>
                </div>
            </c:if>
        </div>

        <form action="${pageContext.request.contextPath}/discussion/edit" method="post" class="discussion-form">
            <input type="hidden" name="id" value="${discussion.id}">

            <div class="form-group">
                <label for="title" class="form-label">
                    <i class="fas fa-heading"></i> Заголовок обсуждения
                </label>
                <input type="text"
                       id="title"
                       name="title"
                       class="form-input"
                       placeholder="Введите заголовок обсуждения..."
                       value="${discussion.title}"
                       maxlength="200"
                       required>
                <div class="character-count">
                    <span id="title-count">${fn:length(discussion.title)}</span>/200 символов
                </div>
            </div>

            <div class="form-group">
                <label for="content" class="form-label">
                    <i class="fas fa-align-left"></i> Содержание обсуждения
                </label>
                <textarea id="content"
                          name="content"
                          class="form-textarea"
                          placeholder="Опишите тему для обсуждения..."
                          maxlength="2000"
                          required>${discussion.content}</textarea>
                <div class="character-count">
                    <span id="content-count">${fn:length(discussion.content)}</span>/2000 символов
                </div>
                <div class="form-hint">
                    💡 Вы можете использовать Markdown для форматирования текста
                </div>
            </div>

            <div class="form-actions">
                <form action="${pageContext.request.contextPath}/discussion/delete" method="post" class="delete-form">
                    <input type="hidden" name="id" value="${discussion.id}">
                    <button type="submit" class="delete-btn" onclick="return confirm('Вы уверены, что хотите удалить это обсуждение? Это действие нельзя отменить.')">
                        <i class="fas fa-trash"></i> Удалить
                    </button>
                </form>
                <div>
                    <a href="${pageContext.request.contextPath}/main?section=discussions" class="cancel-btn">
                        <i class="fas fa-times"></i> Отмена
                    </a>
                    <button type="submit" class="submit-btn">
                        <i class="fas fa-save"></i> Сохранить изменения
                    </button>
                </div>
            </div>
        </form>
    </div>
</div>
</body>
</html>