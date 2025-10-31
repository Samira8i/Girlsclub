package service;

import dao.EventRegistrationDao;
import model.EventRegistration;
import java.sql.Connection;
import java.util.List;

public class EventRegistrationService {
    private EventRegistrationDao eventRegistrationDao;

    public EventRegistrationService(Connection connection) {
        this.eventRegistrationDao = new EventRegistrationDao(connection);
    }

    public boolean registerForEvent(Long postId, Long userId) {
        return eventRegistrationDao.registerForEvent(postId, userId);
    }

    public boolean cancelRegistration(Long postId, Long userId) {
        return eventRegistrationDao.cancelRegistration(postId, userId);
    }

    public boolean isUserRegistered(Long postId, Long userId) {
        EventRegistration registration = eventRegistrationDao.findByPostAndUser(postId, userId);
        return registration != null && "registered".equals(registration.getStatus());
    }

    public List<EventRegistration> getRegisteredUsers(Long postId) {
        return eventRegistrationDao.findRegisteredUsersByPost(postId);
    }

    public int getRegisteredUsersCount(Long postId) {
        return eventRegistrationDao.countRegisteredUsers(postId);
    }

    public int getAvailableSpots(Long postId, int maxAttendance) {
        int registeredCount = getRegisteredUsersCount(postId);
        return Math.max(0, maxAttendance - registeredCount);
    }

    public boolean isEventFull(Long postId, int maxAttendance) {
        return getRegisteredUsersCount(postId) >= maxAttendance;
    }
}