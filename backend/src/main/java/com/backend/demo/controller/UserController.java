package com.backend.demo.controller;

import com.backend.demo.dto.request.AssignRolesRequest;
import com.backend.demo.dto.request.RegisterRequest;
import com.backend.demo.dto.request.UpdateUserRequest;
import com.backend.demo.dto.response.UserResponse;
import com.backend.demo.model.enums.ERole;
import com.backend.demo.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.backend.demo.dto.response.UserActionResponse;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    final private IUserService userService;

    @Operation(
            summary = "Registrar nuevo usuario (US01)",
            description = "Crea una cuenta con nombre, apellido, teléfono, email y contraseña cifrada con BCrypt"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Campos inválidos o faltantes"),
            @ApiResponse(responseCode = "409", description = "El email ya está registrado")
    })
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.register(request));
    }

    @GetMapping
    public ResponseEntity<Page<UserResponse>> getAll(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String apellido,
            Pageable pageable) {
        return ResponseEntity.ok(userService.getAllUsers(nombre, apellido, pageable));
    }

    @GetMapping("/filter")
    public ResponseEntity<Page<UserResponse>> getAllWithRol(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) ERole rol,
            Pageable pageable) {
        return ResponseEntity.ok(userService.getAllUsers(nombre, rol != null ? rol.name() : null, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<UserResponse> activate(@PathVariable Long id) {
        return ResponseEntity.ok(userService.activateUser(id));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<UserResponse> deactivate(@PathVariable Long id) {
        return ResponseEntity.ok(userService.deactivateUser(id));
    }

    @PutMapping("/{id}/roles")
    public ResponseEntity<UserResponse> assignRoles(
            @PathVariable Long id,
            @RequestBody AssignRolesRequest request) {
        return ResponseEntity.ok(userService.assignRoles(id, request.getRoles()));
    }



}