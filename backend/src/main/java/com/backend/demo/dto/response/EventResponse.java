package com.backend.demo.dto.response;

import com.backend.demo.model.enums.EventStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventResponse {

    private Long id;
    private String nombre;
    private String descripcion;
    private LocalDate fecha;
    private LocalTime hora;
    private String ubicacion;
    private EventStatus estado;
    private Integer capacidadMaxima;
    private boolean parkingAvailable;
    private Integer parkingSpots;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long createdById;
    private String createdByNombre;
    private String createdByApellido;
}