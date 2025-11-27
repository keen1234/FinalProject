package com.example.FinalProject.service;

import com.example.FinalProject.model.Notification;
import com.example.FinalProject.model.Notification.RecipientRole;
import com.example.FinalProject.model.Student;
import com.example.FinalProject.model.admin;
import com.example.FinalProject.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    public Notification createStudentNotification(Student student, String message) {
        Notification notification = new Notification();
        notification.setStudent(student);
        notification.setMessage(message);
        notification.setRecipientRole(RecipientRole.STUDENT);
        return notificationRepository.save(notification);
    }

    public Notification createAdminNotification(admin adminEntity, Student originStudent, String message) {
        Notification notification = new Notification();
        notification.setAdmin(adminEntity);
        notification.setStudent(originStudent); // preserve origin info
        notification.setMessage(message);
        notification.setRecipientRole(RecipientRole.ADMIN);
        return notificationRepository.save(notification);
    }

    public List<Notification> findAdminNotificationsForAdmin(admin adminEntity) {
        // If adminEntity is null, return global admin notifications (admin field null but role ADMIN)
        if (adminEntity != null) {
            return notificationRepository.findByAdminOrderByCreatedAtDesc(adminEntity);
        }
        return notificationRepository.findByRecipientRoleOrderByCreatedAtDesc(RecipientRole.ADMIN);
    }

    public List<Notification> findStudentNotifications(Student student) {
        return notificationRepository.findByStudentOrderByCreatedAtDesc(student);
    }

    @Transactional
    public void clearAllAdminNotifications() {
        notificationRepository.deleteByAdminIsNotNull();
    }

    @Transactional
    public void clearAdminNotificationsForAdmin(admin adminEntity) {
        if (adminEntity == null) {
            clearAllAdminNotifications();
            return;
        }
        // delete notifications that are for this admin
        notificationRepository.deleteAll(notificationRepository.findByAdminOrderByCreatedAtDesc(adminEntity));
    }

    @Transactional
    public void markAllStudentNotificationsRead(Student student) {
        List<Notification> unread = notificationRepository.findByStudentAndIsReadFalse(student);
        for (Notification n : unread) {
            n.setRead(true);
        }
        notificationRepository.saveAll(unread);
    }

    @Transactional
    public void clearStudentNotifications(Student student) {
        List<Notification> list = notificationRepository.findByStudentOrderByCreatedAtDesc(student);
        if (list != null && !list.isEmpty()) {
            notificationRepository.deleteAll(list);
        }
    }

    public long countUnreadForStudent(Long studentId) {
        return notificationRepository.findByStudentIdAndIsReadFalse(studentId).size();
    }

    public long countUnreadForAdmin(Long adminId) {
        return notificationRepository.findByAdminIdAndIsReadFalse(adminId).size();
    }
}
