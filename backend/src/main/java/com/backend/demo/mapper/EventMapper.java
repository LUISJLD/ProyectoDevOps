package com.backend.demo.mapper;

import com.backend.demo.dto.request.CreateEventRequest;
import com.backend.demo.dto.request.UpdateEventRequest;
import com.backend.demo.dto.response.EventResponse;
import com.backend.demo.model.entity.Event;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface EventMapper {

    // Entity → Response
    @Mapping(source = "createdBy.id", target = "createdById")
    @Mapping(source = "createdBy.nombre", target = "createdByNombre")
    @Mapping(source = "createdBy.apellido", target = "createdByApellido")
    EventResponse toResponse(Event event);

    // CreateRequest → Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "estado", expression = "java(request.getEstado() != null ? request.getEstado() : com.backend.demo.model.enums.EventStatus.DRAFT)")
    @Mapping(target = "parkingAvailable", expression = "java(request.getParkingAvailable() != null ? request.getParkingAvailable() : false)")
    @Mapping(target = "parkingSpots", expression = "java(request.getParkingSpots() != null ? request.getParkingSpots() : 0)")
    Event toEntity(CreateEventRequest request);

    // UpdateRequest → Entity
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEvent(UpdateEventRequest request, @MappingTarget Event event);
}