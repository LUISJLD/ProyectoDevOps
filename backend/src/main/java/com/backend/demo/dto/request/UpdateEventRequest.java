package com.backend.demo.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateEventRequest {

    @Size(max = 150, message = "El nombre no puede superar los 150 caracteres")
    private String nombre;

    @Size(max = 1000, message = "La descripción no puede superar los 1000 caracteres")
    private String descripcion;

    private LocalDate fecha;

    private LocalTime hora;

    @Size(max = 255, message = "La ubicación no puede superar los 255 caracteres")
    private String ubicacion;

    @Min(value = 1, message = "La capacidad debe ser mayor a 0")
    private Integer capacidadMaxima;

    private Boolean parkingAvailable;

    @Min(value = 0, message = "Los cupos de parqueadero no pueden ser negativos")
    private Integer parkingSpots;
}