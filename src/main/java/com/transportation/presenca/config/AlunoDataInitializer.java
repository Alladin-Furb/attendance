package com.transportation.presenca.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class AlunoDataInitializer implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;

    public AlunoDataInitializer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        jdbcTemplate.update(
                "UPDATE alunos SET external_id = id WHERE external_id IS NULL");
    }
}
