package com.example.FinalProject.repository;

import com.example.FinalProject.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByStatus(Reservation.Status status);
    List<Reservation> findByStudentId(Long studentId);
    List<Reservation> findByStudentIdAndBookId(Long studentId, Long bookId);
    List<Reservation> findByBookId(Long bookId);
}
