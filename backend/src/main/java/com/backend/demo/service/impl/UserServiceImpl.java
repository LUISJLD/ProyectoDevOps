package com.backend.demo.service.impl;

import com.backend.demo.dto.request.RegisterRequest;
import com.backend.demo.dto.response.UserResponse;
import com.backend.demo.exception.EmailAlreadyExistsException;
import com.backend.demo.exception.ResourceNotFoundException;
import com.backend.demo.mapper.UserMapper;
import com.backend.demo.model.entity.Role;
import com.backend.demo.model.entity.User;
import com.backend.demo.repository.RoleRepository;
import com.backend.demo.repository.UserRepository;
import com.backend.demo.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    // REGISTRO
    @Override
    public UserResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(
                    "El correo ya está registrado: " + request.getEmail()
            );
        }

        User user = new User();
        user.setNombre(request.getNombre());
        user.setApellido(request.getApellido());
        user.setTelefono(request.getTelefono());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setActivo(true);
        user.setFechaCreacion(LocalDateTime.now());

        return userMapper.toResponse(userRepository.save(user));
    }

    // LISTAR
    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(String nombre, String apellido, Pageable pageable) {
        return userRepository.findByFilters(nombre, apellido, pageable)
                .map(userMapper::toResponse);
    }

    // BUSCAR POR ID
    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        return userMapper.toResponse(findUserById(id));
    }

    // ELIMINAR USUARIO
    @Override
    public void deleteUser(Long id) {
        userRepository.delete(findUserById(id));
    }

    // ACTIVAR USUARIO
    @Override
    public UserResponse activateUser(Long id) {
        User user = findUserById(id);
        user.setActivo(true);
        return userMapper.toResponse(userRepository.save(user));
    }

    // DESACTIVAR USUARIO
    @Override
    public UserResponse deactivateUser(Long id) {
        User user = findUserById(id);
        user.setActivo(false);
        return userMapper.toResponse(userRepository.save(user));
    }

    // ASIGNACIÓN DE ROLES
    @Override
    public UserResponse assignRoles(Long id, Set<String> roleNames) {

        User user = findUserById(id);

        Set<Role> roles = roleNames.stream()
                .map(this::mapToRole)
                .collect(Collectors.toSet());

        user.setRoles(roles);

        return userMapper.toResponse(userRepository.save(user));
    }

    
    // MÉTODOS PRIVADOS

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Usuario no encontrado con ID: " + id));
    }

    private Role mapToRole(String name) {
        try {
            var eRole = com.backend.demo.model.enums.ERole
                    .valueOf("ROLE_" + name.toUpperCase());

            return roleRepository.findByName(eRole)
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Rol no encontrado: " + name));

        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Rol no válido: " + name);
        }
    }
}