package se331.lab.rest.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import se331.lab.rest.entity.User;
import se331.lab.rest.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class InitApp implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        User admin = userRepository.findByUsername("superadmin").orElse(null);
        if (admin == null) {
            admin = User.builder()
                    .username("superadmin")
                    .password(passwordEncoder.encode("password"))
                    .firstname("Super")
                    .lastname("Admin")
                    .email("superadmin@example.com")
                    .role("ROLE_ADMIN")
                    .build();
            userRepository.save(admin);
            System.out.println("Created superadmin user");
        } else {
            admin.setPassword(passwordEncoder.encode("password"));
            admin.setRole("ROLE_ADMIN");
            userRepository.save(admin);
            System.out.println("Updated superadmin user password");
        }

        // Also fix the original adminuser if it exists
        User originalAdmin = userRepository.findByUsername("adminuser").orElse(null);
        if (originalAdmin != null) {
            originalAdmin.setPassword(passwordEncoder.encode("password"));
            originalAdmin.setRole("ROLE_ADMIN");
            userRepository.save(originalAdmin);
            System.out.println("Updated adminuser password");
        }
    }
}
