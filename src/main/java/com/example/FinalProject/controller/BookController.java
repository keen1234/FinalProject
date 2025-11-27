package com.example.FinalProject.controller;

import com.example.FinalProject.model.Book;
import com.example.FinalProject.model.Reservation;
import com.example.FinalProject.model.Student;
import com.example.FinalProject.model.Notification;
import com.example.FinalProject.model.BorrowRecord;
import com.example.FinalProject.model.admin;
import com.example.FinalProject.repository.BookRepository;
import com.example.FinalProject.repository.ReservationRepository;
import com.example.FinalProject.repository.StudentRepository;
import com.example.FinalProject.repository.NotificationRepository;
import com.example.FinalProject.repository.BorrowRecordRepository;
import com.example.FinalProject.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    
    @Autowired
    private BorrowRecordRepository borrowRecordRepository;

    @Autowired
    private AdminRepository adminRepository;

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
    public String showBooks(
            @RequestParam(value = "studentSearch", required = false) String studentSearch,
            @RequestParam(value = "bookSearch", required = false) String bookSearch,
            Model model, Authentication authentication) {
        // If bookSearch is provided, show only available books matching title or author.
        List<Book> books;
        if (bookSearch != null && !bookSearch.trim().isEmpty()) {
            String q = bookSearch.trim().toLowerCase();
            books = bookRepository.findAll().stream()
                .filter(b -> b != null && b.getStatus() == Book.Status.available)
                .filter(b -> (b.getTitle() != null && b.getTitle().toLowerCase().contains(q))
                          || (b.getAuthor() != null && b.getAuthor().toLowerCase().contains(q)))
                .collect(Collectors.toList());
        } else {
            books = bookRepository.findAll();
        }
         System.out.println("Books fetched: " + books.size());
         for (Book b : books) {
             System.out.println("Book: " + b.getTitle() + ", Author: " + b.getAuthor());
         }
         model.addAttribute("books", books);
         // Preserve the bookSearch value in the model so the template can show it in the input
         model.addAttribute("bookSearch", bookSearch);
         model.addAttribute("editBook", null);

        // Get pending reservations
        List<Reservation> pendingReservations = reservationRepository.findByStatus(Reservation.Status.pending);
        model.addAttribute("pendingReservations", pendingReservations);
        
        // Get accepted reservations (ready to be borrowed) - only those where book is still reserved
        // Include all accepted reservations so admins can still see them even if the book was returned to available
        List<Reservation> allAcceptedReservations = reservationRepository.findByStatus(Reservation.Status.accepted);
        List<Reservation> acceptedReservations = allAcceptedReservations.stream()
            .filter(reservation -> reservation.getBook() != null)
            .collect(Collectors.toList());
        model.addAttribute("acceptedReservations", acceptedReservations);
        
        List<Notification> notifications = notificationRepository.findAll();
        model.addAttribute("notifications", notifications);
        
        // Add borrowed books information
        List<BorrowRecord> borrowedBooks = borrowRecordRepository.findByStatusOrderByDueDateAsc(BorrowRecord.Status.BORROWED);
        model.addAttribute("borrowedBooks", borrowedBooks);
        
        // Add overdue books
        List<BorrowRecord> overdueBooks = borrowRecordRepository.findOverdueBooks(BorrowRecord.Status.BORROWED, LocalDate.now());
        model.addAttribute("overdueBooks", overdueBooks);
        
        // Add books due soon (within 3 days)
        List<BorrowRecord> dueSoonBooks = borrowRecordRepository.findBooksDueSoon(BorrowRecord.Status.BORROWED, LocalDate.now(), LocalDate.now().plusDays(3));
        model.addAttribute("dueSoonBooks", dueSoonBooks);
        
        // Add student search functionality
        if (studentSearch != null && !studentSearch.trim().isEmpty()) {
            List<Student> students = studentRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(studentSearch.trim(), studentSearch.trim());
            
            // For each student, get their book information
            for (Student student : students) {
                // Get borrowed books
                List<BorrowRecord> borrowedRecords = borrowRecordRepository.findByStudentAndStatus(student, BorrowRecord.Status.BORROWED);
                student.setBorrowedBooks(borrowedRecords.stream()
                    .map(BorrowRecord::getBook)
                    .collect(Collectors.toList()));
                
                // Get reserved books (accepted reservations)
                List<Reservation> acceptedReservationsList = reservationRepository.findByStudentAndStatus(student, Reservation.Status.accepted);
                student.setReservedBooks(acceptedReservationsList.stream()
                    .map(Reservation::getBook)
                    .collect(Collectors.toList()));
            }
            
            model.addAttribute("searchResults", students);
            model.addAttribute("studentSearch", studentSearch);
        }
        
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
        if (book.getDatePublish() == null) {
            book.setDatePublish(LocalDate.now()); // default value if not filled
        }
        Book saved = bookRepository.save(book);
        System.out.println("Saved book ID: " + saved.getId());
        System.out.println("Book added: " + book.getTitle() + " by " + book.getAuthor());
        List<Book> books = bookRepository.findAll();
        System.out.println("Books after add: " + books.size());
        for (Book b : books) {
            System.out.println("Book: " + b.getTitle() + ", Author: " + b.getAuthor());
        }
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

    @GetMapping("/admin/book/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String showEditBookPage(@PathVariable Long id, Model model) {
        Book book = bookRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid book Id: " + id));
        model.addAttribute("book", book);
        return "edit-book"; // Ensure you have an `edit-book.html` template
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
        book.setStatus(Book.Status.reserved);
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
        // Send notification to all admins
        List<admin> admins = adminRepository.findAll();
        for (admin adm : admins) {
            Notification adminNotif = new Notification();
            adminNotif.setAdmin(adm);
            adminNotif.setStudent(student); // Set student reference for admin notifications
            adminNotif.setMessage("New reservation request: '" + book.getTitle() + "' by " + student.getFirstName() + " " + student.getLastName());
            notificationRepository.save(adminNotif);
        }
        response.put("message", "Reservation request sent to admin!");
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/reservation/accept/{id}")
    public String acceptReservation(@PathVariable Long id) {
        try {
            Reservation reservation = reservationRepository.findById(id).orElse(null);
            if (reservation != null && reservation.getStatus() == Reservation.Status.pending) {
                reservation.setStatus(Reservation.Status.accepted);
                reservationRepository.save(reservation);
                
                Book book = reservation.getBook();
                Student student = reservation.getStudent();
                
                if (student != null && book != null) {
                    // Keep book status as reserved
                    book.setStatus(Book.Status.reserved);
                    
                    // Ensure reservation relationships are properly maintained
                    final Student finalStudent = student;
                    
                    // Add book to student's reserved books if not already there
                    if (student.getReservedBooks().stream().noneMatch(b -> b.getId().equals(book.getId()))) {
                        student.getReservedBooks().add(book);
                    }
                    
                    // Add student to book's reservers if not already there
                    if (book.getReservers().stream().noneMatch(s -> s.getId().equals(finalStudent.getId()))) {
                        book.getReservers().add(finalStudent);
                    }
                    
                    // Save both entities to persist the relationships
                    studentRepository.save(student);
                    bookRepository.save(book);
                    
                    // Send acceptance notification to student
                    Notification notification = new Notification();
                    notification.setStudent(student);
                    notification.setMessage("Great news! Your reservation for '" + book.getTitle() + "' has been accepted. Please come to the library to pick up the book and complete the borrowing process.");
                    notificationRepository.save(notification);
                    
                    System.out.println("Reservation accepted for book '" + book.getTitle() + "' by student '" + student.getFirstName() + " " + student.getLastName() + "'");
                }
            }
        } catch (Exception e) {
            System.err.println("Error accepting reservation: " + e.getMessage());
        }
        return "redirect:/admin/book";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/reservation/reject/{id}")
    public String rejectReservation(@PathVariable Long id, @RequestParam(value = "notifyMessage", required = false) String notifyMessage) {
        Reservation reservation = reservationRepository.findById(id).orElse(null);
        if (reservation == null) {
            return "redirect:/admin/book";
        }

        Book book = reservation.getBook();
        Student student = reservation.getStudent();

        // If the reservation is pending -> standard reject flow (mark rejected)
        if (reservation.getStatus() == Reservation.Status.pending) {
            reservation.setStatus(Reservation.Status.rejected);
            reservationRepository.save(reservation);

            if (student != null && book != null) {
                final Student finalStudent = student;
                // Remove book from student's reserved books
                student.getReservedBooks().removeIf(b -> b.getId().equals(book.getId()));
                // Remove student from book's reservers
                book.getReservers().removeIf(s -> s.getId().equals(finalStudent.getId()));

                // Check if there are other pending reservations for this book
                List<Reservation> otherPendingReservations = reservationRepository.findByBookIdAndStatus(book.getId(), Reservation.Status.pending);
                if (otherPendingReservations.isEmpty()) {
                    // No other pending reservations, make book available again
                    book.setStatus(Book.Status.available);
                }

                studentRepository.save(student);
                bookRepository.save(book);

                // Send rejection notification to student (use provided message if given)
                Notification notification = new Notification();
                notification.setStudent(student);
                notification.setMessage(notifyMessage != null && !notifyMessage.trim().isEmpty()
                    ? notifyMessage
                    : "Your reservation for '" + book.getTitle() + "' was rejected. The book is now available for other reservations.");
                notificationRepository.save(notification);

                System.out.println("Reservation for book '" + book.getTitle() + "' by student '" + student.getFirstName() + " " + student.getLastName() + "' was rejected and notification sent.");
            }

            return "redirect:/admin/book";
        }

        // If the reservation is accepted -> this is the 'Return to Available' flow triggered by admin
        if (reservation.getStatus() == Reservation.Status.accepted) {
            // Treat this as a rejection/return action: mark reservation rejected and make book available
            reservation.setStatus(Reservation.Status.rejected);
            reservationRepository.save(reservation);

            if (book != null) {
                // Make book available
                book.setStatus(Book.Status.available);

                // Remove this student from the book's reservers and remove book from student's reserved books
                if (student != null) {
                    final Student finalStudent = student;
                    student.getReservedBooks().removeIf(b -> b.getId().equals(book.getId()));
                    book.getReservers().removeIf(s -> s.getId().equals(finalStudent.getId()));
                    studentRepository.save(student);
                }

                bookRepository.save(book);
            }

            // Send notification to student that their reservation was rejected by admin
            if (student != null) {
                Notification notification = new Notification();
                notification.setStudent(student);
                notification.setMessage(notifyMessage != null && !notifyMessage.trim().isEmpty()
                    ? notifyMessage
                    : "Your reservation for '" + (book != null ? book.getTitle() : "the book") + "' was rejected by the admin. The book is now available.");
                notificationRepository.save(notification);
            }


            return "redirect:/admin/book";
        }

        // For other statuses, do nothing
        return "redirect:/admin/book";
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/reservation/borrow/{reservationId}")
    public String borrowReservedBook(@PathVariable Long reservationId, @RequestParam("dueDate") String dueDateStr) {
        try {
            Reservation reservation = reservationRepository.findById(reservationId).orElse(null);
            if (reservation != null && reservation.getStatus() == Reservation.Status.accepted) {
                LocalDate dueDate = LocalDate.parse(dueDateStr);
                
                Book book = reservation.getBook();
                Student student = reservation.getStudent();
                
                if (student != null && book != null) {
                    // Create borrow record
                    BorrowRecord borrowRecord = new BorrowRecord(student, book, dueDate);
                    borrowRecordRepository.save(borrowRecord);
                    
                    // Update book status to borrowed
                    book.setStatus(Book.Status.borrowed);
                    bookRepository.save(book);
                    
                    // Clean up reservation relationships
                    final Student finalStudent = student;
                    student.getReservedBooks().removeIf(b -> b.getId().equals(book.getId()));
                    book.getReservers().removeIf(s -> s.getId().equals(finalStudent.getId()));
                    
                    // Add to borrowed books relationships
                    if (student.getBorrowedBooks().stream().noneMatch(b -> b.getId().equals(book.getId()))) {
                        student.getBorrowedBooks().add(book);
                    }
                    if (book.getBorrowers().stream().noneMatch(s -> s.getId().equals(finalStudent.getId()))) {
                        book.getBorrowers().add(finalStudent);
                    }
                    
                    studentRepository.save(student);
                    
                    // Update reservation status to completed
                    reservation.setStatus(Reservation.Status.completed);
                    reservationRepository.save(reservation);
                    
                    // Send borrowing notification to student
                    Notification notification = new Notification();
                    notification.setStudent(student);
                    notification.setMessage("You have successfully borrowed '" + book.getTitle() + "'. Due date: " + dueDate.toString());
                    notificationRepository.save(notification);
                    
                    System.out.println("Book '" + book.getTitle() + "' borrowed by student '" + student.getFirstName() + " " + student.getLastName() + "' with due date: " + dueDate);
                }
            }
        } catch (Exception e) {
            System.err.println("Error borrowing reserved book: " + e.getMessage());
        }
        return "redirect:/admin/book";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/book/return/{borrowRecordId}")
    public String returnBook(@PathVariable Long borrowRecordId) {
        try {
            BorrowRecord borrowRecord = borrowRecordRepository.findById(borrowRecordId).orElse(null);
            if (borrowRecord != null && borrowRecord.getStatus() == BorrowRecord.Status.BORROWED) {
                borrowRecord.setStatus(BorrowRecord.Status.RETURNED);
                borrowRecord.setReturnedDate(LocalDateTime.now());
                borrowRecordRepository.save(borrowRecord);
                
                Book book = borrowRecord.getBook();
                Student student = borrowRecord.getStudent();
                
                // Make book available for other users to reserve
                book.setStatus(Book.Status.available);
                
                // Remove from borrowed relationships
                student.getBorrowedBooks().removeIf(b -> b.getId().equals(book.getId()));
                book.getBorrowers().removeIf(s -> s.getId().equals(student.getId()));
                
                // Clear any existing reservation relationships since book is now available
                student.getReservedBooks().removeIf(b -> b.getId().equals(book.getId()));
                book.getReservers().removeIf(s -> s.getId().equals(student.getId()));
                
                // Save the updated entities
                studentRepository.save(student);
                bookRepository.save(book);
                
                // Mark any existing reservations for this book-student pair as completed
                List<Reservation> existingReservations = reservationRepository.findByStudentAndBookAndStatus(student, book, Reservation.Status.accepted);
                for (Reservation reservation : existingReservations) {
                    reservation.setStatus(Reservation.Status.completed);
                    reservationRepository.save(reservation);
                }
                
                // Send return notification to student
                Notification notification = new Notification();
                notification.setStudent(student);
                
                String message;
                if (borrowRecord.isOverdue()) {
                    long overdueDays = Math.abs(borrowRecord.getDaysUntilDue());
                    message = "Your book '" + book.getTitle() + "' has been returned. It was " + overdueDays + " day(s) overdue. The book is now available for other users to reserve.";
                } else {
                    message = "Your book '" + book.getTitle() + "' has been returned successfully. The book is now available for other users to reserve.";
                }
                
                notification.setMessage(message);
                notificationRepository.save(notification);
                
                System.out.println("Book '" + book.getTitle() + "' returned by student '" + student.getFirstName() + " " + student.getLastName() + "'");
            }
        } catch (Exception e) {
            System.err.println("Error returning book: " + e.getMessage());
        }
        return "redirect:/admin/book";
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/book/extend-due-date/{borrowRecordId}")
    public String extendDueDate(@PathVariable Long borrowRecordId, @RequestParam("newDueDate") String newDueDateStr) {
        try {
            BorrowRecord borrowRecord = borrowRecordRepository.findById(borrowRecordId).orElse(null);
            if (borrowRecord != null && borrowRecord.getStatus() == BorrowRecord.Status.BORROWED) {
                LocalDate newDueDate = LocalDate.parse(newDueDateStr);
                LocalDate oldDueDate = borrowRecord.getDueDate();
                
                borrowRecord.setDueDate(newDueDate);
                borrowRecordRepository.save(borrowRecord);
                
                // Send notification to student
                Notification notification = new Notification();
                notification.setStudent(borrowRecord.getStudent());
                notification.setMessage("The due date for your book '" + borrowRecord.getBook().getTitle() + "' has been updated from " + oldDueDate + " to " + newDueDate + ".");
                notificationRepository.save(notification);
                
                System.out.println("Due date extended for book '" + borrowRecord.getBook().getTitle() + "' to " + newDueDate);
            }
        } catch (Exception e) {
            System.err.println("Error extending due date: " + e.getMessage());
        }
        return "redirect:/admin/book";
    }

     @PreAuthorize("hasRole('ADMIN')")
     @PostMapping("/admin/notifications/clear")
     public String clearAdminNotifications() {
         try {
             List<Notification> all = notificationRepository.findAll();
             List<Notification> adminOnly = all.stream().filter(n -> n.getAdmin() != null).collect(Collectors.toList());
             if (!adminOnly.isEmpty()) {
                 notificationRepository.deleteAll(adminOnly);
             }
         } catch (Exception e) {
             System.err.println("Failed to clear admin notifications: " + e.getMessage());
         }
         return "redirect:/admin/book";
     }
 }
