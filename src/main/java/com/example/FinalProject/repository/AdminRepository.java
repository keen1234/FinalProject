package com.example.FinalProject.repository;

import com.example.FinalProject.model.admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<admin, Long> {
    admin findByEmail(String email);

    admin findByEmailIgnoreCase(String email);
}
