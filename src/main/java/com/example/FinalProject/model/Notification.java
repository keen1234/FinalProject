package com.example.FinalProject.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "admin_id")
    private admin admin;

    private String message;
    
    @Column(name = "is_read")
    private boolean isRead = false;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    // New enum to explicitly indicate intended recipient role
    public enum RecipientRole {
        STUDENT,
        ADMIN
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "recipient_role")
    private RecipientRole recipientRole = RecipientRole.STUDENT; // default for backward compatibility

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }
    public admin getAdmin() { return admin; }
    public void setAdmin(admin admin) { this.admin = admin; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public boolean isRead() { return isRead; }
    public void setRead(boolean isRead) { this.isRead = isRead; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public RecipientRole getRecipientRole() { return recipientRole; }
    public void setRecipientRole(RecipientRole recipientRole) { this.recipientRole = recipientRole; }
}
