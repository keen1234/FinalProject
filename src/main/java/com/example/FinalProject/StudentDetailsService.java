package com.example.FinalProject;

import com.example.FinalProject.model.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class StudentDetailsService implements UserDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(StudentDetailsService.class);
    @Autowired
    private StudentRepository studentRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Student student = studentRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Student not found with email: " + email));
        logger.info("Loaded student for login: {} with password: {}", email, student.getPassword());
        return new StudentDetails(student);
    }
}
