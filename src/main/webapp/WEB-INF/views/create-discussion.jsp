<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="my" tagdir="/WEB-INF/tags" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>GirlsClub Kazan - Создать обсуждение</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link href="https://fonts.googleapis.com/css2?family=Playfair+Display:wght@400;700&family=Montserrat:wght@300;400;500&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/create-discussion.css">
    <script src="${pageContext.request.contextPath}/js/create-discussion.js"></script>
</head>
<body>
<div class="container">
    <my:userHeader
            username="${user.username}"
            backUrl="${pageContext.request.contextPath}/main"
            backText="Назад к обсуждениям"
    />

    <div class="form-container">
        <h1 class="form-title">Создать новое обсуждение</h1>

        <!-- Сообщения об ошибках -->
        <c:if test="${not empty error}">
            <div class="error-message">
                ❌ ${error}
            </div>
        </c:if>

        <form action="${pageContext.request.contextPath}/discussion/create" method="post" class="discussion-form">
            <div class="form-group">
                <label for="title" class="form-label">
                    <i class="fas fa-heading"></i> Заголовок обсуждения
                </label>
                <input type="text"
                       id="title"
                       name="title"
                       class="form-input"
                       placeholder="Введите заголовок обсуждения..."
                       maxlength="200"
                       value="${param.title}"
                       required>
                <div class="character-count">
                    <span id="title-count">0</span>/200 символов
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
                          required>${param.content}</textarea>
                <div class="character-count">
                    <span id="content-count">0</span>/2000 символов
                </div>
            </div>

            <div class="form-actions">
                <a href="${pageContext.request.contextPath}/discussions" class="cancel-btn">
                    <i class="fas fa-times"></i> Отмена
                </a>
                <button type="submit" class="submit-btn">
                    <i class="fas fa-plus"></i> Создать обсуждение
                </button>
            </div>
        </form>
    </div>
</div>
</body>
</html>