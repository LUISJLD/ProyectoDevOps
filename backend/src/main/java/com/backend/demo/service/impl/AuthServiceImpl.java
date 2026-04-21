package com.backend.demo.service.impl;

import com.backend.demo.dto.auth.LoginRequest;
import com.backend.demo.dto.auth.LoginResponse;
import com.backend.demo.security.jwt.JwtUtil;
import com.backend.demo.service.IAuthService;
import com.backend.demo.model.entity.User;
import com.backend.demo.repository.UserRepository;
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
    private final UserRepository userRepository;
    private static final int MAX_FAILED_ATTEMPTS = 3;

    @Override
    public LoginResponse login(LoginRequest loginRequestDTO) {

        User user = userRepository.findByEmail(loginRequestDTO.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Si está bloqueado
        if (user.isLocked()) {
            throw new RuntimeException("Usuario bloqueado");
        }

        try {
            // Intentar login
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequestDTO.getEmail(),
                            loginRequestDTO.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Login correcto → resetear intentos
            user.setFailedAttempts(0);
            userRepository.save(user);

            String token = jwtUtil.generateToken(authentication.getName());

            return new LoginResponse(token);

        } catch (Exception e) {

            //Login fallido → aumentar intentos
            int attempts = user.getFailedAttempts() + 1;
            user.setFailedAttempts(attempts);

            //Si llega al límite → bloquear
            if (attempts >= MAX_FAILED_ATTEMPTS) {
                user.setLocked(true);
            }

            userRepository.save(user);

            throw new RuntimeException("Credenciales incorrectas");
        }
    }

}