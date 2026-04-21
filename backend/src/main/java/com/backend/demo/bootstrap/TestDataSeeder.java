package com.backend.demo.bootstrap;

import com.backend.demo.model.entity.Role;
import com.backend.demo.model.entity.User;
import com.backend.demo.model.enums.ERole;
import lombok.Builder;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

/**
 * Encapsula los datos de prueba para inicializar usuarios de test.
 * Define credenciales realistas y fáciles de recordar para desarrollo.
 */
@Getter
@Builder
public class TestDataSeeder {
    private final String nombre;
    private final String apellido;
    private final String email;
    private final String telefono;
    private final String password;
    private final ERole role;

    /**
     * Crea usuarios de prueba predefinidos.
     * @return Array con 3 usuarios de test
     */
    public static TestDataSeeder[] getTestUsers() {
        return new TestDataSeeder[]{
                TestDataSeeder.builder()
                        .nombre("Admin")
                        .apellido("Test")
                        .email("admin@test.com")
                        .telefono("3001234567")
                        .password("Admin@123456")
                        .role(ERole.ROLE_ADMIN)
                        .build(),

                TestDataSeeder.builder()
                        .nombre("Usuario")
                        .apellido("Test")
                        .email("usuario@test.com")
                        .telefono("3001234568")
                        .password("Usuario@123456")
                        .role(ERole.ROLE_USER)
                        .build(),

                TestDataSeeder.builder()
                        .nombre("Moderador")
                        .apellido("Test")
                        .email("moderador@test.com")
                        .telefono("3001234569")
                        .password("Moderador@123456")
                        .role(ERole.ROLE_MODERATOR)
                        .build()
        };
    }

    /**
     * Convierte los datos de test a una entidad User.
     * @param encodedPassword Contraseña ya hasheada por PasswordEncoder
     * @return Entidad User lista para persistir
     */
    public User toUserEntity(String encodedPassword) {
        return User.builder()
                .nombre(this.nombre)
                .apellido(this.apellido)
                .email(this.email)
                .telefono(this.telefono)
                .password(encodedPassword)
                .activo(true)
                .locked(false)
                .failedAttempts(0)
                .roles(new HashSet<>())
                .build();
    }
}
