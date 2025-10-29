package com.smartevents.event_registration_system.config;

import com.smartevents.event_registration_system.entity.User;
import com.smartevents.event_registration_system.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Create admin user if not exists
        if (userRepository.findByUsername("admin").isEmpty()) {
            try {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setEmail("admin@smartevent.com");
                admin.setFirstName("System");
                admin.setLastName("Administrator");
                admin.setFullName("System Administrator");
                admin.setContactNumber("111-111-1111"); // Unique contact number
                admin.setRole("ADMIN");
                admin.setLatitude(0.0);
                admin.setLongitude(0.0);
                
                userRepository.save(admin);
                System.out.println("✅ Admin user created successfully!");
            } catch (Exception e) {
                System.out.println("❌ Error creating admin user: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("✅ Admin user already exists");
        }
    }
}