package com.backend.demo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DatabaseKeepAliveScheduler {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseKeepAliveScheduler.class);
    private final JdbcTemplate jdbcTemplate;

    public DatabaseKeepAliveScheduler(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Ejecutar cada 20 minutos (1200000 ms) para mantener la base de datos de Supabase activa
    @Scheduled(fixedRate = 1200000)
    public void pingDatabase() {
        try {
            jdbcTemplate.execute("SELECT 1");
            logger.info("Ping a la base de datos ejecutado correctamente para evitar suspensión.");
        } catch (Exception e) {
            logger.error("Error al ejecutar ping de base de datos", e);
        }
    }
}
