package com.backend.demo.model.entity;

import com.backend.demo.model.enums.EventStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "events")
@ToString(exclude = "createdBy")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false)
    private LocalTime hora;

    @Column(nullable = false)
    private String ubicacion;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false)
    private EventStatus estado = EventStatus.DRAFT;

    @Column(nullable = false, columnDefinition = "INT CHECK (capacidad_maxima > 0)")
    private Integer capacidadMaxima;

    // Parqueadero
    @Column(nullable = false)
    @Builder.Default
    private boolean parkingAvailable = false;

    @Builder.Default
    private Integer parkingSpots = 0;

    // Auditoría
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // Relación con User
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    // =========================
    // LIFECYCLE CALLBACKS LIMPIOS
    // =========================

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        validateParking();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        validateParking();
    }

    // VALIDACIÓN INTERNA

    private void validateParking() {

        if (!parkingAvailable) {
            parkingSpots = 0;
            return;
        }

        if (parkingSpots == null) {
            throw new IllegalArgumentException(
                    "Debe indicar los cupos del parqueadero."
            );
        }

        if (parkingSpots < 0) {
            throw new IllegalArgumentException(
                    "Los cupos no pueden ser negativos."
            );
        }
    }
}