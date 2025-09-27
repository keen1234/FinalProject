package com.example.FinalProject.controller;

import com.example.FinalProject.model.Book;
import com.example.FinalProject.model.Reservation;
import com.example.FinalProject.model.Student;
import com.example.FinalProject.model.Notification;
import com.example.FinalProject.repository.BookRepository;
import com.example.FinalProject.repository.ReservationRepository;
import com.example.FinalProject.repository.StudentRepository;
import com.example.FinalProject.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class BookController {
    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @GetMapping("/book")
    public String listBooks(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam(value = "genre", required = false) String genre,
            Model model) {
        List<Book> books;
        if (search != null && !search.isEmpty()) {
            books = bookRepository.findByTitleContainingIgnoreCase(search);
        } else if (genre != null && !genre.isEmpty()) {
            books = bookRepository.findByGenreContainingIgnoreCase(genre);
        } else {
            books = bookRepository.findAll();
        }
        if (sort != null) {
            switch (sort) {
                case "title":
                    books = books.stream().sorted(Comparator.comparing(Book::getTitle)).collect(Collectors.toList());
                    break;
                case "author":
                    books = books.stream().sorted(Comparator.comparing(Book::getAuthor)).collect(Collectors.toList());
                    break;
                case "date":
                    books = books.stream().sorted(Comparator.comparing(Book::getDatePublish)).collect(Collectors.toList());
                    break;
            }
        }
        model.addAttribute("books", books);
        // Add genres for dropdown
        List<String> genres = bookRepository.findAll().stream()
            .map(Book::getGenre)
            .distinct()
            .collect(Collectors.toList());
        model.addAttribute("genres", genres);
        return "book";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/book")
    public String adminBookPage(Model model, Authentication authentication) {
        List<Book> books = bookRepository.findAll();
        model.addAttribute("books", books);
        model.addAttribute("editBook", null);
        List<Reservation> pendingReservations = reservationRepository.findByStatus(Reservation.Status.pending);
        model.addAttribute("pendingReservations", pendingReservations);
        List<Notification> notifications = notificationRepository.findAll();
        model.addAttribute("notifications", notifications);
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            Map<String, Object> admin = new HashMap<>();
            if (principal instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) principal;
                admin.put("name", userDetails.getUsername());
                admin.put("email", "");
                admin.put("status", "Active");
            } else if (principal instanceof String) {
                admin.put("name", principal);
                admin.put("email", "");
                admin.put("status", "Active");
            }
            model.addAttribute("admin", admin);
        }
        return "admin-book";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/book/add")
    public String addBook(@ModelAttribute Book book) {
        book.setStatus(Book.Status.available);
        bookRepository.save(book);
        return "redirect:/admin/book";
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/book/edit/{id}")
    public String editBook(@PathVariable Long id, @ModelAttribute Book book) {
        Book existingBook = bookRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid book Id:" + id));
        book.setId(id);
        book.setStatus(existingBook.getStatus());
        book.setCountry(existingBook.getCountry()); // Preserve country if not edited
        book.setTime(existingBook.getTime()); // Preserve time if not edited
        bookRepository.save(book);
        return "redirect:/admin/book";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/book/delete/{id}")
    public String deleteBook(@PathVariable Long id) {
        Book book = bookRepository.findById(id).orElse(null);
        if (book != null) {
            // Remove book from all students' borrowedBooks and reservedBooks
            List<Student> allStudents = studentRepository.findAll();
            for (Student student : allStudents) {
                student.getBorrowedBooks().removeIf(b -> b.getId().equals(id));
                student.getReservedBooks().removeIf(b -> b.getId().equals(id));
                studentRepository.save(student);
            }
            // Remove book from all borrowers and reservers
            book.getBorrowers().clear();
            book.getReservers().clear();
            // Delete all reservations for this book
            List<Reservation> reservations = reservationRepository.findByBookId(id);
            reservationRepository.deleteAll(reservations);
            // Delete all notifications for this book
            List<Notification> notifications = notificationRepository.findAll();
            for (Notification notification : notifications) {
                if (notification.getMessage() != null && notification.getMessage().contains(book.getTitle())) {
                    notificationRepository.delete(notification);
                }
            }
            bookRepository.delete(book);
        }
        return "redirect:/admin/book";
    }

    @PostMapping("/api/reserve")
    public ResponseEntity<Map<String, String>> reserveBook(@RequestBody Map<String, String> payload, Authentication authentication) {
        Map<String, String> response = new HashMap<>();
        String title = payload.get("title");
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
            response.put("message", "Not authenticated");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Student student = null;
        // Try to extract Student from StudentDetails principal
        if (userDetails instanceof com.example.FinalProject.model.StudentDetails) {
            student = ((com.example.FinalProject.model.StudentDetails) userDetails).getStudent();
        } else if (userDetails instanceof Student) {
            student = (Student) userDetails;
        } else {
            try {
                java.lang.reflect.Method getStudentMethod = userDetails.getClass().getMethod("getStudent");
                Object studentObj = getStudentMethod.invoke(userDetails);
                if (studentObj instanceof Student) {
                    student = (Student) studentObj;
                }
            } catch (Exception e) {
                response.put("message", "Could not resolve student");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        }
        if (student == null) {
            response.put("message", "Student not found");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        Book book = bookRepository.findByTitleContainingIgnoreCase(title).stream().findFirst().orElse(null);
        if (book == null) {
            response.put("message", "Book not found");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        if (book.getStatus() != Book.Status.available) {
            response.put("message", "Book is not available for reservation");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        List<Reservation> existingReservations = reservationRepository.findByStudentIdAndBookId(student.getId(), book.getId());
        boolean alreadyReserved = existingReservations.stream().anyMatch(r -> r.getStatus() == Reservation.Status.pending || r.getStatus() == Reservation.Status.accepted);
        if (alreadyReserved) {
            response.put("message", "You already have a reservation for this book");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        Reservation reservation = new Reservation();
        reservation.setStudent(student);
        reservation.setBook(book);
        reservation.setStatus(Reservation.Status.pending);
        reservationRepository.save(reservation);
        book.setStatus(Book.Status.not_available);
        // Add to reserved lists
        final Student finalStudent = student;
        if (student.getReservedBooks().stream().noneMatch(b -> b.getId().equals(book.getId()))) {
            student.getReservedBooks().add(book);
        }
        if (book.getReservers().stream().noneMatch(s -> s.getId().equals(finalStudent.getId()))) {
            book.getReservers().add(finalStudent);
        }
        studentRepository.save(student);
        bookRepository.save(book);
        response.put("message", "Reservation request sent to admin!");
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/reservation/accept/{id}")
    public String acceptReservation(@PathVariable Long id) {
        Reservation reservation = reservationRepository.findById(id).orElse(null);
        if (reservation != null && reservation.getStatus() == Reservation.Status.pending) {
            reservation.setStatus(Reservation.Status.accepted);
            reservationRepository.save(reservation);
            Book book = reservation.getBook();
            book.setStatus(Book.Status.borrowed);
            Student student = reservation.getStudent();
            // Move from reserved to borrowed
            if (student != null && book != null) {
                final Student finalStudent2 = student;
                student.getReservedBooks().removeIf(b -> b.getId().equals(book.getId()));
                if (student.getBorrowedBooks().stream().noneMatch(b -> b.getId().equals(book.getId()))) {
                    student.getBorrowedBooks().add(book);
                }
                book.getReservers().removeIf(s -> s.getId().equals(finalStudent2.getId()));
                if (book.getBorrowers().stream().noneMatch(s -> s.getId().equals(finalStudent2.getId()))) {
                    book.getBorrowers().add(finalStudent2);
                }
                studentRepository.save(student);
                studentRepository.flush(); // Ensure changes are written to DB
                bookRepository.save(book);
                System.out.println("Book " + book.getTitle() + " added to student " + student.getFirstName() + "'s borrowedBooks and persisted.");
                Notification notification = new Notification();
                notification.setStudent(student);
                notification.setMessage("Your reservation for '" + book.getTitle() + "' was accepted!");
                notificationRepository.save(notification);
            }
        }
        return "redirect:/admin/book";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/reservation/reject/{id}")
    public String rejectReservation(@PathVariable Long id) {
        Reservation reservation = reservationRepository.findById(id).orElse(null);
        if (reservation != null && reservation.getStatus() == Reservation.Status.pending) {
            reservation.setStatus(Reservation.Status.rejected);
            reservationRepository.save(reservation);
            // TODO: Notify student of rejection
        }
        return "redirect:/admin/book";
    }
}
