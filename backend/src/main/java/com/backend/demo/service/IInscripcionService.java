package com.backend.demo.service;

import com.backend.demo.dto.request.CreateInscripcionRequest;
import com.backend.demo.dto.response.EventoInscritosResponse;
import com.backend.demo.dto.response.InscripcionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Interfaz de servicio para la gestión de inscripciones
 * Define los contratos para crear, consultar, cancelar y eliminar inscripciones
 */
public interface IInscripcionService {

    /**
     * Crea una nueva inscripción de un usuario a un evento
     * @param request datos de la inscripción
     * @return respuesta de inscripción creada
     * @throws ResourceNotFoundException si el usuario o evento no existen
     * @throws BadRequestException si el evento no está disponible, no hay cupos
     *                              o el usuario ya está inscrito
     */
    InscripcionResponse createInscripcion(CreateInscripcionRequest request);

    /**
     * Obtiene una inscripción por su ID
     * @param id identificador de la inscripción
     * @return respuesta de la inscripción
     * @throws ResourceNotFoundException si la inscripción no existe
     */
    InscripcionResponse getInscripcionById(Long id);

    /**
     * Obtiene las inscripciones de un usuario con paginación
     * @param usuarioId ID del usuario
     * @param pageable información de paginación y ordenamiento
     * @return Page de inscripciones del usuario
     * @throws ResourceNotFoundException si el usuario no existe
     */
    Page<InscripcionResponse> getInscripcionesByUsuario(Long usuarioId, Pageable pageable);

    /**
     * Obtiene las inscripciones de un evento con información de cupos
     * @param eventoId ID del evento
     * @param pageable información de paginación y ordenamiento
     * @return respuesta con inscripciones y estadísticas de cupos
     * @throws ResourceNotFoundException si el evento no existe
     */
    EventoInscritosResponse getInscripcionesByEvento(Long eventoId, Pageable pageable);

    /**
     * Cancela una inscripción de forma lógica (cambia estado a CANCELADA)
     * @param id identificador de la inscripción
     * @return respuesta de la inscripción cancelada
     * @throws ResourceNotFoundException si la inscripción no existe
     * @throws BadRequestException si la inscripción ya estaba cancelada
     */
    InscripcionResponse cancelarInscripcion(Long id);

    /**
     * Elimina una inscripción de forma física de la base de datos
     * @param id identificador de la inscripción
     * @throws ResourceNotFoundException si la inscripción no existe
     */
    void deleteInscripcion(Long id);
}