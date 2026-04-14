package com.backend.demo.dto.request;

import com.backend.demo.model.enums.EventStatus;
import java.time.LocalDate;
import java.time.LocalTime;

public class CreateEventRequest {

    private String nombre;
    private String descripcion;
    private LocalDate fecha;
    private LocalTime hora;
    private String ubicacion;
    private EventStatus estado;
    private Integer capacidadMaxima;
    private boolean parkingAvailable;
    private Integer parkingSpots;
    private Long createdById;

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public LocalTime getHora() { return hora; }
    public void setHora(LocalTime hora) { this.hora = hora; }
    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }
    public EventStatus getEstado() { return estado; }
    public void setEstado(EventStatus estado) { this.estado = estado; }
    public Integer getCapacidadMaxima() { return capacidadMaxima; }
    public void setCapacidadMaxima(Integer capacidadMaxima) { this.capacidadMaxima = capacidadMaxima; }
    public boolean isParkingAvailable() { return parkingAvailable; }
    public void setParkingAvailable(boolean parkingAvailable) { this.parkingAvailable = parkingAvailable; }
    public Integer getParkingSpots() { return parkingSpots; }
    public void setParkingSpots(Integer parkingSpots) { this.parkingSpots = parkingSpots; }
    public Long getCreatedById() { return createdById; }
    public void setCreatedById(Long createdById) { this.createdById = createdById; }
}
