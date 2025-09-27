package com.example.FinalProject.service;

import com.example.FinalProject.model.admin;
import com.example.FinalProject.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AdminDetailsService implements UserDetailsService {
    @Autowired
    private AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        admin adminObj = adminRepository.findByEmailIgnoreCase(email.trim().toLowerCase());
        if (adminObj == null) {
            throw new UsernameNotFoundException("Admin not found with email: " + email);
        }
        return new AdminDetails(adminObj);
    }
}

