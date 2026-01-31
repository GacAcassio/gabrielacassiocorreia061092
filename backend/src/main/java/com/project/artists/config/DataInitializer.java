package com.project.artists.config;

import com.project.artists.entity.User;
import com.project.artists.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

/**
 *  Inicializador default admin user 
 */
@Configuration
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        String username = "admin";
        String email = "admin@artists.com";
        String rawPassword = "admin123";

        // INsere admin caso nao exista
        if (userRepository.findByUsername(username).isEmpty()) {
            User admin = new User();
            admin.setUsername(username);
            admin.setEmail(email);
            admin.setPassword(passwordEncoder.encode(rawPassword));
    
            userRepository.save(admin);
            System.out.println("Default admin user criado: " + username);
        } else {
            System.out.println("ℹAdmin user já existe: " + username);
        }
    }
}
