package com.example.FinalProject.repository;

import com.example.FinalProject.model.Notification;
import com.example.FinalProject.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByStudentIdAndIsReadFalse(Long studentId);
    List<Notification> findByStudentId(Long studentId);
    List<Notification> findByStudentOrderByCreatedAtDesc(Student student);
    List<Notification> findByStudentAndIsReadFalse(Student student);
}
