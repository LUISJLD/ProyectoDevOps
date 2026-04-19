package com.backend.demo.service.impl;

import com.backend.demo.dto.request.CreateEventRequest;
import com.backend.demo.dto.response.EventResponse;
import com.backend.demo.exception.BadRequestException;
import com.backend.demo.exception.ResourceNotFoundException;
import com.backend.demo.model.entity.Event;
import com.backend.demo.model.entity.User;
import com.backend.demo.model.enums.EventStatus;
import com.backend.demo.repository.EventRepository;
import com.backend.demo.repository.UserRepository;
import com.backend.demo.service.IEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class EventServiceImpl implements IEventService {

    @Autowired private EventRepository eventRepository;
    @Autowired private UserRepository userRepository;

    @Override
    public EventResponse createEvent(CreateEventRequest request) {
        User creator = userRepository.findById(request.getCreatedById())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + request.getCreatedById()));

        Event event = new Event();
        event.setNombre(request.getNombre());
        event.setDescripcion(request.getDescripcion());
        event.setFecha(request.getFecha());
        event.setHora(request.getHora());
        event.setUbicacion(request.getUbicacion());
        event.setEstado(request.getEstado() != null ? request.getEstado() : EventStatus.DRAFT);
        event.setCapacidadMaxima(request.getCapacidadMaxima());
        event.setParkingAvailable(request.isParkingAvailable());
        event.setParkingSpots(request.getParkingSpots() != null ? request.getParkingSpots() : 0);
        event.setCreatedBy(creator);

        return mapToResponse(eventRepository.save(event));
    }

    @Override
    @Transactional(readOnly = true)
    public EventResponse getEventById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado con ID: " + id));
        return mapToResponse(event);
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
            throw new RuntimeException("Usuario no encontrado con ID: " + userId);
        }
        return eventRepository.findByCreatedById(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public EventResponse updateEvent(Long id, CreateEventRequest request) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado con ID: " + id));

        if (request.getNombre()        != null) event.setNombre(request.getNombre());
        if (request.getDescripcion()   != null) event.setDescripcion(request.getDescripcion());
        if (request.getFecha()         != null) event.setFecha(request.getFecha());
        if (request.getHora()          != null) event.setHora(request.getHora());
        if (request.getUbicacion()     != null) event.setUbicacion(request.getUbicacion());
        if (request.getEstado()        != null) event.setEstado(request.getEstado());
        if (request.getCapacidadMaxima() != null) event.setCapacidadMaxima(request.getCapacidadMaxima());
        if (request.getParkingSpots()  != null) event.setParkingSpots(request.getParkingSpots());
        event.setParkingAvailable(request.isParkingAvailable());

        return mapToResponse(eventRepository.save(event));
    }

    @Override
    public void deleteEvent(Long id) {
        if (!eventRepository.existsById(id)) {
            throw new RuntimeException("Evento no encontrado con ID: " + id);
        }
        eventRepository.deleteById(id);
    }

    @Override
    public EventResponse updateEventStatus(Long id, EventStatus newStatus) {
        Event event = findEventById(id);
        EventStatus current = event.getEstado();
        validateStatusTransition(current, newStatus);
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
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Evento no encontrado con ID: " + id));
    }

    private void validateStatusTransition(EventStatus current, EventStatus next) {
        if (current == next) {
            throw new BadRequestException(
                    "El evento ya se encuentra en estado: " + current);
        }
        switch (current) {
            case DRAFT -> {
                if (next != EventStatus.PUBLISHED && next != EventStatus.CANCELLED) {
                    throw new BadRequestException(
                            "Desde DRAFT solo se puede pasar a PUBLISHED o CANCELLED. Estado solicitado: " + next);
                }
            }
            case PUBLISHED -> {
                if (next != EventStatus.CLOSED && next != EventStatus.CANCELLED) {
                    throw new BadRequestException(
                            "Desde PUBLISHED solo se puede pasar a CLOSED o CANCELLED. Estado solicitado: " + next);
                }
            }
            case CLOSED, CANCELLED -> throw new BadRequestException(
                    "El evento en estado " + current + " es terminal y no puede cambiar de estado.");
            default -> throw new BadRequestException("Estado desconocido: " + current);
        }
    }

    private EventResponse mapToResponse(Event event) {
        EventResponse response = new EventResponse();
        response.setId(event.getId());
        response.setNombre(event.getNombre());
        response.setDescripcion(event.getDescripcion());
        response.setFecha(event.getFecha());
        response.setHora(event.getHora());
        response.setUbicacion(event.getUbicacion());
        response.setEstado(event.getEstado());
        response.setCapacidadMaxima(event.getCapacidadMaxima());
        response.setParkingAvailable(event.isParkingAvailable());
        response.setParkingSpots(event.getParkingSpots());
        response.setCreatedAt(event.getCreatedAt());
        response.setUpdatedAt(event.getUpdatedAt());
        response.setCreatedById(event.getCreatedBy().getId());
        response.setCreatedByNombre(event.getCreatedBy().getNombre() + " " + event.getCreatedBy().getApellido());
        return response;
    }
}