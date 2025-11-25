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
    // existing entity-based method (if still used elsewhere)
    List<Reservation> findByStudentAndStatus(Student student, Reservation.Status status);

    // id-based methods to avoid detached entity problems and to satisfy usages in controllers
    List<Reservation> findByStudentIdAndStatus(Long studentId, Reservation.Status status);
    List<Reservation> findByStudentIdAndStatusIn(Long studentId, List<Reservation.Status> statuses);

    List<Reservation> findByStudentIdAndBookId(Long studentId, Long bookId); // fixes the missing method error
    List<Reservation> findByBookId(Long bookId);
    List<Reservation> findByBookIdAndStatus(Long bookId, Reservation.Status status);
    List<Reservation> findByBookIdAndStatusIn(Long bookId, List<Reservation.Status> statuses);

    // convenience entity-based method used in return flow
    List<Reservation> findByStudentAndBookAndStatus(Student student, Book book, Reservation.Status status);

    // simple status lookup
    List<Reservation> findByStatus(Reservation.Status status);
}
