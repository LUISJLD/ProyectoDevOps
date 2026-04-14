package com.backend.demo.service;

import com.backend.demo.dto.request.RegisterRequest;
import com.backend.demo.dto.response.UserResponse;
import com.backend.demo.model.enums.ERole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Set;



public interface IUserService {

    UserResponse register(RegisterRequest request);

    Page<UserResponse> getAllUsers(String nombre, String apellido, Pageable pageable);

    UserResponse getUserById(Long id);

    void deleteUser(Long id);

    UserResponse activateUser(Long id);

    UserResponse deactivateUser(Long id);

    UserResponse assignRoles(Long id, Set<String> roles);
}