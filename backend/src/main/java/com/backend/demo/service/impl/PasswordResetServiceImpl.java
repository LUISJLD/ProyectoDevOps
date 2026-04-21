package com.backend.demo.service.impl;

import com.backend.demo.model.entity.PasswordResetToken;
import com.backend.demo.repository.PasswordResetTokenRepository;
import com.backend.demo.repository.UserRepository;
import com.backend.demo.service.IPasswordResetService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements IPasswordResetService {

    private final PasswordResetTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void forgotPassword(String email) {

        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // tokenRepository.deleteByUser(user);

        String token = UUID.randomUUID().toString();

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiresAt(LocalDateTime.now().plusMinutes(30));
        resetToken.setUsed(false);
        resetToken.setCreatedAt(LocalDateTime.now());

        tokenRepository.save(resetToken);

        String link = "http://localhost:3000/reset-password?token=" + token;

        //  imprimir en consola
        System.out.println("=================================");
        System.out.println(" RESET PASSWORD LINK:");
        System.out.println(link);
        System.out.println("=================================");
    }

    @Override
    public void resetPassword(String token, String newPassword) {

        var resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token inválido"));

        if (resetToken.isUsed()) {
            throw new RuntimeException("Token ya usado");
        }

        if (resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token expirado");
        }

        var user = resetToken.getUser();

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // marcar como usado
        resetToken.setUsed(true);
        tokenRepository.save(resetToken);
    }
}