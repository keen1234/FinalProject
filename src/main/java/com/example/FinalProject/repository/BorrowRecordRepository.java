package com.example.FinalProject.repository;

import com.example.FinalProject.model.BorrowRecord;
import com.example.FinalProject.model.Book;
import com.example.FinalProject.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {
    
    // Find active borrow record for a specific book and student
    Optional<BorrowRecord> findByStudentAndBookAndStatus(Student student, Book book, BorrowRecord.Status status);
    
    // Find all active borrowed books by student
    List<BorrowRecord> findByStudentAndStatus(Student student, BorrowRecord.Status status);
    
    // Find all active borrowed books by book
    List<BorrowRecord> findByBookAndStatus(Book book, BorrowRecord.Status status);
    
    // Find all currently borrowed books (for admin view)
    List<BorrowRecord> findByStatusOrderByDueDateAsc(BorrowRecord.Status status);
    
    // Find overdue books
    @Query("SELECT br FROM BorrowRecord br WHERE br.status = :status AND br.dueDate < :currentDate")
    List<BorrowRecord> findOverdueBooks(@Param("status") BorrowRecord.Status status, @Param("currentDate") LocalDate currentDate);
    
    // Find books due soon (within specified days)
    @Query("SELECT br FROM BorrowRecord br WHERE br.status = :status AND br.dueDate BETWEEN :currentDate AND :dueSoonDate")
    List<BorrowRecord> findBooksDueSoon(@Param("status") BorrowRecord.Status status, 
                                        @Param("currentDate") LocalDate currentDate, 
                                        @Param("dueSoonDate") LocalDate dueSoonDate);
    
    // Find all borrow records for a student (for history)
    List<BorrowRecord> findByStudentOrderByBorrowedDateDesc(Student student);
    
    // Find all borrow records for a book (for history)
    List<BorrowRecord> findByBookOrderByBorrowedDateDesc(Book book);
}