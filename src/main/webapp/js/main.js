// Показ/скрытие секций
function showSection(sectionName) {
    // Скрыть все секции
    document.querySelectorAll('.content-section').forEach(section => {
        section.classList.remove('active');
    });

    // Показать выбранную секцию
    document.getElementById(sectionName + '-section').classList.add('active');

    // Обновить активный пункт меню
    document.querySelectorAll('.nav-item').forEach(item => {
        item.classList.remove('active');
    });

    // Найти и активировать соответствующий пункт меню
    const menuItems = document.querySelectorAll('.nav-item');
    for (let item of menuItems) {
        if (item.textContent.includes(getSectionDisplayName(sectionName))) {
            item.classList.add('active');
            break;
        }
    }
}

// Вспомогательная функция для получения отображаемого имени секции
function getSectionDisplayName(sectionName) {
    const names = {
        'meetings': 'Встречи',
        'discussions': 'Обсуждения',
        'quotes': 'Цитаты дня'
    };
    return names[sectionName] || sectionName;
}

// Переключение отображения комментариев
function toggleComments(discussionId) {
    const commentsSection = document.getElementById(`comments-${discussionId}`);
    const commentIcon = document.getElementById(`comment-icon-${discussionId}`);

    if (commentsSection.style.display === 'none') {
        commentsSection.style.display = 'block';
        commentIcon.className = 'fas fa-chevron-up';
    } else {
        commentsSection.style.display = 'none';
        commentIcon.className = 'fas fa-chevron-down';
    }
}

// Фильтрация встреч по дате
function filterMeetings() {
    const dateFilter = document.getElementById('dateFilter');
    const filterDate = new Date(dateFilter.value);

    if (!dateFilter.value) {
        alert('Пожалуйста, выберите дату для фильтрации');
        return;
    }

    const meetingCards = document.querySelectorAll('.meeting-card');
    let visibleCount = 0;

    meetingCards.forEach(card => {
        const eventDateStr = card.getAttribute('data-event-date');
        if (eventDateStr) {
            const eventDate = new Date(eventDateStr);

            if (eventDate >= filterDate) {
                card.style.display = 'block';
                visibleCount++;
            } else {
                card.style.display = 'none';
            }
        }
    });

    // Показать сообщение, если нет подходящих встреч
    const noMeetingsElement = document.querySelector('.no-meetings');
    if (visibleCount === 0) {
        if (!noMeetingsElement) {
            const container = document.getElementById('meetings-container');
            const message = document.createElement('div');
            message.className = 'no-meetings';
            message.innerHTML = `
                <i class="fas fa-calendar-times"></i>
                <p>Нет встреч на выбранную дату или позже</p>
                <button class="create-first-meeting" onclick="clearFilter()">Показать все встречи</button>
            `;
            container.appendChild(message);
        }
    } else if (noMeetingsElement && !noMeetingsElement.classList.contains('permanent')) {
        noMeetingsElement.remove();
    }
}

// Сброс фильтра
function clearFilter() {
    document.getElementById('dateFilter').value = '';

    const meetingCards = document.querySelectorAll('.meeting-card');
    meetingCards.forEach(card => {
        card.style.display = 'block';
    });

    // Удалить временное сообщение "нет встреч"
    const tempNoMeetings = document.querySelector('.no-meetings:not(.permanent)');
    if (tempNoMeetings) {
        tempNoMeetings.remove();
    }
}


// Система цитат
const quotes = [
    {
        text: "Сила женщины не в том, чтобы доказывать свою силу, а в том, чтобы быть собой.",
        author: "Неизвестная мудрость"
    },
    {
        text: "Ты - автор своей жизни. Не позволяй никому держать ручку за тебя.",
        author: "Неизвестный автор"
    },
    {
        text: "Самая красивая женщина - это та, которая уверена в себе.",
        author: "Коко Шанель"
    },
    {
        text: "Не бойся мечтать о большем и делать все возможное для этого.",
        author: "Опра Уинфри"
    },
    {
        text: "Сегодня твой день сиять и быть счастливой!",
        author: "Ваше настроение"
    },
    {
        text: "Женская поддержка - это суперсила, которая меняет мир.",
        author: "GirlsClub Kazan"
    }
];

