package com.backend.demo.controller;

import com.backend.demo.dto.passwordreset.ForgotPasswordRequest;
import com.backend.demo.dto.passwordreset.ResetPasswordRequest;
import com.backend.demo.service.IPasswordResetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/password")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
public class PasswordResetController {

    private final IPasswordResetService passwordResetService;

    @PostMapping("/forgot")
    public ResponseEntity<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        try {
            passwordResetService.forgotPassword(request.getEmail());
            return ResponseEntity.ok().build();
        } catch (RuntimeException ex) {
            // Puedes decidir el status según mensaje; por ahora algo simple:
            // - Si el usuario no existe: 404
            // - Otros errores: 400
            if (ex.getMessage() != null && ex.getMessage().toLowerCase().contains("no encontrado")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/reset")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        try {
            passwordResetService.resetPassword(request.getToken(), request.getNewPassword());
            return ResponseEntity.ok().build();
        } catch (RuntimeException ex) {
            // Token inválido / usado / expirado: 400
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
