package com.example.FinalProject.model;

import jakarta.persistence.*;

@Entity
@Table(name = "courses")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long courseId;

    @Column(name = "course_code", nullable = false)
    private String courseCode;

    @Column(name = "category", nullable = false)
    private String category;

    // Getters and setters
    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }
    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}
