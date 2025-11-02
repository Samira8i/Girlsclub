package service;

import java.sql.SQLException;
import util.DatabaseUtil;

public class ServiceFactory {

    public static UserService getUserService() {
        try {
            return new UserService(DatabaseUtil.getConnection());
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка создания UserService", e);
        }
    }

    public static MeetingService getMeetingService() {
        try {
            return new MeetingService(DatabaseUtil.getConnection());
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка создания MeetingService", e);
        }
    }

    public static EventRegistrationService getEventRegistrationService() {
        try {
            return new EventRegistrationService(DatabaseUtil.getConnection());
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка создания EventRegistrationService", e);
        }
    }

    public static DiscussionService getDiscussionService() {
        try {
            return new DiscussionService(DatabaseUtil.getConnection());
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка создания DiscussionService", e);
        }
    }
}