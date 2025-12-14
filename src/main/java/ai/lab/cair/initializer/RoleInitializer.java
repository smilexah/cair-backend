package ai.lab.cair.initializer;

import ai.lab.cair.entity.Role;
import ai.lab.cair.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(1)
public class RoleInitializer implements CommandLineRunner {
    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        initializeRoles();
    }

    private void initializeRoles() {
        createRoleIfNotExists("ADMIN");
        createRoleIfNotExists("USER");
    }

    private void createRoleIfNotExists(String roleName) {
        roleRepository.findByName(roleName)
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName(roleName);
                    Role savedRole = roleRepository.save(role);
                    log.info("Created {} role with ID: {}", roleName, savedRole.getId());
                    return savedRole;
                });
    }

    public Role getAdminRole() {
        return roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new RuntimeException("ADMIN role not found"));
    }

    public Role getUserRole() {
        return roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("USER role not found"));
    }
}
