package com.backend.demo.model.entity;

import com.backend.demo.model.enums.InscripcionStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "inscripciones",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_usuario_evento",
                        columnNames = {"usuario_id", "evento_id"}
                )
        }
)
public class Inscripcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private User usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evento_id", nullable = false)
    private Event evento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InscripcionStatus estado = InscripcionStatus.PENDIENTE;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUsuario() { return usuario; }
    public void setUsuario(User usuario) { this.usuario = usuario; }
    public Event getEvento() { return evento; }
    public void setEvento(Event evento) { this.evento = evento; }
    public InscripcionStatus getEstado() { return estado; }
    public void setEstado(InscripcionStatus estado) { this.estado = estado; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}