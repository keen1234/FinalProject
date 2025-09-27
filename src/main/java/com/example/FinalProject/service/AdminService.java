package com.example.FinalProject.service;

import com.example.FinalProject.model.admin;
import com.example.FinalProject.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminService {
    @Autowired
    private AdminRepository adminRepository;

    public admin getAdminByEmail(String email) {
        String checkedEmail = email == null ? "" : email.trim().toLowerCase();
        System.out.println("[AdminService] Querying for email: '" + checkedEmail + "'");
        // Print all admin emails in DB for debugging
        System.out.println("[AdminService] All admins in DB:");
        for (admin a : adminRepository.findAll()) {
            System.out.println("[AdminService] DB admin email: '" + a.getEmail().toLowerCase().trim() + "'");
        }
        admin result = adminRepository.findByEmailIgnoreCase(checkedEmail);
        System.out.println("[AdminService] Query result for email='" + checkedEmail + "': " + (result != null ? result.getEmail() : "null"));
        if (result == null) {
            System.out.println("[AdminService] No admin found for email='" + checkedEmail + "'.");
        } else {
            System.out.println("[AdminService] Found admin: email='" + result.getEmail() + "', password='" + result.getPassword() + "', status='" + result.getStatus() + "'.");
        }
        return result;
    }

    public admin findByEmailAndPassword(String email, String password) {
        if (email == null || password == null) return null;
        admin found = adminRepository.findByEmailIgnoreCase(email.trim().toLowerCase());
        if (found != null && found.getPassword().equals(password)) {
            return found;
        }
        return null;
    }

    public admin save(admin adminObj) {
        return adminRepository.save(adminObj);
    }

}
