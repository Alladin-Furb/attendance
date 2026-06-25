package com.transportation.presenca;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PresencaServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(PresencaServiceApplication.class, args);
    }
}
