// src/main/webapp/js/create-meeting.js

document.addEventListener('DOMContentLoaded', function() {
    initializeDateTime();
    setupFormValidation();
});

// Инициализация даты и времени
function initializeDateTime() {
    const now = new Date();
    const dateInput = document.getElementById('eventDate');

    if (dateInput) {
        // Устанавливаем минимальную дату как сейчас
        const minDateTime = formatDateTime(now);
        dateInput.min = minDateTime;

        // Устанавливаем значение по умолчанию - через 1 час
        const defaultDate = new Date(now.getTime() + 60 * 60 * 1000);
        dateInput.value = formatDateTime(defaultDate);

        // Валидация при изменении
        dateInput.addEventListener('change', validateDate);
    }
}

// Форматирование даты для input[type="datetime-local"]
function formatDateTime(date) {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');

    return `${year}-${month}-${day}T${hours}:${minutes}`;
}

// Настройка валидации формы
function setupFormValidation() {
    const form = document.getElementById('meetingForm');
    if (!form) return;

    form.addEventListener('submit', function(e) {
        if (!validateForm()) {
            e.preventDefault();
            return false;
        }
    });
}

// Валидация всей формы
function validateForm() {
    const title = document.querySelector('input[name="title"]')?.value.trim();
    const description = document.querySelector('textarea[name="description"]')?.value.trim();
    const eventDate = document.getElementById('eventDate')?.value;
    const maxAttendance = document.querySelector('input[name="maxAttendance"]')?.value;
    const location = document.querySelector('input[name="location"]')?.value.trim();

    // Проверка обязательных полей
    if (!title || !description || !eventDate || !maxAttendance || !location) {
        alert('❌ Пожалуйста, заполните все обязательные поля');
        return false;
    }

    // Проверка даты
    if (!validateDate()) {
        alert('❌ Дата встречи должна быть в будущем');
        return false;
    }

    // Проверка количества участников
    const attendance = parseInt(maxAttendance);
    if (attendance < 2 || attendance > 50) {
        alert('❌ Количество участников должно быть от 2 до 50');
        return false;
    }

    return true;
}

// Валидация даты
function validateDate() {
    const dateInput = document.getElementById('eventDate');
    const validationMessage = document.getElementById('dateValidationMessage');

    if (!dateInput || !dateInput.value) return true;

    const selectedDate = new Date(dateInput.value);
    const now = new Date();

    if (selectedDate <= now) {
        if (validationMessage) {
            validationMessage.style.display = 'block';
        }
        if (dateInput) {
            dateInput.style.borderColor = '#dc3545';
        }
        return false;
    } else {
        if (validationMessage) {
            validationMessage.style.display = 'none';
        }
        if (dateInput) {
            dateInput.style.borderColor = 'rgba(129, 1, 0, 0.5)';
        }
        return true;
    }
}