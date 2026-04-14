// model/entity/User.java
package com.backend.demo.model.entity;

import com.backend.demo.model.enums.ERole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String apellido;
    private String telefono;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;
    private boolean activo = true;
    private boolean locked = false;
    private int failedAttempts = 0;
    private LocalDateTime lockTime;
    private LocalDateTime fechaCreacion;
    private String resetToken;
    private LocalDateTime resetTokenExpiry;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();
}