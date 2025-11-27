package com.example.FinalProject.repository;

import com.example.FinalProject.model.Notification;
import com.example.FinalProject.model.Student;
import com.example.FinalProject.model.admin;
import com.example.FinalProject.model.Notification.RecipientRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByStudentIdAndIsReadFalse(Long studentId);
    List<Notification> findByStudentId(Long studentId);
    List<Notification> findByStudentOrderByCreatedAtDesc(Student student);
    List<Notification> findByStudentAndIsReadFalse(Student student);

    // Find notifications by recipient role
    List<Notification> findByRecipientRoleOrderByCreatedAtDesc(RecipientRole role);
    List<Notification> findByRecipientRoleAndIsReadFalse(RecipientRole role);

    // Admin-specific queries
    List<Notification> findByAdminOrderByCreatedAtDesc(admin admin);
    List<Notification> findByAdminIdAndIsReadFalse(Long adminId);

    // Delete all notifications that are targeted at any admin using JPQL
    @Modifying
    @Transactional
    @Query("delete from Notification n where n.admin is not null")
    void deleteAdminNotifications();

    // Convenience derived delete
    @Modifying
    @Transactional
    void deleteByAdminIsNotNull();
}
