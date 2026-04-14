package com.backend.demo.service;

import com.backend.demo.dto.response.RoleResponse;

import java.util.List;

public interface IRoleService {
    RoleResponse createRole(String roleName);
    RoleResponse getRoleByName(String roleName);
    List<RoleResponse> getAllRoles();
    void deleteRole(Long id);
}