let currentQuoteIndex = 0;

function nextQuote() {
    currentQuoteIndex = (currentQuoteIndex + 1) % quotes.length;
    displayQuote(currentQuoteIndex);
}

function displayQuote(index) {
    const quote = quotes[index];
    const quoteDisplay = document.querySelector('.quote-display');

    if (quoteDisplay) {
        quoteDisplay.style.opacity = '0';

        setTimeout(() => {
            const mainQuote = quoteDisplay.querySelector('.main-quote');
            const quoteAuthor = quoteDisplay.querySelector('.quote-author');

            mainQuote.textContent = `"${quote.text}"`;
            quoteAuthor.textContent = `- ${quote.author}`;

            quoteDisplay.style.opacity = '1';
        }, 300);
    }
}

// Автоматическая смена цитат каждые 10 секунд
function startQuoteRotation() {
    const quotesSection = document.getElementById('quotes-section');
    if (quotesSection && quotesSection.classList.contains('active')) {
        setInterval(() => {
            nextQuote();
        }, 10000);
    }
}

document.addEventListener('DOMContentLoaded', function() {
    // Инициализация системы цитат
    if (document.getElementById('quotes-section')) {
        displayQuote(currentQuoteIndex);
        startQuoteRotation();
    }

    // Предотвращение двойного отправления форм
    const likeForms = document.querySelectorAll('.like-form');
    likeForms.forEach(form => {
        form.addEventListener('submit', function(e) {
            const button = this.querySelector('button[type="submit"]');
            if (button.disabled) {
                e.preventDefault();
                return;
            }

            button.disabled = true;
            button.style.opacity = '0.6';

            // Восстановление кнопки через 2 секунды на случай ошибки
            setTimeout(() => {
                button.disabled = false;
                button.style.opacity = '1';
            }, 2000);
        });
    });

    // Обработка форм комментариев
    const commentForms = document.querySelectorAll('.comment-form-container');
    commentForms.forEach(form => {
        form.addEventListener('submit', function(e) {
            const input = this.querySelector('.comment-input');
            const button = this.querySelector('button[type="submit"]');

            if (input.value.trim() === '') {
                e.preventDefault();
                input.focus();
                return;
            }

            button.disabled = true;
            button.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Отправка...';
        });
    });

    // Автоматическое скрытие сообщений через 5 секунд
    setTimeout(() => {
        const successMessages = document.querySelectorAll('.success-message');
        const errorMessages = document.querySelectorAll('.error-message');

        successMessages.forEach(msg => {
            msg.style.transition = 'opacity 0.5s ease';
            msg.style.opacity = '0';
            setTimeout(() => msg.remove(), 500);
        });

        errorMessages.forEach(msg => {
            msg.style.transition = 'opacity 0.5s ease';
            msg.style.opacity = '0';
            setTimeout(() => msg.remove(), 500);
        });
    }, 5000);
});

// Валидация даты при создании/редактировании встреч
function validateMeetingDate() {
    const dateInput = document.querySelector('input[type="datetime-local"]');
    if (dateInput) {
        const selectedDate = new Date(dateInput.value);
        const now = new Date();

        if (selectedDate < now) {
            alert('Дата встречи не может быть в прошлом!');
            dateInput.value = '';
            return false;
        }
    }
    return true;
}

// Подтверждение выхода
function confirmLogout() {
    return confirm('Вы уверены, что хотите выйти?');
}

// Автоматическое переключение на секцию из URL параметра
function checkUrlSection() {
    const urlParams = new URLSearchParams(window.location.search);
    const section = urlParams.get('section');
    if (section && ['meetings', 'discussions', 'quotes'].includes(section)) {
        showSection(section);
    }
}

// Вызвать при загрузке
document.addEventListener('DOMContentLoaded', function() {
    checkUrlSection();
});

// Глобальные переменные для доступа из консоли (для отладки)
window.app = {
    showSection,
    toggleComments,
    filterMeetings,
    clearFilter,
    deleteMeeting,
    nextQuote,
    validateMeetingDate,
    confirmLogout
};