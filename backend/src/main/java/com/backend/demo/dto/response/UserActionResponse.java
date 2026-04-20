package com.backend.demo.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserActionResponse {
    private Long id;
    private String tipo;
    private String descripcion;
    private LocalDateTime fecha;

}