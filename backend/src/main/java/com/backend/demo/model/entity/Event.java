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
    @Column(nullable = false)
    private EventStatus estado = EventStatus.DRAFT;

    @Column(nullable = false, columnDefinition = "INT CHECK (capacidad_maxima > 0)")
    private Integer capacidadMaxima;

    // Campos de parqueadero
    @Column(nullable = false)
    private boolean parkingAvailable = false;
    private Integer parkingSpots = 0;

    // Auditoría
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // Relación con User (creador)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    // Se ejecuta antes de persistir
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Se ejecuta antes de actualizar
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }


    @PrePersist
    @PreUpdate
    private void validateParking(){
        if(!parkingAvailable){
            //Si no hay parqueaderos => cupos = 0
            parkingSpots = 0;
        }else{
            //Si hay parqueaderos -> Validar
            if(parkingSpots == null){
                throw new RuntimeException("Debe indicar los cupos del parqueadero.");
            }

            if(parkingSpots < 0){
                throw new RuntimeException("Los cupos no pueden ser negativos.");
            }
        }
    }
}