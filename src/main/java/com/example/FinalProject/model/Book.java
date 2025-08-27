package com.example.FinalProject.model;

            import java.time.LocalDate;
            import java.util.List;

            public class Book {
                private Long id;
                private String title;
                private String author;
                private LocalDate publishedDate;
                private List<String> genres;
                private String description;
                private String imageUrl;

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