package com.example.FinalProject.repository;

import com.example.FinalProject.model.Reservation;
import com.example.FinalProject.model.Student;
import com.example.FinalProject.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByStatus(Reservation.Status status);
    List<Reservation> findByStudentId(Long studentId);
    List<Reservation> findByStudentIdAndBookId(Long studentId, Long bookId);
    List<Reservation> findByBookId(Long bookId);
    List<Reservation> findByBookIdAndStatus(Long bookId, Reservation.Status status);
    List<Reservation> findByStudentAndStatus(Student student, Reservation.Status status);
    List<Reservation> findByStudentAndBookAndStatus(Student student, Book book, Reservation.Status status);
}
