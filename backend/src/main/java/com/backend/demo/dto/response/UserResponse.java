package com.backend.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private boolean activo;
    private Set<String> roles;
}