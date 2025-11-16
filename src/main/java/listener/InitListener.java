package listener;

import dao.*;
import service.*;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@WebListener
public class InitListener implements ServletContextListener {
    private ScheduledExecutorService scheduler;
    private UserDao userDao;
    private MeetingDao meetingDao;
    private EventRegistrationDao eventRegistrationDao;
    private SessionDao sessionDao;
    private DiscussionDao discussionDao;

    private UserService userService;
    private MeetingService meetingService;
    private DiscussionService discussionService;
    private EventRegistrationService eventRegistrationService;

    @Override
    public void contextInitialized(ServletContextEvent event) {
        System.out.println("Инициализация приложения...");

        try {
            initializeDatabaseTables();
            initializeServices();
            storeServicesInContext(event);
            startBackgroundTasks();
            System.out.println("Приложение успешно инициализировано");

        } catch (Exception e) {
            System.err.println("Ошибка инициализации приложения: " + e.getMessage());
            throw new RuntimeException("Ошибка инициализации приложения", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        stopBackgroundTasks();
        System.out.println("Приложение остановлено");
    }

    private void initializeDatabaseTables() {
        try {
            userDao = new UserDao();
            meetingDao = new MeetingDao();
            eventRegistrationDao = new EventRegistrationDao();
            sessionDao = new SessionDao();
            discussionDao = new DiscussionDao();

            System.out.println("Все таблицы БД инициализированы");

        } catch (Exception e) {
            throw new RuntimeException("Ошибка инициализации таблиц БД", e);
        }
    }

    private void initializeServices() {
        userService = new UserService(userDao, sessionDao);
        meetingService = new MeetingService(meetingDao, eventRegistrationDao);
        discussionService = new DiscussionService(discussionDao);
        eventRegistrationService = new EventRegistrationService(eventRegistrationDao);

        System.out.println("Все сервисы инициализированы");
    }

    private void storeServicesInContext(ServletContextEvent event) {
        // Сохраняю сервисы в контекст приложения для доступа из сервлетов
        var context = event.getServletContext();
        context.setAttribute("userService", userService);
        context.setAttribute("meetingService", meetingService);
        context.setAttribute("discussionService", discussionService);
        context.setAttribute("eventRegistrationService", eventRegistrationService);

        System.out.println("Сервисы сохранены в контекст приложения");
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
            sessionDao.cleanupExpiredSessions();
            System.out.println("Просроченные сессии очищены");
        } catch (Exception e) {
            System.err.println("Ошибка очистки сессий: " + e.getMessage());
        }
    }
}