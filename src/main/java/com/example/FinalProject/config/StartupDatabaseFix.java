package com.example.FinalProject.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class StartupDatabaseFix implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(StartupDatabaseFix.class);

    private final JdbcTemplate jdbcTemplate;

    public StartupDatabaseFix(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) throws Exception {
        try {
            logger.info("Checking admin.id column for AUTO_INCREMENT...");
            String extra = jdbcTemplate.queryForObject(
                "SELECT EXTRA FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'admin' AND COLUMN_NAME = 'id'",
                String.class
            );

            if (extra == null || !extra.toLowerCase().contains("auto_increment")) {
                logger.warn("admin.id is not AUTO_INCREMENT, attempting to modify the column to add AUTO_INCREMENT");
                try {
                    jdbcTemplate.execute("ALTER TABLE admin MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT");
                    logger.info("Successfully altered admin.id to AUTO_INCREMENT");
                } catch (Exception e) {
                    logger.error("Failed to ALTER admin.id to AUTO_INCREMENT", e);
                }
            } else {
                logger.info("admin.id already has AUTO_INCREMENT");
            }
        } catch (Exception e) {
            logger.warn("Could not check/modify admin.id AUTO_INCREMENT property. This may be because INFORMATION_SCHEMA is inaccessible or the database does not exist yet.", e);
        }
    }
}

