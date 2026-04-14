package com.backend.demo.service.impl;

import com.backend.demo.dto.auth.LoginRequest;
import com.backend.demo.dto.auth.LoginResponse;
import com.backend.demo.security.jwt.JwtUtil;
import com.backend.demo.service.IAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Override
    public LoginResponse login(LoginRequest loginRequestDTO) {
        // Autenticar usuario
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDTO.getEmail(),
                        loginRequestDTO.getPassword()
                )
        );

        // Establecer contexto de seguridad
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Extraer username del objeto Authentication y generar token
        String username = authentication.getName(); // Obtiene el email
        String token = jwtUtil.generateToken(username);

        // Retornar usando tu DTO actual
        return new LoginResponse(token);
    }

}