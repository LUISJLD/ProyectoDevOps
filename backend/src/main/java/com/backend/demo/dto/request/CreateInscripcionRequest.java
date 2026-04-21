package com.backend.demo.dto.request;

import jakarta.validation.constraints.NotNull;

public class CreateInscripcionRequest {

    @NotNull(message = "El ID del usuario es obligatorio")
    private Long usuarioId;

    @NotNull(message = "El ID del evento es obligatorio")
    private Long eventoId;

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    public Long getEventoId() { return eventoId; }
    public void setEventoId(Long eventoId) { this.eventoId = eventoId; }
}
