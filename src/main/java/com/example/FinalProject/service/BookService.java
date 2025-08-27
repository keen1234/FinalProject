package com.example.FinalProject.service;

import com.example.FinalProject.model.Book;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    private final List<Book> books = Arrays.asList(
            new Book(1L, "The Silent Echo", "Eleanor Rivers", LocalDate.of(2023, 6, 15),
                    Arrays.asList("mystery", "fiction"),
                    "A gripping mystery set in a small coastal town where ancient secrets resurface.",
                    "https://storage.googleapis.com/workspace-0f70711f-8b4e-4d94-86f1-2a93ccde5887/image/4bcb2fbe-678f-4174-a984-a97e26b8b940.png"),

            new Book(2L, "Cosmic Horizons", "Marcus Zhang", LocalDate.of(2022, 11, 3),
                    Arrays.asList("sci-fi", "fiction"),
                    "Humanity's first interstellar mission encounters an ancient alien civilization.",
                    "https://storage.googleapis.com/workspace-0f70711f-8b4e-4d94-86f1-2a93ccde5887/image/15d43165-5405-4d4c-a38a-b72e2d46fe77.png"),

            new Book(3L, "Empire's Fall", "Sarah Johnson", LocalDate.of(2021, 9, 22),
                    Arrays.asList("history", "biography"),
                    "The definitive account of the Roman Empire's decline.",
                    "https://storage.googleapis.com/workspace-0f70711f-8b4e-4d94-86f1-2a93ccde5887/image/eba30d05-cf86-4c81-b31a-d59c67269bce.png"),

            new Book(4L, "Whispers of Magic", "Aisha Patel", LocalDate.of(2023, 3, 8),
                    Arrays.asList("fantasy", "fiction"),
                    "In a world where magic is fading, a young apprentice discovers ancient powers.",
                    "https://storage.googleapis.com/workspace-0f70711f-8b4e-4d94-86f1-2a93ccde5887/image/4f4ca5ff-ba2e-4f68-bba1-502ab7e70076.png"),

            new Book(5L, "The Midnight Library", "Nora Roberts", LocalDate.of(2023, 1, 14),
                    Arrays.asList("fiction", "mystery"),
                    "Between life and death there is a library with endless possibilities.",
                    "https://storage.googleapis.com/workspace-0f70711f-8b4e-4d94-86f1-2a93ccde5887/image/b82c332b-5568-415f-9458-b50717be287f.png"),

            new Book(6L, "Quantum Realms", "Dr. Alan Turing", LocalDate.of(2022, 7, 30),
                    Arrays.asList("sci-fi", "fiction"),
                    "A groundbreaking exploration of quantum computing and its potential.",
                    "https://storage.googleapis.com/workspace-0f70711f-8b4e-4d94-86f1-2a93ccde5887/image/0b538c8f-1f89-4f3a-869c-184c0ecc495c.png"),

            new Book(7L, "Ancient Wisdom", "Li Chen", LocalDate.of(2021, 12, 5),
                    Arrays.asList("history", "biography"),
                    "Rediscover the lost knowledge of ancient civilizations.",
                    "https://storage.googleapis.com/workspace-0f70711f-8b4e-4d94-86f1-2a93ccde5887/image/d7219e03-d549-4826-86a2-88c911b07f70.png"),

            new Book(8L, "Dragon's Legacy", "Kaelen Stormrider", LocalDate.of(2023, 8, 19),
                    Arrays.asList("fantasy", "fiction"),
                    "The epic conclusion to the Dragon Rider trilogy.",
                    "https://storage.googleapis.com/workspace-0f70711f-8b4e-4d94-86f1-2a93ccde5887/image/8bb084c4-5ff8-4fea-839e-925f69e5ee11.png")
    );

    public List<Book> getAllBooks() {
        return books;
    }

    public Optional<Book> getBookById(Long id) {
        return books.stream()
                .filter(book -> book.getId().equals(id))
                .findFirst();
    }
}
