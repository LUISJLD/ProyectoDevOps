package com.backend.demo.service.impl;

import com.backend.demo.dto.request.RegisterRequest;
import com.backend.demo.dto.request.UpdateUserRequest;
import com.backend.demo.dto.response.UserResponse;
import com.backend.demo.exception.ResourceNotFoundException;
import com.backend.demo.model.entity.Role;
import com.backend.demo.model.entity.User;
import com.backend.demo.repository.RoleRepository;
import com.backend.demo.repository.UserRepository;
import com.backend.demo.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;


    // REGISTRO
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

        return mapToResponse(userRepository.save(user));
    }

    // LISTAR

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(String nombre, String apellido, Pageable pageable) {
        return userRepository.findByFilters(nombre, apellido, pageable)
                .map(this::mapToResponse);
    }


    // BUSCAR POR ID

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        return mapToResponse(findUserById(id));
    }

    // ASIGNACIÓN DE ROLES
    @Override
    public UserResponse assignRoles(Long id, Set<String> roleNames) {

        User user = findUserById(id);

        Set<Role> roles = roleNames.stream()
                .map(name -> {
                    try {
                        com.backend.demo.model.enums.ERole eRole =
                                com.backend.demo.model.enums.ERole.valueOf("ROLE_" + name.toUpperCase());

                        return roleRepository.findByName(eRole)
                                .orElseThrow(() ->
                                        new RuntimeException("Rol no encontrado: " + name));

                    } catch (IllegalArgumentException e) {
                        throw new RuntimeException("Rol no válido: " + name);
                    }
                })
                .collect(java.util.stream.Collectors.toSet());

        user.setRoles(roles);

        return mapToResponse(userRepository.save(user));
    }
    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Usuario no encontrado con ID: " + id));
    }


    // MAPPER
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
}