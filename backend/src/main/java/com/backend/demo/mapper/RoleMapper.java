package com.backend.demo.mapper;

import com.backend.demo.dto.response.RoleResponse;
import com.backend.demo.model.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    @Mapping(target = "nombre", expression = "java(role.getName().name())")
    RoleResponse toResponse(Role role);
}