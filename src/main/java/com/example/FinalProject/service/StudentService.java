package com.example.FinalProject.service;

import com.example.FinalProject.model.Student;
import com.example.FinalProject.repository.StudentRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StudentService {
    private final StudentRepository studentRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public boolean emailExists(String email) {
        return studentRepository.findByEmail(email).isPresent();
    }

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student saveStudent(Student student) {
        if (studentRepository.findByEmail(student.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }
        student.setPassword(passwordEncoder.encode(student.getPassword()));
        return studentRepository.save(student);
    }

    public boolean authenticate(String email, String rawPassword) {
        Optional<Student> student = studentRepository.findByEmail(email);
        if (student.isPresent()) {
            return passwordEncoder.matches(rawPassword, student.get().getPassword());
        }
        return false;
    }

}
