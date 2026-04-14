package com.backend.demo.service;

import com.backend.demo.dto.request.CreateEventRequest;
import com.backend.demo.dto.response.EventResponse;
import com.backend.demo.model.enums.EventStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IEventService {
    EventResponse createEvent(CreateEventRequest request);
    EventResponse getEventById(Long id);
    Page<EventResponse> getAllEvents(String nombre, EventStatus estado, Pageable pageable);
    List<EventResponse> getEventsByUser(Long userId);
    EventResponse updateEvent(Long id, CreateEventRequest request);
    void deleteEvent(Long id);
}