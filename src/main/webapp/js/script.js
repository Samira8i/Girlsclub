
const quotes = [
    {text: "Каждая женщина - это вселенная, полная загадок и красоты.", author: "Коко Шанель"},
    {text: "Ты сегодня выглядишь особенно прекрасно!", author: "Ваше отражение"},
    {text: "Сила женщины не в слабости, а в умении быть разной.", author: "Неизвестный автор"},
    {text: "Ты способна на большее, чем себе представляешь.", author: "Ваша уверенность"},
    {text: "Красота есть в каждой из нас, главное - уметь её разглядеть.", author: "Неизвестный автор"},
    {text: "Ты уникальна, и в этом твоя сила.", author: "Ваше внутреннее я"},
    {text: "Сегодня твой день сиять и быть счастливой!", author: "Ваше настроение"}
];

// Показать случайную цитату
function showRandomQuote() {
    const randomIndex = Math.floor(Math.random() * quotes.length);
    const quote = quotes[randomIndex];

    const quoteElement = document.querySelector('.main-quote');
    const authorElement = document.querySelector('.quote-author');

    if (quoteElement && authorElement) {
        quoteElement.textContent = `"${quote.text}"`;
        authorElement.textContent = `- ${quote.author}`;
    }
}

// Инициализация при загрузке
document.addEventListener('DOMContentLoaded', function() {
    // Показываем случайную цитату
    showRandomQuote();

    // Назначаем обработчики для кнопок
    document.querySelectorAll('.btn-register').forEach(button => {
        button.addEventListener('click', function() {
            alert('Функция записи на встречу будет реализована позже!');
        });
    });

    document.querySelectorAll('.btn-like').forEach(button => {
        button.addEventListener('click', function() {
            alert('Функция лайков будет реализована позже!');
        });
    });
});