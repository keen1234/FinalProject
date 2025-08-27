package com.example.FinalProject.model;

            import java.time.LocalDate;
            import java.util.List;

            public class Book {
                private final Long id;
                private final String title;
                private final String author;
                private final LocalDate publishedDate;
                private final List<String> genres;
                private final String description;
                private final String imageUrl;

                public Book(Long id, String title, String author, LocalDate publishedDate,
                            List<String> genres, String description, String imageUrl) {
                    this.id = id;
                    this.title = title;
                    this.author = author;
                    this.publishedDate = publishedDate;
                    this.genres = genres;
                    this.description = description;
                    this.imageUrl = imageUrl;
                }

                public Long getId() { return id; }
                public String getTitle() { return title; }
                public String getAuthor() { return author; }
                public LocalDate getPublishedDate() { return publishedDate; }
                public List<String> getGenres() { return genres; }
                public String getDescription() { return description; }
                public String getImageUrl() { return imageUrl; }
            }