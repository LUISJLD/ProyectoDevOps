package com.backend.demo.mapper;

import com.backend.demo.dto.response.UserResponse;
import com.backend.demo.model.entity.Role;
import com.backend.demo.model.entity.User;
import org.mapstruct.Mapper;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponse toResponse(User user);

    // MapStruct usa esto automáticamente
    default Set<String> map(Set<Role> roles) {
        if (roles == null) return Set.of();

        return roles.stream()
                .map(r -> r.getName().name())
                .collect(Collectors.toSet());
    }
}