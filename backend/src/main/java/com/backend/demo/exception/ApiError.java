package com.backend.demo.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@AllArgsConstructor
@Builder
public class ApiError {
    private LocalDateTime timestamp; // Fecha y hora del error
    private int status; //Código HTTP del error (por ejemplo, 400, 404, 500...).
    private String message;
    private Map<String, String> errors; // Usado para errores de validación, puede ser null
}