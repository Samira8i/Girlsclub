package service;

import dao.MeetingDao;
import dao.EventRegistrationDao;
import model.MeetingPost;
import model.EventRegistration;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class MeetingService {
    private MeetingDao meetingDao;
    private EventRegistrationDao eventRegistrationDao;

    public MeetingService(Connection connection) {
        this.meetingDao = new MeetingDao(connection);
        this.eventRegistrationDao = new EventRegistrationDao(connection);
    }

    public boolean createMeeting(String title, String description, String eventDate,
                                 int maxAttendance, String location, Long authorId) {
        try {
            System.out.println("Полученная дата на вход: " + eventDate);

            LocalDateTime dateTime = parseDateTime(eventDate);
            if (dateTime == null) {
                System.err.println("Не удалось распарсить дату: " + eventDate);
                return false;
            }

            MeetingPost meeting = new MeetingPost();
            meeting.setTitle(title);
            meeting.setDescription(description);
            meeting.setEventDate(dateTime);
            meeting.setMaxAttendance(maxAttendance);
            meeting.setLocation(location);
            meeting.setAuthorId(authorId);

            System.out.println("Дата успешно преобразована: " + dateTime);

            return meetingDao.create(meeting);
        } catch (Exception e) {
            System.err.println("Ошибка при создании встречи: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<MeetingPost> getAllMeetings() {
        List<MeetingPost> meetings = meetingDao.findAll();

        // Обогащаем данные о встречах информацией о регистрациях
        for (MeetingPost meeting : meetings) {
            enrichMeetingWithRegistrationData(meeting);
        }

        return meetings;
    }

    public MeetingPost getMeetingById(Long id) {
        MeetingPost meeting = meetingDao.findById(id);
        if (meeting != null) {
            enrichMeetingWithRegistrationData(meeting);
        }
        return meeting;
    }

    public boolean updateMeeting(Long id, String title, String description, String eventDate,
                                 int maxAttendance, String location) {
        try {
            MeetingPost meeting = meetingDao.findById(id);
            if (meeting == null) return false;

            LocalDateTime dateTime = parseDateTime(eventDate);
            if (dateTime == null) {
                return false;
            }

            meeting.setTitle(title);
            meeting.setDescription(description);
            meeting.setEventDate(dateTime);
            meeting.setMaxAttendance(maxAttendance);
            meeting.setLocation(location);

            return meetingDao.update(meeting);
        } catch (Exception e) {
            System.err.println(" Ошибка при обновлении встречи: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean isAuthor(Long meetingId, Long userId) {
        MeetingPost meeting = meetingDao.findById(meetingId);
        return meeting != null && meeting.getAuthorId().equals(userId);
    }

    public boolean deleteMeeting(Long id) {
        return meetingDao.delete(id);
    }

    private void enrichMeetingWithRegistrationData(MeetingPost meeting) {
        if (meeting == null) return;

        // Получаем список участников
        List<EventRegistration> participants = eventRegistrationDao.findRegisteredUsersByPost(meeting.getId());
        meeting.setParticipants(participants);

        // Вычисляем доступные места
        int registeredCount = participants.size();
        meeting.setAvailableSpots(Math.max(0, meeting.getMaxAttendance() - registeredCount));

        // Проверяем, заполнена ли встреча
        meeting.setFull(registeredCount >= meeting.getMaxAttendance());
    }

    private LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.trim().isEmpty()) {
            return null;
        }
//TODO: разобраться с парсировкоц даты и сделать адекватную
        dateTimeStr = dateTimeStr.trim();
        System.out.println("Пытаемся распарсить дату: " + dateTimeStr);

        // Пробуем разные форматы
        DateTimeFormatter[] formatters = {
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),
                DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"),
                DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"),
                DateTimeFormatter.ISO_LOCAL_DATE_TIME
        };

        for (DateTimeFormatter formatter : formatters) {
            try {
                LocalDateTime result = LocalDateTime.parse(dateTimeStr, formatter);
                System.out.println("Дата успешно распарсена с форматом: " + formatter);
                return result;
            } catch (DateTimeParseException e) {
                // Пробуем следующий формат
            }
        }

        // Если стандартные форматы не подошли, пробуем ручное преобразование
        try {
            // Обрабатываем формат из HTML datetime-local (2026-01-10T04:04)
            if (dateTimeStr.contains("T")) {
                String[] parts = dateTimeStr.split("T");
                if (parts.length == 2) {
                    String datePart = parts[0];
                    String timePart = parts[1];

                    // Добавляем секунды если их нет
                    if (timePart.split(":").length == 2) {
                        timePart += ":00";
                    }

                    String formatted = datePart + " " + timePart;
                    System.out.println("🔄 Форматированная строка: " + formatted);

                    return LocalDateTime.parse(formatted, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                }
            }

            // Пробуем просто добавить секунды если их нет
            if (dateTimeStr.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}")) {
                return LocalDateTime.parse(dateTimeStr + ":00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            }

        } catch (Exception e) {
            System.err.println("Ошибка при ручном парсинге даты: " + e.getMessage());
        }

        System.err.println("Не удалось распарсить дату ни одним из методов: " + dateTimeStr);
        return null;
    }
}