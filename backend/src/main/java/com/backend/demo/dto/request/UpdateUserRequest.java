package com.backend.demo.dto.request;

import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserRequest {
    private String nombre;
    private String apellido;
    private String telefono;
    private String email;
}