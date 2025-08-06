package com.MADA.mada_SeoulBike.global.config;


import com.MADA.mada_SeoulBike.user.entity.User;
import com.MADA.mada_SeoulBike.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DummyDataConfig {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initAdmin() {
        return args -> {
            if (userRepository.findByEmail("admin@mada.com").isEmpty()) {
                User admin = User.builder()
                        .email("admin@mada.com")
                        .password(passwordEncoder.encode("admin1234!"))
                        .name("관리자")
                        .role("ADMIN")
                        .build();
                userRepository.save(admin);
                System.out.println("어드민 더미 생성됨: admin@mada.com / admin1234!");
            }
        };
    }
}
