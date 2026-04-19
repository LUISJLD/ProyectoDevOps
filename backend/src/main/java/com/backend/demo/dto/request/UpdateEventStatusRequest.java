package com.backend.demo.dto.request;

import com.backend.demo.model.enums.EventStatus;
import jakarta.validation.constraints.NotNull;

/**
 * US15 - Request para actualizar únicamente el estado de un evento.
 * Se usa en PATCH /api/events/{id}/status
 */
public class UpdateEventStatusRequest {

    @NotNull(message = "El estado es obligatorio")
    private EventStatus estado;

    public EventStatus getEstado() { return estado; }
    public void setEstado(EventStatus estado) { this.estado = estado; }
}
