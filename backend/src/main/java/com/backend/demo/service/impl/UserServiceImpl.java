package com.backend.demo.service.impl;

import com.backend.demo.dto.request.RegisterRequest;
import com.backend.demo.dto.request.UpdateUserRequest;
import com.backend.demo.dto.response.UserResponse;
import com.backend.demo.model.entity.Role;
import com.backend.demo.model.entity.User;
import com.backend.demo.model.enums.ERole;
import com.backend.demo.repository.RoleRepository;
import com.backend.demo.repository.UserRepository;
import com.backend.demo.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements IUserService {

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final long LOCK_DURATION_MINUTES = 30;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    // ─────────────────────────────────────────────
    // RF01 - Registro
    // ─────────────────────────────────────────────
    @Override
    public UserResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El correo ya está registrado: " + request.getEmail());
        }

        User user = new User();
        user.setNombre(request.getNombre());
        user.setApellido(request.getApellido());
        user.setTelefono(request.getTelefono());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword()); // sin encoder por ahora
        user.setActivo(true);
        user.setFechaCreacion(LocalDateTime.now());

        Role defaultRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Rol USER no encontrado"));
        user.setRoles(Set.of(defaultRole));

        return mapToResponse(userRepository.save(user));
    }

    // ─────────────────────────────────────────────
    // RF04 - Listar, filtrar y paginar
    // ─────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(String nombre, String apellido, Pageable pageable) {
        return userRepository.findByFilters(nombre, apellido, pageable)
                .map(this::mapToResponse);
    }

    // ─────────────────────────────────────────────
    // RF05 - Gestión de usuarios
    // ─────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        return mapToResponse(user);
    }

    @Override
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));

        if (request.getNombre()   != null) user.setNombre(request.getNombre());
        if (request.getApellido() != null) user.setApellido(request.getApellido());
        if (request.getTelefono() != null) user.setTelefono(request.getTelefono());

        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("El correo ya está en uso");
            }
            user.setEmail(request.getEmail());
        }

        return mapToResponse(userRepository.save(user));
    }

    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        userRepository.delete(user);
    }

    @Override
    public UserResponse activateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        user.setActivo(true);
        return mapToResponse(userRepository.save(user));
    }

    @Override
    public UserResponse deactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        user.setActivo(false);
        return mapToResponse(userRepository.save(user));
    }

    // ─────────────────────────────────────────────
    // RF06 - Asignar roles
    // ─────────────────────────────────────────────
    @Override
    public UserResponse assignRoles(Long id, Set<String> roleNames) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));

        Set<Role> roles = new HashSet<>();
        for (String name : roleNames) {
            ERole eRole;
            try {
                eRole = ERole.valueOf("ROLE_" + name.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Rol no válido: " + name);
            }
            Role role = roleRepository.findByName(eRole)
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + name));
            roles.add(role);
        }

        user.setRoles(roles);
        return mapToResponse(userRepository.save(user));
    }

    // ─────────────────────────────────────────────
    // RF07 - Validar correo único
    // ─────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    // ─────────────────────────────────────────────
    // RF08 - Bloqueo por intentos fallidos
    // ─────────────────────────────────────────────
    @Override
    public void registerFailedAttempt(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            int attempts = user.getFailedAttempts() + 1;
            user.setFailedAttempts(attempts);
            if (attempts >= MAX_FAILED_ATTEMPTS) {
                user.setLocked(true);
                user.setLockTime(LocalDateTime.now());
            }
            userRepository.save(user);
        });
    }

    @Override
    public void unlockAccount(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            user.setLocked(false);
            user.setFailedAttempts(0);
            user.setLockTime(null);
            userRepository.save(user);
        });
    }

    // ─────────────────────────────────────────────
    // Método de mapeo
    // ─────────────────────────────────────────────
    private UserResponse mapToResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setNombre(user.getNombre());
        response.setApellido(user.getApellido());
        response.setEmail(user.getEmail());
        response.setTelefono(user.getTelefono());
        response.setActivo(user.isActivo());
        response.setRoles(
                user.getRoles().stream()
                        .map(r -> r.getName().name())
                        .collect(Collectors.toSet())
        );
        return response;
    }
}