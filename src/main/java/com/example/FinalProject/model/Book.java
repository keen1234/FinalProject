package com.example.FinalProject.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String author;
    @Column(name = "date_publish")
    private LocalDate datePublish;
    private String genre;
    private String language;
    private String description;
    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToMany(mappedBy = "borrowedBooks", fetch = FetchType.EAGER)
    private List<Student> borrowers = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "book_reservers",
        joinColumns = @JoinColumn(name = "book_id"),
        inverseJoinColumns = @JoinColumn(name = "student_id")
    )
    private List<Student> reservers = new ArrayList<>();

    private String country;
    private String time;

    public enum Status {
        available,
        reserved,
        borrowed
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public LocalDate getDatePublish() { return datePublish; }
    public void setDatePublish(LocalDate datePublish) { this.datePublish = datePublish; }
    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public List<Student> getBorrowers() {
        return borrowers;
    }
    public void setBorrowers(List<Student> borrowers) {
        this.borrowers = borrowers;
    }
    public List<Student> getReservers() {
        return reservers;
    }
    public void setReservers(List<Student> reservers) {
        this.reservers = reservers;
    }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
}
