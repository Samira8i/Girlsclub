package model;

import java.time.LocalDateTime;

public class EventRegistration {
    private Long id;
    private Long postId;
    private Long userId; //ID пользователя который регистрируется
    private User user; //TODO: избыточно, поменять логику
    private String status; // "registered", "cancelled"
    private LocalDateTime registrationDate;

    // Конструкторы
    public EventRegistration() {}

    public EventRegistration(Long postId, Long userId, String status) {
        this.postId = postId;
        this.userId = userId;
        this.status = status;
        this.registrationDate = LocalDateTime.now();
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPostId() { return postId; }
    public void setPostId(Long postId) { this.postId = postId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(LocalDateTime registrationDate) { this.registrationDate = registrationDate; }
}