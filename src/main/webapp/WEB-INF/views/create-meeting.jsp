<%-- src/main/webapp/WEB-INF/views/create-meeting.jsp --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>GirlsClub Kazan - Создать встречу</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link href="https://fonts.googleapis.com/css2?family=Playfair+Display:wght@400;700&family=Montserrat:wght@300;400;500&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
    <style>
        /* Красивые стили для формы создания встречи */
        .create-meeting-container {
            max-width: 700px;
            margin: 0 auto;
            padding: 40px;
            background: linear-gradient(135deg, rgba(24, 23, 23, 0.9) 0%, rgba(43, 3, 7, 0.9) 100%);
            border-radius: 20px;
            box-shadow: 0 15px 35px rgba(0, 0, 0, 0.5);
            border: 1px solid rgba(129, 1, 0, 0.3);
            backdrop-filter: blur(10px);
        }

        .form-title {
            font-family: 'Playfair Display', serif;
            font-size: 2.5rem;
            text-align: center;
            margin-bottom: 40px;
            color: #EDEBDD;
            text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.5);
        }

        .form-group {
            margin-bottom: 2rem;
            position: relative;
        }

        .form-label {
            display: block;
            margin-bottom: 0.8rem;
            font-family: 'Montserrat', sans-serif;
            font-weight: 500;
            color: #EDEBDD;
            font-size: 1.1rem;
            text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.3);
        }

        .form-input, .form-textarea {
            width: 100%;
            padding: 15px 20px;
            background: rgba(237, 235, 221, 0.1);
            border: 2px solid rgba(129, 1, 0, 0.5);
            border-radius: 12px;
            font-size: 1rem;
            color: #EDEBDD;
            font-family: 'Montserrat', sans-serif;
            transition: all 0.3s ease;
            backdrop-filter: blur(5px);
        }

        .form-input::placeholder, .form-textarea::placeholder {
            color: rgba(237, 235, 221, 0.6);
        }

        .form-input:focus, .form-textarea:focus {
            outline: none;
            border-color: #810100;
            background: rgba(237, 235, 221, 0.15);
            box-shadow: 0 0 20px rgba(129, 1, 0, 0.3);
            transform: translateY(-2px);
        }

        .form-textarea {
            height: 140px;
            resize: vertical;
            line-height: 1.6;
        }

        .form-submit {
            width: 100%;
            padding: 18px;
            background: linear-gradient(135deg, #810100 0%, #51080d 100%);
            color: #EDEBDD;
            border: none;
            border-radius: 12px;
            font-size: 1.2rem;
            font-family: 'Montserrat', sans-serif;
            font-weight: 500;
            cursor: pointer;
            transition: all 0.3s ease;
            text-transform: uppercase;
            letter-spacing: 1px;
            margin-top: 20px;
            box-shadow: 0 5px 15px rgba(129, 1, 0, 0.4);
        }

        .form-submit:hover {
            background: linear-gradient(135deg, #51080d 0%, #810100 100%);
            transform: translateY(-3px);
            box-shadow: 0 8px 25px rgba(129, 1, 0, 0.6);
        }

        .form-submit:active {
            transform: translateY(-1px);
        }

        .back-link {
            display: inline-flex;
            align-items: center;
            margin-top: 30px;
            color: #EDEBDD;
            text-decoration: none;
            font-family: 'Montserrat', sans-serif;
            font-size: 1rem;
            transition: all 0.3s ease;
            padding: 10px 20px;
            background: rgba(129, 1, 0, 0.2);
            border-radius: 8px;
            border: 1px solid rgba(129, 1, 0, 0.5);
        }

        .back-link:hover {
            background: rgba(129, 1, 0, 0.4);
            transform: translateX(-5px);
            text-decoration: none;
        }

        .back-link i {
            margin-right: 8px;
        }

        .error-message {
            background: linear-gradient(135deg, rgba(220, 53, 69, 0.9) 0%, rgba(200, 35, 51, 0.9) 100%);
            color: #EDEBDD;
            padding: 20px;
            border-radius: 12px;
            margin-bottom: 30px;
            border-left: 5px solid #dc3545;
            font-family: 'Montserrat', sans-serif;
            font-size: 1rem;
            box-shadow: 0 5px 15px rgba(220, 53, 69, 0.3);
        }

        .success-message {
            background: linear-gradient(135deg, rgba(40, 167, 69, 0.9) 0%, rgba(33, 136, 56, 0.9) 100%);
            color: #EDEBDD;
            padding: 20px;
            border-radius: 12px;
            margin-bottom: 30px;
            border-left: 5px solid #28a745;
            font-family: 'Montserrat', sans-serif;
            font-size: 1rem;
            box-shadow: 0 5px 15px rgba(40, 167, 69, 0.3);
        }

        .input-icon {
            position: absolute;
            right: 20px;
            top: 50px;
            color: #810100;
            font-size: 1.1rem;
        }

        .form-hint {
            font-size: 0.9rem;
            color: rgba(237, 235, 221, 0.7);
            margin-top: 5px;
            font-style: italic;
        }

        .date-validation-message {
            color: #ff6b6b;
            font-size: 0.9rem;
            margin-top: 5px;
            font-style: italic;
            display: none;
        }

        /* Анимации */
        @keyframes fadeInUp {
            from {
                opacity: 0;
                transform: translateY(30px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }

        .create-meeting-container {
            animation: fadeInUp 0.6s ease-out;
        }

        .form-group {
            animation: fadeInUp 0.6s ease-out;
            animation-fill-mode: both;
        }

        .form-group:nth-child(1) { animation-delay: 0.1s; }
        .form-group:nth-child(2) { animation-delay: 0.2s; }
        .form-group:nth-child(3) { animation-delay: 0.3s; }
        .form-group:nth-child(4) { animation-delay: 0.4s; }
        .form-group:nth-child(5) { animation-delay: 0.5s; }

        /* Адаптивность */
        @media (max-width: 768px) {
            .create-meeting-container {
                margin: 20px;
                padding: 30px 20px;
            }

            .form-title {
                font-size: 2rem;
            }

            .form-input, .form-textarea {
                padding: 12px 15px;
            }
        }
    </style>
</head>
<body>
<div id="create-meeting-page" class="page active">
    <div class="container">
        <header>
            <div class="logo">GirlsClub Kazan</div>
            <div class="tagline">Создание новой встречи</div>
        </header>

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

                <a href="${pageContext.request.contextPath}/main" class="back-link">
                    <i class="fas fa-arrow-left"></i>
                    Вернуться на главную
                </a>
            </div>
        </div>

        <footer>
            <p>© 2025 GirlsClub Kazan. Все права защищены.</p>
        </footer>
    </div>
</div>

<script>
    // Автоматически устанавливаем минимальную дату как сегодня
    document.addEventListener('DOMContentLoaded', function() {
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

        // Устанавливаем значение по умолчанию - через 1 час от текущего времени
        const defaultDate = new Date(now.getTime() + 60 * 60 * 1000); // +1 час
        const defaultYear = defaultDate.getFullYear();
        const defaultMonth = String(defaultDate.getMonth() + 1).padStart(2, '0');
        const defaultDay = String(defaultDate.getDate()).padStart(2, '0');
        const defaultHours = String(defaultDate.getHours()).padStart(2, '0');
        const defaultMinutes = String(defaultDate.getMinutes()).padStart(2, '0');

        const defaultDateTime = `${defaultYear}-${defaultMonth}-${defaultDay}T${defaultHours}:${defaultMinutes}`;
        dateInput.value = defaultDateTime;
    });

    // Валидация даты при изменении
    document.getElementById('eventDate').addEventListener('change', function() {
        validateDate();
    });

    // Валидация даты при отправке формы
    document.getElementById('meetingForm').addEventListener('submit', function(e) {
        if (!validateDate()) {
            e.preventDefault();
            alert('❌ Дата встречи должна быть в будущем');
            return false;
        }
    });

    // Функция валидации даты
    function validateDate() {
        const dateInput = document.getElementById('eventDate');
        const validationMessage = document.getElementById('dateValidationMessage');
        const selectedDate = new Date(dateInput.value);
        const now = new Date();

        if (selectedDate <= now) {
            validationMessage.style.display = 'block';
            dateInput.style.borderColor = '#dc3545';
            return false;
        } else {
            validationMessage.style.display = 'none';
            dateInput.style.borderColor = 'rgba(129, 1, 0, 0.5)';
            return true;
        }
    }

    // Автоматическая валидация при загрузке страницы
    document.addEventListener('DOMContentLoaded', function() {
        validateDate();
    });
</script>
</body>
</html>