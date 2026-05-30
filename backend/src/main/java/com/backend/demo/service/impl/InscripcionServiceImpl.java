package com.backend.demo.service.impl;

import com.backend.demo.dto.request.CheckinRequest;
import com.backend.demo.dto.request.CreateInscripcionRequest;
import com.backend.demo.dto.response.CheckinResponse;
import com.backend.demo.dto.response.EventoInscritosResponse;
import com.backend.demo.dto.response.InscripcionResponse;
import com.backend.demo.dto.response.ReporteAsistenciaResponse;
import com.backend.demo.dto.response.ReporteAsistenciaResponse.AsistenteDetalleResponse;
import com.backend.demo.exception.AccessDeniedException;
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
import com.backend.demo.security.services.UserInfoDetail;
import com.backend.demo.service.IEmailNotificationService;
import com.backend.demo.service.IInscripcionService;
import com.backend.demo.service.QrService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class InscripcionServiceImpl implements IInscripcionService {

    private final InscripcionRepository inscripcionRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final IEmailNotificationService emailNotificationService;
    private final QrService qrService;          // ← nuevo

    // ==================== CREAR INSCRIPCIÓN ====================

    @Override
    public InscripcionResponse createInscripcion(CreateInscripcionRequest request) {

        UserInfoDetail userDetail = getAuthenticatedUser();

        User usuario = userRepository.findById(userDetail.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuario autenticado no encontrado"));

        Event evento = eventRepository.findById(request.getEventoId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Evento no encontrado con ID: " + request.getEventoId()));

        validarEventoDisponible(evento);
        validarNoInscritoPreview(usuario.getId(), evento.getId());

        if (!evento.tieneCuposDisponibles()) {
            throw new BadRequestException("No hay cupos disponibles para este evento");
        }

        Inscripcion inscripcion = inscripcionRepository.findByUsuarioIdAndEventoId(usuario.getId(), evento.getId())
                .orElse(null);

        if (inscripcion != null) {
            // Reutilizar inscripción cancelada
            inscripcion.setEstado(InscripcionStatus.CONFIRMADA);
            inscripcion.setAsistio(false);
            inscripcion.setCheckinAt(null);
        } else {
            // Generar token UUID único para el QR
            String qrToken = UUID.randomUUID().toString();

            inscripcion = Inscripcion.builder()
                    .usuario(usuario)
                    .evento(evento)
                    .estado(InscripcionStatus.CONFIRMADA)
                    .qrToken(qrToken)
                    .asistio(false)
                    .build();
        }

        evento.incrementarInscritos();

        Inscripcion saved = inscripcionRepository.save(inscripcion);

        // Enviar notificación asíncrona al usuario
        emailNotificationService.sendInscripcionConfirmation(saved);

        return mapToResponse(saved);
    }

    // ==================== QR ====================

    @Override
    @Transactional(readOnly = true)
    public String getQrUrl(Long inscripcionId) {
        Inscripcion inscripcion = inscripcionRepository.findById(inscripcionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Inscripción no encontrada con ID: " + inscripcionId));

        return qrService.generarUrlQr(inscripcion.getQrToken());
    }

    // ==================== CHECK-IN ====================

    @Override
    public CheckinResponse realizarCheckin(Long eventoId, CheckinRequest request) {

        // 1. Buscar la inscripción por token
        Inscripcion inscripcion = inscripcionRepository.findByQrToken(request.getToken())
                .orElseThrow(() -> new BadRequestException(
                        "El código QR no es válido o no corresponde a ninguna inscripción"));

        // 2. Validar que el token pertenezca a ESTE evento
        if (!inscripcion.getEvento().getId().equals(eventoId)) {
            throw new BadRequestException(
                    "El código QR corresponde a un evento diferente al que se está registrando");
        }

        // 3. Rechazar inscripciones canceladas
        if (inscripcion.getEstado() == InscripcionStatus.CANCELADA) {
            throw new BadRequestException(
                    "No se puede hacer check-in: la inscripción se encuentra cancelada");
        }

        // 4. Rechazar QR ya utilizado
        if (inscripcion.isAsistio()) {
            throw new BadRequestException(
                    "El código QR ya fue utilizado. No se permite un segundo check-in");
        }

        // 5. Marcar asistencia
        inscripcion.setAsistio(true);
        inscripcion.setCheckinAt(LocalDateTime.now());
        inscripcion.setEstado(InscripcionStatus.ASISTIDA);
        inscripcionRepository.save(inscripcion);
        emailNotificationService.sendCheckinConfirmation(inscripcion);
        User usuario = inscripcion.getUsuario();
        Event evento = inscripcion.getEvento();

        return CheckinResponse.builder()
                .inscripcionId(inscripcion.getId())
                .usuarioId(usuario.getId())
                .usuarioNombre(usuario.getNombre() + " " + usuario.getApellido())
                .eventoId(evento.getId())
                .eventoNombre(evento.getNombre())
                .checkinAt(inscripcion.getCheckinAt())
                .mensaje("¡Check-in registrado exitosamente! Bienvenido al evento.")
                .build();
    }

    // REPORTE

    @Override
    @Transactional(readOnly = true)
    public ReporteAsistenciaResponse getReporteAsistencia(Long eventoId) {

        Event evento = eventRepository.findById(eventoId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Evento no encontrado con ID: " + eventoId));

        List<Inscripcion> inscripciones =
                inscripcionRepository.findAllByEventoIdForReporte(eventoId);

        int totalInscritos  = inscripciones.size();
        int totalAsistentes = (int) inscripciones.stream()
                .filter(Inscripcion::isAsistio).count();
        int totalAusentes   = totalInscritos - totalAsistentes;

        double porcentaje = totalInscritos > 0
                ? Math.round((totalAsistentes * 100.0 / totalInscritos) * 100.0) / 100.0
                : 0.0;

        List<AsistenteDetalleResponse> detalle = inscripciones.stream()
                .map(i -> AsistenteDetalleResponse.builder()
                        .inscripcionId(i.getId())
                        .usuarioId(i.getUsuario().getId())
                        .nombreCompleto(i.getUsuario().getNombre() + " "
                                + i.getUsuario().getApellido())
                        .email(i.getUsuario().getEmail())
                        .estadoInscripcion(i.getEstado().name())
                        .asistio(i.isAsistio())
                        .checkinAt(i.getCheckinAt())
                        .build())
                .collect(Collectors.toList());

        return ReporteAsistenciaResponse.builder()
                .eventoId(evento.getId())
                .eventoNombre(evento.getNombre())
                .totalInscritos(totalInscritos)
                .totalAsistentes(totalAsistentes)
                .totalAusentes(totalAusentes)
                .porcentajeAsistencia(porcentaje)
                .inscritos(detalle)
                .build();
    }

    // ==================== CANCELAR ====================

    @Override
    public InscripcionResponse cancelarInscripcion(Long id) {
        Inscripcion inscripcion = inscripcionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Inscripción no encontrada con ID: " + id));

        if (inscripcion.getEstado() == InscripcionStatus.CANCELADA) {
            throw new BadRequestException("La inscripción ya estaba cancelada.");
        }

        Event evento = inscripcion.getEvento();
        evento.decrementarInscritos();
        eventRepository.save(evento);

        inscripcion.setEstado(InscripcionStatus.CANCELADA);
        return mapToResponse(inscripcionRepository.save(inscripcion));
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
        if (!userRepository.existsById(usuarioId)) {
            throw new ResourceNotFoundException("Usuario no encontrado con ID: " + usuarioId);
        }
        return inscripcionRepository.findByUsuarioId(usuarioId, pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public EventoInscritosResponse getInscripcionesByEvento(Long eventoId, Pageable pageable) {
        Event evento = eventRepository.findById(eventoId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Evento no encontrado con ID: " + eventoId));

        Page<InscripcionResponse> inscripcionesResponse =
                inscripcionRepository.findByEventoId(eventoId, pageable)
                        .map(this::mapToResponse);

        Integer totalCupos       = evento.getCapacidadMaxima();
        Integer cuposUsados      = evento.getInscritosCount();
        Integer cuposDisponibles = totalCupos - cuposUsados;

        return new EventoInscritosResponse(
                inscripcionesResponse, totalCupos, cuposUsados, cuposDisponibles);
    }

    @Override
    public void deleteInscripcion(Long id) {
        if (!inscripcionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Inscripción no encontrada con ID: " + id);
        }
        inscripcionRepository.deleteById(id);
    }

    // ==================== VALIDACIONES PRIVADAS ====================

    private void validarEventoDisponible(Event evento) {
        if (evento.getEstado() == EventStatus.CANCELLED
                || evento.getEstado() == EventStatus.DRAFT
                || evento.getEstado() == EventStatus.COMPLETED) {
            throw new BadRequestException("El evento no está disponible para inscripciones");
        }
    }

    private void validarNoInscritoPreview(Long usuarioId, Long eventoId) {
        if (inscripcionRepository.hasActiveInscription(usuarioId, eventoId)) {
            throw new BadRequestException("El usuario ya se encuentra inscrito en este evento");
        }
    }

    private UserInfoDetail getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UserInfoDetail user)) {
            throw new AccessDeniedException("Usuario no autenticado");
        }
        return user;
    }

    // ==================== MAPEO ====================

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
                        - inscripcion.getEvento().getInscritosCount());
        // URL del QR disponible en la respuesta de consulta
        response.setQrUrl(qrService.generarUrlQr(inscripcion.getQrToken()));
        return response;
    }
}