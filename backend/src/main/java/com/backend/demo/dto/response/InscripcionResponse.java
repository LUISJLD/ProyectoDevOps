package com.backend.demo.dto.response;

import com.backend.demo.model.enums.InscripcionStatus;
import java.time.LocalDateTime;

public class InscripcionResponse {

    private Long id;
    private Long usuarioId;
    private String usuarioNombre;
    private Long eventoId;
    private String eventoNombre;
    private InscripcionStatus estado;
    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    public String getUsuarioNombre() { return usuarioNombre; }
    public void setUsuarioNombre(String usuarioNombre) { this.usuarioNombre = usuarioNombre; }
    public Long getEventoId() { return eventoId; }
    public void setEventoId(Long eventoId) { this.eventoId = eventoId; }
    public String getEventoNombre() { return eventoNombre; }
    public void setEventoNombre(String eventoNombre) { this.eventoNombre = eventoNombre; }
    public InscripcionStatus getEstado() { return estado; }
    public void setEstado(InscripcionStatus estado) { this.estado = estado; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}