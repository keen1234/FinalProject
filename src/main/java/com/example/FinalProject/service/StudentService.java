package com.example.FinalProject.service;

import com.example.FinalProject.model.Student;
import com.example.FinalProject.model.Course;
import com.example.FinalProject.repository.StudentRepository;
import com.example.FinalProject.repository.CourseRepository;
import com.example.FinalProject.dto.StudentProfileDto;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Service
@Transactional
public class StudentService {
    private static final Logger logger = LoggerFactory.getLogger(StudentService.class);

    private final StudentRepository studentRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final CourseRepository courseRepository;

    public boolean emailExists(String email) {
        return studentRepository.findByEmail(email).isPresent();
    }

    public StudentService(StudentRepository studentRepository, CourseRepository courseRepository) {
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
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

    public Optional<Student> findById(Long id) {
        return studentRepository.findById(id);
    }

    /**
     * Update profile fields for the student with the given id.
     * Only updates allowed fields. If password is provided, it will be encoded.
     * If email is changed, caller should ensure uniqueness (or this method will throw).
     */
    public Student updateProfile(Long id, StudentProfileDto dto) {
        Student student = studentRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Student not found"));

        logger.info("Updating profile for student id={}", id);
        logger.info("Before update: email={}, firstName={}, lastName={}, address={}, number={}, course={}",
                student.getEmail(), student.getFirstName(), student.getLastName(), student.getAddress(), student.getNumber(), student.getCourse() == null ? "<none>" : student.getCourse().getCourseCode());

        // Email uniqueness check
        if (!student.getEmail().equals(dto.getEmail())) {
            if (studentRepository.findByEmail(dto.getEmail()).isPresent()) {
                throw new IllegalArgumentException("Email already exists");
            }
            student.setEmail(dto.getEmail());
        }

        student.setFirstName(dto.getFirstName());
        student.setLastName(dto.getLastName());
        student.setAddress(dto.getAddress());
        student.setNumber(dto.getNumber());

        if (dto.getCourseCode() != null && !dto.getCourseCode().isBlank()) {
            Course c = courseRepository.findByCourseCode(dto.getCourseCode());
            if (c != null) {
                student.setCourse(c);
            }
        }

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            if (!dto.getPassword().equals(dto.getConfirmPassword())) {
                throw new IllegalArgumentException("Passwords do not match");
            }
            student.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        Student saved = studentRepository.saveAndFlush(student);

        logger.info("After update: email={}, firstName={}, lastName={}, address={}, number={}, course={}",
                saved.getEmail(), saved.getFirstName(), saved.getLastName(), saved.getAddress(), saved.getNumber(), saved.getCourse() == null ? "<none>" : saved.getCourse().getCourseCode());

        return saved;
    }
}
