package com.backend.demo.dto.response;

import lombok.*;

import java.util.Set;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private boolean activo;
    private Set<String> roles;
}