package com.backend.demo.service.impl;

import com.backend.demo.dto.request.CreateInscripcionRequest;
import com.backend.demo.dto.response.InscripcionResponse;
import com.backend.demo.model.entity.Event;
import com.backend.demo.model.entity.Inscripcion;
import com.backend.demo.model.entity.User;
import com.backend.demo.model.enums.InscripcionStatus;
import com.backend.demo.repository.EventRepository;
import com.backend.demo.repository.InscripcionRepository;
import com.backend.demo.repository.UserRepository;
import com.backend.demo.service.IInscripcionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class InscripcionServiceImpl implements IInscripcionService {

    @Autowired private InscripcionRepository inscripcionRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private EventRepository eventRepository;

    @Override
    public InscripcionResponse createInscripcion(CreateInscripcionRequest request) {

        // Verificar restricción única
        if (inscripcionRepository.existsByUsuarioIdAndEventoId(
                request.getUsuarioId(), request.getEventoId())) {
            throw new RuntimeException("El usuario ya está inscrito en este evento");
        }

        User usuario = userRepository.findById(request.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + request.getUsuarioId()));

        Event evento = eventRepository.findById(request.getEventoId())
                .orElseThrow(() -> new RuntimeException("Evento no encontrado con ID: " + request.getEventoId()));

        Inscripcion inscripcion = new Inscripcion();
        inscripcion.setUsuario(usuario);
        inscripcion.setEvento(evento);
        inscripcion.setEstado(InscripcionStatus.PENDIENTE);

        return mapToResponse(inscripcionRepository.save(inscripcion));
    }

    @Override
    @Transactional(readOnly = true)
    public InscripcionResponse getInscripcionById(Long id) {
        Inscripcion inscripcion = inscripcionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inscripcion no encontrada con ID: " + id));
        return mapToResponse(inscripcion);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InscripcionResponse> getInscripcionesByUsuario(Long usuarioId) {
        if (!userRepository.existsById(usuarioId)) {
            throw new RuntimeException("Usuario no encontrado con ID: " + usuarioId);
        }
        return inscripcionRepository.findByUsuarioId(usuarioId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InscripcionResponse> getInscripcionesByEvento(Long eventoId) {
        if (!eventRepository.existsById(eventoId)) {
            throw new RuntimeException("Evento no encontrado con ID: " + eventoId);
        }
        return inscripcionRepository.findByEventoId(eventoId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public InscripcionResponse cancelarInscripcion(Long id) {
        Inscripcion inscripcion = inscripcionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inscripcion no encontrada con ID: " + id));
        inscripcion.setEstado(InscripcionStatus.CANCELADA);
        return mapToResponse(inscripcionRepository.save(inscripcion));
    }

    @Override
    public void deleteInscripcion(Long id) {
        if (!inscripcionRepository.existsById(id)) {
            throw new RuntimeException("Inscripcion no encontrada con ID: " + id);
        }
        inscripcionRepository.deleteById(id);
    }

    private InscripcionResponse mapToResponse(Inscripcion inscripcion) {
        InscripcionResponse response = new InscripcionResponse();
        response.setId(inscripcion.getId());
        response.setUsuarioId(inscripcion.getUsuario().getId());
        response.setUsuarioNombre(
                inscripcion.getUsuario().getNombre() + " " + inscripcion.getUsuario().getApellido()
        );
        response.setEventoId(inscripcion.getEvento().getId());
        response.setEventoNombre(inscripcion.getEvento().getNombre());
        response.setEstado(inscripcion.getEstado());
        response.setCreatedAt(inscripcion.getCreatedAt());
        return response;
    }
}