package com.taskmanager.config;

import com.taskmanager.entity.Role;
import com.taskmanager.entity.User;
import com.taskmanager.repository.RoleRepository;
import com.taskmanager.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataSeeder.class);

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Seed Roles
        if (roleRepository.count() == 0) {
            roleRepository.save(Role.builder().name(Role.ERole.ROLE_USER).build());
            roleRepository.save(Role.builder().name(Role.ERole.ROLE_ADMIN).build());
            logger.info("✅ Roles seeded: ROLE_USER, ROLE_ADMIN");
        }

        // Seed Admin User
        if (!userRepository.existsByUsername("admin")) {
            Role adminRole = roleRepository.findByName(Role.ERole.ROLE_ADMIN)
                    .orElseThrow(() -> new RuntimeException("Admin role not found"));
            Role userRole = roleRepository.findByName(Role.ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("User role not found"));

            Set<Role> roles = new HashSet<>();
            roles.add(adminRole);
            roles.add(userRole);

            User admin = User.builder()
                    .username("admin")
                    .email("admin@taskmanager.com")
                    .password(passwordEncoder.encode("admin123"))
                    .fullName("System Administrator")
                    .enabled(true)
                    .roles(roles)
                    .build();

            userRepository.save(admin);
            logger.info("✅ Admin user seeded: username=admin, password=admin123");
        }

        // Seed demo user
        if (!userRepository.existsByUsername("demo")) {
            Role userRole = roleRepository.findByName(Role.ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("User role not found"));

            Set<Role> roles = new HashSet<>();
            roles.add(userRole);

            User demo = User.builder()
                    .username("demo")
                    .email("demo@taskmanager.com")
                    .password(passwordEncoder.encode("demo123"))
                    .fullName("Demo User")
                    .enabled(true)
                    .roles(roles)
                    .build();

            userRepository.save(demo);
            logger.info("✅ Demo user seeded: username=demo, password=demo123");
        }
    }
}
