package com.example.FinalProject.repository;

import com.example.FinalProject.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByStudentIdAndReadFalse(Long studentId);
    List<Notification> findByStudentId(Long studentId);
}
