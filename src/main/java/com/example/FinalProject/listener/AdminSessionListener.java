package com.example.FinalProject.listener;

import com.example.FinalProject.model.admin;
import com.example.FinalProject.service.AdminService;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.WebApplicationContextUtils;

@Component
public class AdminSessionListener implements HttpSessionListener {
    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        Object adminObj = se.getSession().getAttribute("admin");
        if (adminObj instanceof admin) {
            admin adminUser = (admin) adminObj;
            adminUser.setStatus("offline");
            AdminService adminService = WebApplicationContextUtils
                .getWebApplicationContext(se.getSession().getServletContext())
                .getBean(AdminService.class);
            adminService.save(adminUser);
        }
    }
}

