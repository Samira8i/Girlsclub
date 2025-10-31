package util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.UUID;

/**
 * Утилита для работы с паролями с использованием соли
 */
public class PasswordUtil {

    /**
     * Генерация случайной соли
     */
    public static String generateSalt() {
        return UUID.randomUUID().toString();
    }

    /**
     * Хэшировать пароль с солью используя SHA-256
     */
    public static String hashPassword(String password, String salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String saltedPassword = password + salt;
            byte[] hashedBytes = digest.digest(saltedPassword.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashedBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Алгоритм SHA-256 не найден", e);
        }
    }

    /**
     * Проверить пароль
     */
    public static boolean checkPassword(String password, String salt, String hashedPassword) {
        return hashPassword(password, salt).equals(hashedPassword);
    }

    /**
     * Простая проверка сложности пароля
     */
    public static boolean isPasswordValid(String password) {
        return password != null && password.length() >= 3;
    }
}