package com.backend.demo.dto.request;

import jakarta.validation.constraints.NotEmpty;

import java.util.Set;

public class AssignRolesRequest {

    @NotEmpty(message = "Debe proporcionar al menos un rol")
    private Set<String> roles;

    public Set<String> getRoles() { return roles; }
    public void setRoles(Set<String> roles) { this.roles = roles; }
}