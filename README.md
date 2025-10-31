# 👯‍♀️ GirlsClub

**Место, где девушки находят подруг и вдохновение!**

GirlsClub — это уютный уголок, где можно легко организовать встречу с подругами (или найти новых!) и пообщаться на любые темы в ленте.

## ✨ Что можно делать?

### 🗓️ **Создавать встречи**
Кофе, выставка, совместные пробежки или просто болтовня в парке — анонсируй свое событие!

### ✅ **Записываться на встречи**
Увидела что-то интересное? Жми "Я пойду!" и планируй свое время.

### 💬 **Публиковать посты**
Делитесь своими мыслями, успехами или задавайте вопросы сообществу.

### ❤️ **Поддерживать друг друга**
Ставьте лайки и комментируйте посты, чтобы показать, что вы рядом!

## 🛠️ Технологический стек

**Backend:**
- Java Servlets
- JSP (JavaServer Pages)
- PostgreSQL
- Maven

**Frontend:**
- HTML5 + CSS3
- JavaScript (Vanilla)
- JSTL

**Архитектура:**
- MVC Pattern
- DAO Pattern
- Service Layer

## 🚀 Быстрый старт

### Требования
- Java 11+
- Apache Tomcat 9+
- PostgreSQL 12+

### Установка

1. **Клонируйте репозиторий:**
```bash
git clone https://github.com/Samira8i/GirlsClub.git

2.  **Настройте базу данных:**

sql
CREATE DATABASE girlsclub;
Настройте конфигурацию:

bash
cp src/main/resources/database.properties.example src/main/resources/database.properties
Отредактируйте database.properties с вашими настройками БД.

3.  Соберите проект:

bash
mvn clean package
Разверните на Tomcat и наслаждайтесь!

📁 Структура проекта
text
src/
├── main/
│   ├── java/
│   │   ├── controller/     # Servlets
│   │   ├── service/        # Business logic
│   │   ├── dao/           # Data access
│   │   ├── model/         # Entities
│   │   └── util/          # Utilities
│   ├── webapp/
│   │   ├── WEB-INF/views/ # JSP pages
│   │   ├── css/           # Stylesheets
│   │   └── js/            # JavaScript
│   └── resources/
│       ├── database.properties.example  # Config template
└── test/
🗃️ Конфигурация базы данных

4.  Создайте src/main/resources/database.properties на основе шаблона:

properties
# Database Configuration
db.url=jdbc:postgresql://localhost:5432/girlsclub
db.username=your_username
db.password=your_password
db.driver=org.postgresql.Driver

# Connection pool
db.pool.size=10
db.timeout=30000
