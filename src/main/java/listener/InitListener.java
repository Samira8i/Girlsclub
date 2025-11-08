package listener;

import util.DatabaseUtil;
import dao.SessionDao;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@WebListener
public class InitListener implements ServletContextListener {

    private ScheduledExecutorService scheduler;

    @Override
    public void contextInitialized(ServletContextEvent event) {
        System.out.println("Приложение запускается...");

        try {
            initializeDatabase();
            startBackgroundTasks();
            System.out.println("Приложение успешно инициализировано");

        } catch (Exception e) {
            System.err.println("Ошибка инициализации приложения: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Ошибка инициализации приложения", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        System.out.println("Приложение останавливается...");

        // Останавливаем фоновые задачи
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }

        System.out.println("Приложение остановлено");
    }

    private void initializeDatabase() {
        try {
            Connection connection = DatabaseUtil.getConnection();
            if (connection != null) {
                connection.close(); // Закрываем соединение, таблицы уже созданы
            }
            System.out.println("База данных инициализирована");

        } catch (Exception e) {
            System.err.println("Ошибка инициализации базы данных: " + e.getMessage());
            throw new RuntimeException("Ошибка инициализации БД", e);
        }
    }

    private void startBackgroundTasks() {
        scheduler = Executors.newScheduledThreadPool(1);

        // Задача для очистки просроченных сессий каждые 30 минут
        scheduler.scheduleAtFixedRate(this::cleanupExpiredSessions, 0, 5, TimeUnit.SECONDS);

        System.out.println("Фоновые задачи запущены");
    }

    private void cleanupExpiredSessions() {
        try (Connection connection = DatabaseUtil.getConnection()) {
            SessionDao sessionDao = new SessionDao(connection);
            sessionDao.cleanupExpiredSessions();
            System.out.println("Просроченные сессии очищены");
        } catch (SQLException e) {
            System.err.println("Ошибка очистки сессий: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Неожиданная ошибка при очистке сессий: " + e.getMessage());
        }
    }
}