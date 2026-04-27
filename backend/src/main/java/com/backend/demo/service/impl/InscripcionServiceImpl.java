package com.backend.demo.service.impl;

import com.backend.demo.dto.request.CreateInscripcionRequest;
import com.backend.demo.dto.response.EventoInscritosResponse;
import com.backend.demo.dto.response.InscripcionResponse;
import com.backend.demo.exception.BadRequestException;
import com.backend.demo.exception.ResourceNotFoundException;
import com.backend.demo.model.entity.Event;
import com.backend.demo.model.entity.Inscripcion;
import com.backend.demo.model.entity.User;
import com.backend.demo.model.enums.EventStatus;
import com.backend.demo.model.enums.InscripcionStatus;
import com.backend.demo.repository.EventRepository;
import com.backend.demo.repository.InscripcionRepository;
import com.backend.demo.repository.UserRepository;
import com.backend.demo.service.IInscripcionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class InscripcionServiceImpl implements IInscripcionService {

    @Autowired
    private InscripcionRepository inscripcionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;

    // ==================== CREAR INSCRIPCIÓN ====================

    @Override
    public InscripcionResponse createInscripcion(CreateInscripcionRequest request) {
        // Validar que el usuario exista
        User usuario = userRepository.findById(request.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuario no encontrado con ID: " + request.getUsuarioId()));

        // Validar que el evento exista
        Event evento = eventRepository.findById(request.getEventoId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Evento no encontrado con ID: " + request.getEventoId()));

        // Validar que el evento esté disponible
        validarEventoDisponible(evento);

        // Validar cupos disponibles
        validarCuposDisponibles(evento);

        // Validar que el usuario no esté ya inscrito
        validarNoInscritoPreview(usuario.getId(), evento.getId());

        // Crear la inscripción
        Inscripcion inscripcion = new Inscripcion();
        inscripcion.setUsuario(usuario);
        inscripcion.setEvento(evento);
        inscripcion.setEstado(InscripcionStatus.CONFIRMADA);

        // Actualizar contador del evento
        evento.incrementarInscritos();
        eventRepository.save(evento);

        // Guardar inscripción
        Inscripcion inscripcionCreada = inscripcionRepository.save(inscripcion);

        return mapToResponse(inscripcionCreada);
    }

    // ==================== CANCELAR INSCRIPCIÓN ====================

    @Override
    public InscripcionResponse cancelarInscripcion(Long id) {
        Inscripcion inscripcion = inscripcionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Inscripción no encontrada con ID: " + id));

        // Validar que no esté ya cancelada
        if (inscripcion.getEstado() == InscripcionStatus.CANCELADA) {
            throw new BadRequestException("La inscripción ya estaba cancelada.");
        }

        // Decrementar contador del evento
        Event evento = inscripcion.getEvento();
        evento.decrementarInscritos();
        eventRepository.save(evento);

        // Cambiar estado a cancelada (lógica)
        inscripcion.setEstado(InscripcionStatus.CANCELADA);
        Inscripcion inscripcionCancelada = inscripcionRepository.save(inscripcion);

        return mapToResponse(inscripcionCancelada);
    }

    // ==================== CONSULTAS ====================

    @Override
    @Transactional(readOnly = true)
    public InscripcionResponse getInscripcionById(Long id) {
        Inscripcion inscripcion = inscripcionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Inscripción no encontrada con ID: " + id));
        return mapToResponse(inscripcion);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InscripcionResponse> getInscripcionesByUsuario(Long usuarioId, Pageable pageable) {
        // Validar que el usuario exista
        if (!userRepository.existsById(usuarioId)) {
            throw new ResourceNotFoundException(
                    "Usuario no encontrado con ID: " + usuarioId);
        }

        // Obtener inscripciones paginadas
        Page<Inscripcion> inscripciones = inscripcionRepository.findByUsuarioId(usuarioId, pageable);
        
        // Mapear a DTO
        return inscripciones.map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public EventoInscritosResponse getInscripcionesByEvento(Long eventoId, Pageable pageable) {
        // Validar que el evento exista
        Event evento = eventRepository.findById(eventoId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Evento no encontrado con ID: " + eventoId));

        // Obtener inscripciones con paginación
        Page<Inscripcion> inscripciones = inscripcionRepository.findByEventoId(eventoId, pageable);
        Page<InscripcionResponse> inscripcionesResponse = inscripciones.map(this::mapToResponse);

        // Calcular estadísticas
        Integer totalCupos = evento.getCapacidadMaxima();
        Integer cuposUsados = evento.getInscritosCount();
        Integer cuposDisponibles = totalCupos - cuposUsados;

        return new EventoInscritosResponse(
                inscripcionesResponse,
                totalCupos,
                cuposUsados,
                cuposDisponibles
        );
    }

    @Override
    public void deleteInscripcion(Long id) {
        if (!inscripcionRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Inscripción no encontrada con ID: " + id);
        }
        inscripcionRepository.deleteById(id);
    }

    // ==================== VALIDACIONES ====================

    /**
     * Valida que el evento esté disponible para nuevas inscripciones
     * @param evento evento a validar
     * @throws BadRequestException si el evento no está disponible
     */
    private void validarEventoDisponible(Event evento) {
        if (evento.getEstado() == EventStatus.CANCELLED) {
            throw new BadRequestException("evento no disponible");
        }

        if (evento.getEstado() == EventStatus.DRAFT) {
            throw new BadRequestException("evento no disponible");
        }

        if (evento.getEstado() == EventStatus.COMPLETED) {
            throw new BadRequestException("evento no disponible");
        }
    }

    /**
     * Valida que el evento tenga cupos disponibles
     * @param evento evento a validar
     * @throws BadRequestException si no hay cupos disponibles
     */
    private void validarCuposDisponibles(Event evento) {
        if (evento.getInscritosCount() >= evento.getCapacidadMaxima()) {
            throw new BadRequestException("cupos agotados");
        }
    }

    /**
     * Valida que el usuario no esté ya inscrito al evento
     * @param usuarioId ID del usuario
     * @param eventoId ID del evento
     * @throws BadRequestException si el usuario ya está inscrito
     */
    private void validarNoInscritoPreview(Long usuarioId, Long eventoId) {
        if (inscripcionRepository.hasActiveInscription(usuarioId, eventoId)) {
            throw new BadRequestException("ya inscrito");
        }
    }

    // ==================== MAPEO ====================

    /**
     * Mapea una entidad Inscripcion a su DTO de respuesta
     * @param inscripcion inscripción a mapear
     * @return DTO InscripcionResponse
     */
    private InscripcionResponse mapToResponse(Inscripcion inscripcion) {
        InscripcionResponse response = new InscripcionResponse();
        response.setId(inscripcion.getId());
        response.setUsuarioId(inscripcion.getUsuario().getId());
        response.setUsuarioNombre(inscripcion.getUsuario().getNombre() + " "
                + inscripcion.getUsuario().getApellido());
        response.setEventoId(inscripcion.getEvento().getId());
        response.setEventoNombre(inscripcion.getEvento().getNombre());
        response.setEstado(inscripcion.getEstado());
        response.setCreatedAt(inscripcion.getCreatedAt());
        response.setCuposRestantes(
                inscripcion.getEvento().getCapacidadMaxima()
                        - inscripcion.getEvento().getInscritosCount()
        );

        return response;
    }
}