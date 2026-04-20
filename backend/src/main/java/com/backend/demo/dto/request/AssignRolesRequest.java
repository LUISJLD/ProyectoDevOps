package com.backend.demo.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.Set;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignRolesRequest {

    @NotEmpty(message = "Debe proporcionar al menos un rol")
    private Set<String> roles;
}