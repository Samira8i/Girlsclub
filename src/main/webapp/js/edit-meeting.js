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
    }
}

// Форматирование даты для input
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
    const form = document.querySelector('.meeting-form');
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
    const title = document.getElementById('title')?.value.trim();
    const description = document.getElementById('description')?.value.trim();
    const eventDate = document.getElementById('eventDate')?.value;
    const maxAttendance = document.getElementById('maxAttendance')?.value;
    const location = document.getElementById('location')?.value.trim();

    // Проверка обязательных полей
    if (!title || !description || !eventDate || !maxAttendance || !location) {
        alert('Пожалуйста, заполните все обязательные поля');
        return false;
    }

    // Проверка даты
    const selectedDate = new Date(eventDate);
    const now = new Date();
    if (selectedDate <= now) {
        alert('Дата встречи должна быть в будущем');
        return false;
    }

    // Проверка количества участников
    const attendance = parseInt(maxAttendance);
    if (attendance < 2 || attendance > 100) {
        alert('Количество участников должно быть от 2 до 100');
        return false;
    }

    return true;
}