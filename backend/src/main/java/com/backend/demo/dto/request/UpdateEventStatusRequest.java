package com.backend.demo.dto.request;

import com.backend.demo.model.enums.EventStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * US15 - DTO para actualizar únicamente el estado de un evento.
 * Se usa en PATCH /api/events/{id}/status
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateEventStatusRequest {

    @NotNull(message = "El estado es obligatorio")
    private EventStatus estado;

}
