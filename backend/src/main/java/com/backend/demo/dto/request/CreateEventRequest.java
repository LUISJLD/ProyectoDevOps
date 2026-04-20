package com.backend.demo.dto.request;

import com.backend.demo.model.enums.EventStatus;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateEventRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    private String nombre;

    @Size(max = 500, message = "La descripción no puede superar 500 caracteres")
    private String descripcion;

    @NotNull(message = "La fecha es obligatoria")
    @FutureOrPresent(message = "La fecha no puede ser en el pasado")
    private LocalDate fecha;

    @NotNull(message = "La hora es obligatoria")
    private LocalTime hora;

    @NotBlank(message = "La ubicación es obligatoria")
    private String ubicacion;

    private EventStatus estado;

    @NotNull(message = "La capacidad máxima es obligatoria")
    @Min(value = 1, message = "La capacidad mínima es 1")
    @Max(value = 100000, message = "La capacidad no puede superar 100.000")
    private Integer capacidadMaxima;

    private Boolean parkingAvailable;

    @Min(value = 0, message = "Los cupos de parqueadero no pueden ser negativos")
    private Integer parkingSpots = 0;

    @NotNull(message = "El ID del creador es obligatorio")
    private Long createdById;

}