package listener;

import dao.*;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@WebListener
public class InitListener implements ServletContextListener {
    private ScheduledExecutorService scheduler;

    @Override
    public void contextInitialized(ServletContextEvent event) {
        System.out.println("Инициализация приложения...");

        try {
            initializeDatabaseTables();
            startBackgroundTasks();
            System.out.println("Приложение успешно инициализировано");

        } catch (Exception e) {
            System.err.println("Ошибка инициализации приложения: " + e.getMessage());
            throw new RuntimeException("Ошибка инициализации приложения", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        System.out.println("Остановка приложения...");

        stopBackgroundTasks();
        System.out.println("Приложение остановлено");
    }

    private void initializeDatabaseTables() {
        try {
            new UserDao();
            new MeetingDao();
            new EventRegistrationDao();
            new SessionDao();
            new DiscussionDao();

            System.out.println("Все таблицы БД инициализированы");

        } catch (Exception e) {
            throw new RuntimeException("Ошибка инициализации таблиц БД", e);
        }
    }

    private void startBackgroundTasks() {
        scheduler = Executors.newScheduledThreadPool(1);
        // Очистка сессий каждые 30 минут
        scheduler.scheduleAtFixedRate(this::cleanupExpiredSessions, 0, 30, TimeUnit.MINUTES);
        System.out.println("Фоновые задачи запущены");
    }

    private void stopBackgroundTasks() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
            System.out.println("Фоновые задачи остановлены");
        }
    }

    private void cleanupExpiredSessions() {
        try {
            new SessionDao().cleanupExpiredSessions();
            System.out.println("Просроченные сессии очищены");
        } catch (Exception e) {
            System.err.println("Ошибка очистки сессий: " + e.getMessage());
        }
    }
}