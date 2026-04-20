// controller/EventController.java
package com.backend.demo.controller;

import com.backend.demo.dto.request.CreateEventRequest;
import com.backend.demo.dto.request.UpdateEventRequest;
import com.backend.demo.dto.request.UpdateEventStatusRequest;
import com.backend.demo.dto.response.EventResponse;
import com.backend.demo.model.enums.EventStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import com.backend.demo.service.IEventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    final private IEventService eventService;

    @Operation(summary = "Crear un nuevo evento")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Evento creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Usuario creador no encontrado")
    })
    @PostMapping
    public ResponseEntity<EventResponse> create(@Valid @RequestBody CreateEventRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(eventService.createEvent(request));
    }

    @Operation(
            summary = "Listar eventos con filtros opcionales",
            description = "Filtra por nombre (parcial) y/o estado: DRAFT, PUBLISHED, CLOSED, CANCELLED"
    )
    @GetMapping
    public ResponseEntity<Page<EventResponse>> getAll(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) EventStatus estado,
            Pageable pageable) {
        return ResponseEntity.ok(eventService.getAllEvents(nombre, estado, pageable));
    }

    @Operation(summary = "Obtener detalle de un evento por ID")
    @GetMapping("/{id}")
    public ResponseEntity<EventResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getEventById(id));
    }

    @Operation(summary = "Listar eventos creados por un usuario")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<EventResponse>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(eventService.getEventsByUser(userId));
    }

    @Operation(
            summary = "Filtrar eventos por estado",
            description = "Devuelve todos los eventos con el estado indicado: DRAFT, PUBLISHED, CLOSED o CANCELLED"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de eventos filtrada"),
            @ApiResponse(responseCode = "400", description = "Estado no válido")
    })
    @GetMapping("/status/{estado}")
    public ResponseEntity<List<EventResponse>> getByStatus(
            @PathVariable EventStatus estado) {
        return ResponseEntity.ok(eventService.getEventsByStatus(estado));
    }

    @Operation(
            summary = "Actualizar el estado de un evento",
            description = """
            Transiciones permitidas del ciclo de vida:
            - DRAFT      → PUBLISHED | CANCELLED
            - PUBLISHED  → CLOSED    | CANCELLED
            - CLOSED     → (estado terminal, sin cambios)
            - CANCELLED  → (estado terminal, sin cambios)
            """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estado actualizado correctamente"),
            @ApiResponse(responseCode = "400", description = "Transición de estado no permitida"),
            @ApiResponse(responseCode = "404", description = "Evento no encontrado")
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<EventResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateEventStatusRequest request) {
        return ResponseEntity.ok(eventService.updateEventStatus(id, request.getEstado()));
    }

    @Operation(summary = "Actualizar los datos de un evento")
    @PutMapping("/{id}")
    public ResponseEntity<EventResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateEventRequest request) {
        return ResponseEntity.ok(eventService.updateEvent(id, request));
    }

    @Operation(summary = "Eliminar un evento")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }
}