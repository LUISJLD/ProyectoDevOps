// controller/EventController.java
package com.backend.demo.controller;

import com.backend.demo.dto.request.CreateEventRequest;
import com.backend.demo.dto.response.EventResponse;
import com.backend.demo.model.enums.EventStatus;
import com.backend.demo.service.IEventService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    @Autowired
    private IEventService eventService;

    @PostMapping
    public ResponseEntity<EventResponse> create(@Valid @RequestBody CreateEventRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(eventService.createEvent(request));
    }

    @GetMapping
    public ResponseEntity<Page<EventResponse>> getAll(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) EventStatus estado,
            Pageable pageable) {
        return ResponseEntity.ok(eventService.getAllEvents(nombre, estado, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getEventById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<EventResponse>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(eventService.getEventsByUser(userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody CreateEventRequest request) {
        return ResponseEntity.ok(eventService.updateEvent(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }
}