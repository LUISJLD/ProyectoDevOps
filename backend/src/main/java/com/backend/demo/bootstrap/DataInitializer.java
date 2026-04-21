package com.backend.demo.bootstrap;

import com.backend.demo.model.entity.Role;
import com.backend.demo.model.entity.User;
import com.backend.demo.model.enums.ERole;
import com.backend.demo.repository.RoleRepository;
import com.backend.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        log.info("===============================================");
        log.info("INICIANDO CARGA DE DATOS DE PRUEBA");
        log.info("===============================================");

        try {
            initializeRoles();
            initializeTestUsers();

            log.info("===============================================");
            log.info("✓ DATOS DE PRUEBA INICIALIZADOS");
            log.info("===============================================");
            logCredentials();

        } catch (Exception e) {
            log.error("✗ Error durante inicialización: ", e);
            throw new RuntimeException("Error inicializando datos de prueba", e);
        }
    }

    private void initializeRoles() {
        log.info(">>> Verificando roles en base de datos...");

        for (ERole eRole : ERole.values()) {
            if (roleRepository.findByName(eRole).isEmpty()) {
                Role role = Role.builder()
                        .name(eRole)
                        .build();

                roleRepository.save(role);
                log.info("   ✓ Rol creado: {}", eRole.name());
            } else {
                log.info("   ✓ Rol ya existe: {}", eRole.name());
            }
        }
    }

    private void initializeTestUsers() {
        log.info(">>> Verificando usuarios de prueba...");

        TestDataSeeder[] testUsers = TestDataSeeder.getTestUsers();

        for (TestDataSeeder testData : testUsers) {
            if (userRepository.existsByEmail(testData.getEmail())) {
                log.info("   ✓ Usuario ya existe: {}", testData.getEmail());
            } else {
                String encodedPassword = passwordEncoder.encode(testData.getPassword());
                User newUser = testData.toUserEntity(encodedPassword);

                Role role = roleRepository.findByName(testData.getRole())
                        .orElseThrow(() -> new RuntimeException(
                                "Rol no encontrado: " + testData.getRole().name()
                        ));

                newUser.getRoles().add(role);
                userRepository.save(newUser);
                log.info("   ✓ Usuario creado: {} (Rol: {})", 
                        testData.getEmail(), testData.getRole().name());
            }
        }
    }

    private void logCredentials() {
        log.info("");
        log.info("================================================");
        log.info("  USUARIOS DE PRUEBA DISPONIBLES");
        log.info("================================================");

        TestDataSeeder[] testUsers = TestDataSeeder.getTestUsers();
        for (TestDataSeeder user : testUsers) {
            log.info("");
            log.info("  Email    : {}", user.getEmail());
            log.info("  Password : {}", user.getPassword());
            log.info("  Rol      : {}", user.getRole().name());
        }

        log.info("");
        log.info("================================================");
        log.info("  Endpoint: POST /api/auth/login");
        log.info("  Body: {\"email\": \"...\", \"password\": \"...\"}");
        log.info("================================================");
        log.info("");
    }
}