package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MeetingPost {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime eventDate;
    private Integer maxAttendance;
    private String location;
    private LocalDateTime createdAt;
    private Long authorId;
    private User author;
    private boolean userRegistered; //зарегестрирован ли текущий пользователь на встречу
    private List<EventRegistration> participants; //участники
    private int availableSpots; //количество свободных мест
    private boolean full; //занято полностью или нет

    public MeetingPost() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getEventDate() { return eventDate; }
    public void setEventDate(LocalDateTime eventDate) { this.eventDate = eventDate; }

    public Integer getMaxAttendance() { return maxAttendance; }
    public void setMaxAttendance(Integer maxAttendance) { this.maxAttendance = maxAttendance; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Long getAuthorId() { return authorId; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }

    public User getAuthor() { return author; }
    public void setAuthor(User author) { this.author = author; }

    public boolean isUserRegistered() { return userRegistered; }
    public void setUserRegistered(boolean userRegistered) { this.userRegistered = userRegistered; }

    public List<EventRegistration> getParticipants() { return participants; }
    public void setParticipants(List<EventRegistration> participants) { this.participants = participants; }

    public int getAvailableSpots() { return availableSpots; }
    public void setAvailableSpots(int availableSpots) { this.availableSpots = availableSpots; }

    public boolean isFull() { return full; }
    public void setFull(boolean full) { this.full = full; }


    public String getFormattedEventDate() { //TODO: понять используются ли и нужны ли
        if (eventDate == null) return "Дата не указана";
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
            return eventDate.format(formatter);
        } catch (Exception e) {
            return "Ошибка формата даты";
        }
    }

}