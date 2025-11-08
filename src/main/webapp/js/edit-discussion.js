document.addEventListener('DOMContentLoaded', function() {
    setupCharacterCounters();
    setupFormValidation();
    setupDeleteConfirmation();
});

// Настройка счетчиков символов
function setupCharacterCounters() {
    const titleInput = document.getElementById('title');
    const titleCount = document.getElementById('title-count');
    const contentInput = document.getElementById('content');
    const contentCount = document.getElementById('content-count');

    if (titleInput && titleCount) {
        titleInput.addEventListener('input', function() {
            titleCount.textContent = this.value.length;
        });
    }

    if (contentInput && contentCount) {
        contentInput.addEventListener('input', function() {
            contentCount.textContent = this.value.length;
        });
    }
}

// Настройка валидации формы
function setupFormValidation() {
    const form = document.querySelector('.discussion-form');
    if (!form) return;

    form.addEventListener('submit', function(e) {
        if (!validateForm()) {
            e.preventDefault();
            return false;
        }
    });
}

// Валидация формы
function validateForm() {
    const title = document.getElementById('title')?.value.trim();
    const content = document.getElementById('content')?.value.trim();

    if (!title) {
        alert('Пожалуйста, введите заголовок обсуждения');
        document.getElementById('title').focus();
        return false;
    }

    if (!content) {
        alert('Пожалуйста, введите содержание обсуждения');
        document.getElementById('content').focus();
        return false;
    }

    if (title.length > 200) {
        alert('Заголовок не может превышать 200 символов');
        document.getElementById('title').focus();
        return false;
    }

    if (content.length > 2000) {
        alert('Содержание не может превышать 2000 символов');
        document.getElementById('content').focus();
        return false;
    }

    return true;
}

// Подтверждение удаления
function setupDeleteConfirmation() {
    const deleteForm = document.querySelector('.delete-form');
    if (deleteForm) {
        deleteForm.addEventListener('submit', function(e) {
            if (!confirm('Вы уверены, что хотите удалить это обсуждение? Это действие нельзя отменить.')) {
                e.preventDefault();
                return false;
            }
        });
    }
}