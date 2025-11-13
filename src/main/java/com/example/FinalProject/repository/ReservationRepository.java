package com.example.FinalProject.repository;

import com.example.FinalProject.model.Reservation;
import com.example.FinalProject.model.Student;
import com.example.FinalProject.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    // Returns active reservations for the given student.
    // Use string literal 'ACTIVE' to avoid referencing an enum constant that may not exist.
    @Query("SELECT r FROM Reservation r WHERE r.student.id = :studentId AND r.status = 'ACTIVE' ORDER BY r.id DESC")
    List<Reservation> findActiveByStudentId(@Param("studentId") Long studentId);

    List<Reservation> findByStatus(Reservation.Status status);
    List<Reservation> findByStudentId(Long studentId);
    List<Reservation> findByStudentIdAndBookId(Long studentId, Long bookId);
    List<Reservation> findByBookId(Long bookId);
    List<Reservation> findByBookIdAndStatus(Long bookId, Reservation.Status status);
    List<Reservation> findByStudentAndStatus(Student student, Reservation.Status status);
    List<Reservation> findByStudentAndBookAndStatus(Student student, Book book, Reservation.Status status);
}
