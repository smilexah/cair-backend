//package ai.lab.cair.initializer;
//
//import ai.lab.cair.entity.Role;
//import ai.lab.cair.entity.User;
//import ai.lab.cair.repository.UserRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Value;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.core.annotation.Order;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.stereotype.Component;
//
//import java.time.LocalDateTime;
//import java.util.Set;
//
//@Component
//@RequiredArgsConstructor
//@Slf4j
//@Order(2)
//public class AdminInitializer implements CommandLineRunner {
//    private final UserRepository userRepository;
//    private final RoleInitializer roleInitializer;
//    private final BCryptPasswordEncoder passwordEncoder;
//
//    @Value("${app.admin.username}")
//    private String adminUsername;
//
//    @Value("${app.admin.password}")
//    private String adminPassword;
//
//    @Override
//    public void run(String... args) {
//        initializeAdminUser();
//    }
//
//    private void initializeAdminUser() {
//        if (userRepository.existsByUsername(adminUsername)) {
//            log.info("Admin user '{}' already exists", adminUsername);
//            return;
//        }
//
//        Role adminRole = roleInitializer.getAdminRole();
//
//        User adminUser = User.builder()
//                .username(adminUsername)
//                .password(passwordEncoder.encode(adminPassword))
//                .isActive(true)
//                .createdAt(LocalDateTime.now())
//                .roles(Set.of(adminRole))
//                .build();
//
//        userRepository.save(adminUser);
//        log.info("Admin '{}' created successfully", adminUsername);
//    }
//}
