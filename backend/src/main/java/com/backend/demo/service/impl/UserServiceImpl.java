package com.backend.demo.service.impl;

import com.backend.demo.dto.request.RegisterRequest;
import com.backend.demo.dto.request.UpdateUserRequest;
import com.backend.demo.dto.response.UserActionResponse;
import com.backend.demo.dto.response.UserResponse;
import com.backend.demo.exception.ResourceNotFoundException;
import com.backend.demo.model.entity.Role;
import com.backend.demo.model.entity.User;
import com.backend.demo.repository.RoleRepository;
import com.backend.demo.repository.UserActionRepository;
import com.backend.demo.repository.UserRepository;
import com.backend.demo.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private static final int MAX_FAILED_ATTEMPTS = 5;

    @Autowired private UserRepository userRepository;
    @Autowired private RoleRepository roleRepository;
    @Autowired private UserActionRepository userActionRepository;

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
        user.setPassword(request.getPassword());
        user.setActivo(true);
        user.setFechaCreacion(LocalDateTime.now());

        Role defaultRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Rol USER no encontrado"));
        user.setRoles(Set.of(defaultRole));

        User saved = userRepository.save(user);
        registrarAccion(saved, "REGISTRO", "Usuario registrado en el sistema");
        return mapToResponse(saved);
    }

    // LISTAR

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(String nombre, String apellido, Pageable pageable) {
        return userRepository.findByFilters(nombre, apellido, pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(String nombre, ERole rol, Pageable pageable) {
        return userRepository.findByNombreAndRol(nombre, rol, pageable)
                .map(this::mapToResponse);
    }


    // BUSCAR POR ID

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        return mapToResponse(findUserById(id));
    }

    // ACTUALIZAR USUARIO
    @Override
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        User user = findUserById(id);

        if (request.getNombre() != null && !request.getNombre().isBlank()) {
            user.setNombre(request.getNombre());
        }
        if (request.getApellido() != null && !request.getApellido().isBlank()) {
            user.setApellido(request.getApellido());
        }
        if (request.getTelefono() != null && !request.getTelefono().isBlank()) {
            user.setTelefono(request.getTelefono());
        }
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            if (!user.getEmail().equals(request.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("El correo ya está registrado: " + request.getEmail());
            }
            user.setEmail(request.getEmail());
        }

        return mapToResponse(userRepository.save(user));
    }

    // ELIMINAR USUARIO
    @Override
    public void deleteUser(Long id) {
        User user = findUserById(id);
        userRepository.delete(user);
    }

    // ACTIVAR USUARIO
    @Override
    public UserResponse activateUser(Long id) {
        User user = findUserById(id);
        user.setActivo(true);
        return mapToResponse(userRepository.save(user));
    }

    // DESACTIVAR USUARIO
    @Override
    public UserResponse deactivateUser(Long id) {
        User user = findUserById(id);
        user.setActivo(false);
        return mapToResponse(userRepository.save(user));
    }

    // ASIGNACIÓN DE ROLES
    @Override
    public UserResponse assignRoles(Long id, Set<String> roleNames) {

        User updated = userRepository.save(user);
        registrarAccion(updated, "ACTUALIZAR", "Datos del usuario actualizados");
        return mapToResponse(updated);
    }

    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        registrarAccion(user, "ELIMINAR", "Usuario eliminado del sistema");
        userRepository.delete(user);
    }

    @Override
    public UserResponse activateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        user.setActivo(true);
        User updated = userRepository.save(user);
        registrarAccion(updated, "ACTIVAR", "Cuenta de usuario activada");
        return mapToResponse(updated);
    }

    @Override
    public UserResponse deactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        user.setActivo(false);
        User updated = userRepository.save(user);
        registrarAccion(updated, "DESACTIVAR", "Cuenta de usuario desactivada");
        return mapToResponse(updated);
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
        User updated = userRepository.save(user);
        registrarAccion(updated, "ASIGNAR_ROLES", "Roles asignados: " + roleNames);
        return mapToResponse(updated);
    }

        return mapToResponse(userRepository.save(user));
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
                registrarAccion(user, "BLOQUEAR", "Cuenta bloqueada por " + attempts + " intentos fallidos");
            } else {
                registrarAccion(user, "INTENTO_FALLIDO", "Intento fallido " + attempts + " de " + MAX_FAILED_ATTEMPTS);
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
            registrarAccion(user, "DESBLOQUEAR", "Cuenta desbloqueada");
        });
    }

    // ─────────────────────────────────────────────
    // RF09 - Historial de acciones
    // ─────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public List<UserActionResponse> getUserActionHistory(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("Usuario no encontrado con ID: " + userId);
        }
        return userActionRepository.findByUserIdOrderByFechaDesc(userId)
                .stream()
                .map(this::mapActionToResponse)
                .collect(Collectors.toList());
    }

    // ─────────────────────────────────────────────
    // Métodos privados
    // ─────────────────────────────────────────────
    private void registrarAccion(User user, String tipo, String descripcion) {
        UserAction action = new UserAction();
        action.setUser(user);
        action.setTipo(tipo);
        action.setDescripcion(descripcion);
        action.setFecha(LocalDateTime.now());
        userActionRepository.save(action);
    }

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
                        .collect(java.util.stream.Collectors.toSet())
        );
        return response;
    }

    private UserActionResponse mapActionToResponse(UserAction action) {
        UserActionResponse response = new UserActionResponse();
        response.setId(action.getId());
        response.setTipo(action.getTipo());
        response.setDescripcion(action.getDescripcion());
        response.setFecha(action.getFecha());
        return response;
    }
}