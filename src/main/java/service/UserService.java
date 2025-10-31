package service;

import dao.UserDao;
import dao.SessionDao;
import model.User;
import model.Session;
import exceptions.AuthenticationException;
import util.PasswordUtil;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.time.Duration;
import java.util.UUID;

public class UserService {
    private UserDao userDao;
    private SessionDao sessionDao;
    private final Duration sessionDuration = Duration.ofDays(30);

    public UserService(Connection connection) {
        this.userDao = new UserDao(connection);
        this.sessionDao = new SessionDao(connection);
    }

    /**
     * Регистрация нового пользователя
     */
    public String registerUser(String username, String password, String passwordRepeat) {
        // Проверка совпадения паролей
        if (!password.equals(passwordRepeat)) {
            throw new AuthenticationException("Пароли не совпадают");
        }

        // Проверка существования пользователя
        if (userDao.findByUsername(username) != null) {
            throw new AuthenticationException("Пользователь с таким именем уже существует");
        }

        // Генерация соли и хэширование пароля
        String salt = PasswordUtil.generateSalt();
        String passwordHash = PasswordUtil.hashPassword(password, salt);

        // Создание пользователя
        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(passwordHash);
        user.setSalt(salt);

        // Сохраняем пользователя и получаем его ID
        boolean created = userDao.create(user);
        if (!created) {
            throw new AuthenticationException("Ошибка при создании пользователя");
        }

        // Создаем сессию
        String sessionId = generateSessionId();
        Session session = new Session(sessionId, user.getId(), LocalDateTime.now().plus(sessionDuration));
        sessionDao.addSession(session);

        return sessionId;
    }

    /**
     * Аутентификация пользователя
     */
    public String loginUser(String username, String password) {
        User user = userDao.findByUsername(username);
        if (user == null) {
            throw new AuthenticationException("Пользователь не найден");
        }

        // Проверка пароля с использованием соли
        if (!PasswordUtil.checkPassword(password, user.getSalt(), user.getPasswordHash())) {
            throw new AuthenticationException("Неверный пароль");
        }

        // Создаем новую сессию
        String sessionId = generateSessionId();
        Session session = new Session(sessionId, user.getId(), LocalDateTime.now().plus(sessionDuration));
        sessionDao.addSession(session);

        return sessionId;
    }

    /**
     * Получение пользователя по sessionId
     */
    public User getUserBySessionId(String sessionId) {
        if (sessionId == null || sessionId.trim().isEmpty()) {
            throw new AuthenticationException("Session ID не может быть пустым");
        }

        Session session = sessionDao.findBySessionId(sessionId);
        if (session == null) {
            throw new AuthenticationException("Сессия не найдена");
        }

        // Проверка срока действия сессии
        if (session.getExpireAt().isBefore(LocalDateTime.now())) {
            sessionDao.deleteSession(sessionId);
            throw new AuthenticationException("Сессия истекла");
        }

        return userDao.findById(session.getUserId());
    }

    /**
     * Выход пользователя (удаление сессии)
     */
    public void logoutUser(String sessionId) {
        if (sessionId != null) {
            sessionDao.deleteSession(sessionId);
        }
    }

    /**
     * Генерация уникального sessionId
     */
    private String generateSessionId() {
        return UUID.randomUUID().toString();
    }
}