package com.backend.demo.dto.auth;

import java.util.List;

public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String nombreUsuario;
    private String email;
    private List<String> roles;
}