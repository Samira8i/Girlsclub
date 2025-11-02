
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>GirlsClub Kazan - Редактировать обсуждение</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link href="https://fonts.googleapis.com/css2?family=Playfair+Display:wght@400;700&family=Montserrat:wght@300;400;500&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
    <style>
        .form-container {
            max-width: 800px;
            margin: 0 auto;
            padding: 30px;
        }

        .form-title {
            color: #EDEBDD;
            font-family: 'Playfair Display', serif;
            font-size: 2.5em;
            margin-bottom: 30px;
            text-align: center;
        }

        .discussion-form {
            background: rgba(237, 235, 221, 0.05);
            border: 1px solid rgba(129, 1, 0, 0.3);
            border-radius: 12px;
            padding: 30px;
        }

        .form-group {
            margin-bottom: 25px;
        }

        .form-label {
            display: block;
            color: #EDEBDD;
            font-weight: 500;
            margin-bottom: 8px;
            font-size: 1.1em;
        }

        .form-input, .form-textarea {
            width: 100%;
            background: rgba(237, 235, 221, 0.1);
            border: 2px solid rgba(129, 1, 0, 0.5);
            border-radius: 8px;
            padding: 12px 16px;
            color: #EDEBDD;
            font-family: 'Montserrat', sans-serif;
            font-size: 1em;
            transition: all 0.3s ease;
        }

        .form-input:focus, .form-textarea:focus {
            outline: none;
            border-color: #810100;
            background: rgba(237, 235, 221, 0.15);
        }

        .form-textarea {
            min-height: 200px;
            resize: vertical;
        }

        .form-actions {
            display: flex;
            gap: 15px;
            justify-content: space-between;
            margin-top: 30px;
        }

        .submit-btn {
            background: linear-gradient(135deg, #810100 0%, #51080d 100%);
            color: #EDEBDD;
            border: none;
            border-radius: 8px;
            padding: 12px 30px;
            font-family: 'Montserrat', sans-serif;
            font-weight: 500;
            font-size: 1.1em;
            cursor: pointer;
            transition: all 0.3s ease;
        }

        .submit-btn:hover {
            background: linear-gradient(135deg, #51080d 0%, #810100 100%);
            transform: translateY(-2px);
        }

        .cancel-btn {
            background: rgba(237, 235, 221, 0.1);
            color: #EDEBDD;
            border: 2px solid rgba(129, 1, 0, 0.5);
            border-radius: 8px;
            padding: 12px 30px;
            font-family: 'Montserrat', sans-serif;
            font-weight: 500;
            font-size: 1.1em;
            cursor: pointer;
            text-decoration: none;
            transition: all 0.3s ease;
        }

        .cancel-btn:hover {
            background: rgba(129, 1, 0, 0.2);
            transform: translateY(-2px);
        }

        .delete-btn {
            background: rgba(255, 107, 107, 0.1);
            color: #ff6b6b;
            border: 2px solid rgba(255, 107, 107, 0.5);
            border-radius: 8px;
            padding: 12px 30px;
            font-family: 'Montserrat', sans-serif;
            font-weight: 500;
            font-size: 1.1em;
            cursor: pointer;
            transition: all 0.3s ease;
        }

        .delete-btn:hover {
            background: rgba(255, 107, 107, 0.2);
            transform: translateY(-2px);
        }

        .error-message {
            background: rgba(255, 107, 107, 0.1);
            border: 1px solid rgba(255, 107, 107, 0.5);
            border-radius: 8px;
            padding: 15px;
            margin-bottom: 20px;
            color: #ff6b6b;
        }

        .character-count {
            text-align: right;
            color: #B8B4A6;
            font-size: 0.9em;
            margin-top: 5px;
        }

        .form-hint {
            color: #B8B4A6;
            font-size: 0.9em;
            margin-top: 5px;
            font-style: italic;
        }

        .discussion-info {
            background: rgba(129, 1, 0, 0.1);
            border: 1px solid rgba(129, 1, 0, 0.3);
            border-radius: 8px;
            padding: 15px;
            margin-bottom: 20px;
        }

        .info-item {
            display: flex;
            justify-content: space-between;
            margin-bottom: 5px;
            color: #B8B4A6;
        }

        .info-label {
            font-weight: 500;
        }

        .info-value {
            color: #EDEBDD;
        }
    </style>
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
        <a href="${pageContext.request.contextPath}/discussions" class="logout-btn">Назад к обсуждениям</a>
    </div>

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
                <form action="${pageContext.request.contextPath}/discussion/delete" method="post" style="display: inline;">
                    <input type="hidden" name="id" value="${discussion.id}">
                    <button type="submit" class="delete-btn" onclick="return confirm('Вы уверены, что хотите удалить это обсуждение? Это действие нельзя отменить.')">
                        <i class="fas fa-trash"></i> Удалить
                    </button>
                </form>
                <div>
                    <a href="${pageContext.request.contextPath}/discussions" class="cancel-btn">
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

<script>
    // Подсчет символов для заголовка
    const titleInput = document.getElementById('title');
    const titleCount = document.getElementById('title-count');

    titleInput.addEventListener('input', function() {
        titleCount.textContent = this.value.length;
    });

    // Подсчет символов для содержания
    const contentInput = document.getElementById('content');
    const contentCount = document.getElementById('content-count');

    contentInput.addEventListener('input', function() {
        contentCount.textContent = this.value.length;
    });

    // Валидация формы
    document.querySelector('.discussion-form').addEventListener('submit', function(e) {
        const title = titleInput.value.trim();
        const content = contentInput.value.trim();

        if (!title) {
            e.preventDefault();
            alert('❌ Пожалуйста, введите заголовок обсуждения');
            titleInput.focus();
            return;
        }

        if (!content) {
            e.preventDefault();
            alert('❌ Пожалуйста, введите содержание обсуждения');
            contentInput.focus();
            return;
        }

        if (title.length > 200) {
            e.preventDefault();
            alert('❌ Заголовок не может превышать 200 символов');
            titleInput.focus();
            return;
        }

        if (content.length > 2000) {
            e.preventDefault();
            alert('❌ Содержание не может превышать 2000 символов');
            contentInput.focus();
            return;
        }
    });
</script>
</body>
</html>
