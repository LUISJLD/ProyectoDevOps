package com.backend.demo.service.impl;

import com.backend.demo.dto.request.CreateEventRequest;
import com.backend.demo.dto.request.UpdateEventRequest;
import com.backend.demo.dto.response.EventResponse;
import com.backend.demo.exception.AccessDeniedException;
import com.backend.demo.exception.BadRequestException;
import com.backend.demo.exception.ResourceNotFoundException;
import com.backend.demo.model.entity.Event;
import com.backend.demo.model.entity.User;
import com.backend.demo.model.enums.EventStatus;
import com.backend.demo.model.enums.ERole;
import com.backend.demo.repository.EventRepository;
import com.backend.demo.repository.UserRepository;
import com.backend.demo.security.services.UserInfoDetail;
import com.backend.demo.service.IEventService;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class EventServiceImpl implements IEventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    //Crear evento
    @Override
    public EventResponse createEvent(CreateEventRequest request) {

        UserInfoDetail user = getAuthenticatedUser();

        User creator = userRepository.findById(user.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Usuario autenticado no encontrado"));

        Event event = Event.builder()
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .fecha(request.getFecha())
                .hora(request.getHora())
                .ubicacion(request.getUbicacion())
                .estado(request.getEstado() != null ? request.getEstado() : EventStatus.DRAFT)
                .capacidadMaxima(request.getCapacidadMaxima())
                .parkingAvailable(request.getParkingAvailable() != null ? request.getParkingAvailable() : false)
                .parkingSpots(request.getParkingSpots() != null ? request.getParkingSpots() : 0)
                .createdBy(creator)
                .build();

        return mapToResponse(eventRepository.save(event));
    }


    @Override
    @Transactional(readOnly = true)
    public EventResponse getEventById(Long id) {
        return mapToResponse(findEventById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EventResponse> getAllEvents(String nombre, EventStatus estado, Pageable pageable) {
        return eventRepository.findByFilters(nombre, estado, pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponse> getEventsByUser(Long userId) {

        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("Usuario no encontrado con ID: " + userId);
        }

        return eventRepository.findByCreatedById(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Actualizar evento
    @Override
    public EventResponse updateEvent(Long id, UpdateEventRequest request) {

        Event event = findEventById(id);
        UserInfoDetail user = getAuthenticatedUser();

        boolean isOwner = event.getCreatedBy().getId().equals(user.getId());
        boolean isAdmin = user.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(ERole.ROLE_ADMIN.name()));

        if (!isOwner && !isAdmin) {
            throw new AccessDeniedException("No autorizado para editar este evento");
        }

        // Validaciones

        if (request.getFecha() != null && request.getFecha().isBefore(LocalDate.now())) {
            throw new BadRequestException("La fecha no puede ser pasada");
        }

        if (request.getCapacidadMaxima() != null && request.getCapacidadMaxima() <= 0) {
            throw new BadRequestException("Capacidad debe ser mayor a 0");
        }

        if (request.getParkingSpots() != null && request.getParkingSpots() < 0) {
            throw new BadRequestException("Los cupos de parqueadero no pueden ser negativos");
        }

        if (Boolean.TRUE.equals(request.getParkingAvailable())
                && request.getParkingSpots() != null
                && request.getParkingSpots() <= 0) {
            throw new BadRequestException("Debe haber al menos un cupo de parqueadero");
        }

        //Seteo

        if (request.getNombre() != null) event.setNombre(request.getNombre());
        if (request.getDescripcion() != null) event.setDescripcion(request.getDescripcion());
        if (request.getFecha() != null) event.setFecha(request.getFecha());
        if (request.getHora() != null) event.setHora(request.getHora());
        if (request.getUbicacion() != null) event.setUbicacion(request.getUbicacion());

        if (request.getCapacidadMaxima() != null) {
            event.setCapacidadMaxima(request.getCapacidadMaxima());
        }

        if (request.getParkingAvailable() != null) {
            event.setParkingAvailable(request.getParkingAvailable());
        }

        if (request.getParkingSpots() != null) {
            event.setParkingSpots(request.getParkingSpots());
        }

        //Mantener consistencia
        if (!event.isParkingAvailable()) {
            event.setParkingSpots(0);
        }

        return mapToResponse(eventRepository.save(event));
    }


    @Override
    public void deleteEvent(Long id) {

        Event event = findEventById(id);
        UserInfoDetail user = getAuthenticatedUser();

        boolean isAdmin = user.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals(ERole.ROLE_ADMIN.name()));

        if (!event.getCreatedBy().getId().equals(user.getId()) && !isAdmin) {
            throw new AccessDeniedException("No autorizado para eliminar este evento");
        }

        eventRepository.delete(event);
    }

    // Actualizar status
    @Override
    public EventResponse updateEventStatus(Long id, EventStatus newStatus) {

        Event event = findEventById(id);
        UserInfoDetail user = getAuthenticatedUser();

        boolean isOwner = event.getCreatedBy().getId().equals(user.getId());
        boolean isAdmin = user.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(ERole.ROLE_ADMIN.name()));

        if (!isOwner && !isAdmin) {
            throw new AccessDeniedException("No autorizado para cambiar el estado");
        }

        validateStatusTransition(event.getEstado(), newStatus);

        event.setEstado(newStatus);

        return mapToResponse(eventRepository.save(event));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponse> getEventsByStatus(EventStatus estado) {
        return eventRepository.findByEstado(estado)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }


    private Event findEventById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Evento no encontrado con ID: " + id));
    }

    private UserInfoDetail getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof UserInfoDetail user)) {
            throw new AccessDeniedException("Usuario no autenticado");
        }

        return user;
    }

    private void validateStatusTransition(EventStatus current, EventStatus next) {

        if (current == next) {
            throw new BadRequestException("El evento ya está en estado: " + current);
        }

        switch (current) {
            case DRAFT -> {
                if (next != EventStatus.PUBLISHED && next != EventStatus.CANCELLED) {
                    throw new BadRequestException("Desde DRAFT solo → PUBLISHED o CANCELLED");
                }
            }
            case PUBLISHED -> {
                if (next != EventStatus.CLOSED && next != EventStatus.CANCELLED) {
                    throw new BadRequestException("Desde PUBLISHED solo → CLOSED o CANCELLED");
                }
            }
            case CLOSED, CANCELLED ->
                    throw new BadRequestException("Estado terminal: " + current);
        }
    }

    private EventResponse mapToResponse(Event event) {
        return EventResponse.builder()
                .id(event.getId())
                .nombre(event.getNombre())
                .descripcion(event.getDescripcion())
                .fecha(event.getFecha())
                .hora(event.getHora())
                .ubicacion(event.getUbicacion())
                .estado(event.getEstado())
                .capacidadMaxima(event.getCapacidadMaxima())
                .parkingAvailable(event.isParkingAvailable())
                .parkingSpots(event.getParkingSpots())
                .createdAt(event.getCreatedAt())
                .updatedAt(event.getUpdatedAt())
                .createdById(event.getCreatedBy().getId())
                .createdByNombre(
                        event.getCreatedBy().getNombre() + " " +
                                event.getCreatedBy().getApellido()
                )
                .build();
    }
}