package com.example.FinalProject;

                import com.example.FinalProject.model.Book;
                import com.example.FinalProject.service.BookService;
                import org.springframework.web.bind.annotation.*;
                import org.springframework.http.ResponseEntity;

                import java.time.LocalDate;
                import java.util.Arrays;
                import java.util.List;
                import java.util.stream.Collectors;

                @RestController
                @RequestMapping("/api/books")
                @CrossOrigin(origins = "*")
                public class BookController {

                    private final BookService bookService;

                    public BookController(BookService bookService) {
                        this.bookService = bookService;
                    }

                    @GetMapping
                    public ResponseEntity<List<Book>> getAllBooks(
                            @RequestParam(required = false) String search,
                            @RequestParam(required = false) String genre,
                            @RequestParam(required = false, defaultValue = "title") String sortBy) {

                        List<Book> books = bookService.getAllBooks();

                        // Filter by search term
                        if (search != null && !search.isEmpty()) {
                            String searchLower = search.toLowerCase();
                            books = books.stream()
                                    .filter(book ->
                                            book.getTitle().toLowerCase().contains(searchLower) ||
                                                    book.getAuthor().toLowerCase().contains(searchLower) ||
                                                    book.getGenres().stream().anyMatch(g -> g.toLowerCase().contains(searchLower)) ||
                                                    book.getDescription().toLowerCase().contains(searchLower)
                                    )
                                    .collect(Collectors.toList());
                        }

                        // Filter by genre
                        if (genre != null && !genre.isEmpty()) {
                            books = books.stream()
                                    .filter(book -> book.getGenres().contains(genre))
                                    .collect(Collectors.toList());
                        }

                        // Sort books
                        switch (sortBy.toLowerCase()) {
                            case "author":
                                books.sort((b1, b2) -> b1.getAuthor().compareToIgnoreCase(b2.getAuthor()));
                                break;
                            case "date":
                                books.sort((b1, b2) -> b2.getPublishedDate().compareTo(b1.getPublishedDate())); // newest first
                                break;
                            default: // title
                                books.sort((b1, b2) -> b1.getTitle().compareToIgnoreCase(b2.getTitle()));
                        }

                        return ResponseEntity.ok(books);
                    }

                    @GetMapping("/{id}")
                    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
                        return bookService.getBookById(id)
                                .map(ResponseEntity::ok)
                                .orElse(ResponseEntity.notFound().build());
                    }

                    @GetMapping("/genres")
                    public ResponseEntity<List<String>> getAllGenres() {
                        List<String> genres = Arrays.asList(
                                "fiction", "mystery", "sci-fi", "fantasy", "biography", "history"
                        );
                        return ResponseEntity.ok(genres);
                    }
                }