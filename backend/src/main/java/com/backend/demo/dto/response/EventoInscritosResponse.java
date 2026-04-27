package com.backend.demo.dto.response;

import org.springframework.data.domain.Page;

/**
 * DTO para respuestas de inscripciones en un evento
 * Incluye información de paginación y estadísticas de cupos
 */
public class EventoInscritosResponse {

    private Page<InscripcionResponse> inscripciones;
    private Integer totalCupos;
    private Integer cuposUsados;
    private Integer cuposDisponibles;

    // CONSTRUCTOR

    public EventoInscritosResponse() {}

    public EventoInscritosResponse(
            Page<InscripcionResponse> inscripciones,
            Integer totalCupos,
            Integer cuposUsados,
            Integer cuposDisponibles) {
        this.inscripciones = inscripciones;
        this.totalCupos = totalCupos;
        this.cuposUsados = cuposUsados;
        this.cuposDisponibles = cuposDisponibles;
    }

    // GETTERS Y SETTERS

    public Page<InscripcionResponse> getInscripciones() {
        return inscripciones;
    }

    public void setInscripciones(Page<InscripcionResponse> inscripciones) {
        this.inscripciones = inscripciones;
    }

    public Integer getTotalCupos() {
        return totalCupos;
    }

    public void setTotalCupos(Integer totalCupos) {
        this.totalCupos = totalCupos;
    }

    public Integer getCuposUsados() {
        return cuposUsados;
    }

    public void setCuposUsados(Integer cuposUsados) {
        this.cuposUsados = cuposUsados;
    }

    public Integer getCuposDisponibles() {
        return cuposDisponibles;
    }

    public void setCuposDisponibles(Integer cuposDisponibles) {
        this.cuposDisponibles = cuposDisponibles;
    }
}
