package com.backend.demo.service.impl;

import com.backend.demo.dto.response.RoleResponse;
import com.backend.demo.exception.BadRequestException;
import com.backend.demo.exception.ResourceNotFoundException;
import com.backend.demo.model.entity.Role;
import com.backend.demo.model.enums.ERole;
import com.backend.demo.repository.RoleRepository;
import com.backend.demo.service.IRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class RoleServiceImpl implements IRoleService {
    private final RoleRepository roleRepository;

    @Override
    public RoleResponse createRole(String roleName) {
        ERole eRole = ERole.valueOf("ROLE_" + roleName.toUpperCase());
        if (roleRepository.findByName(eRole).isPresent()) {
            throw new BadRequestException("El rol ya existe: " + roleName);
        }
        Role saved = roleRepository.save(
                Role.builder()
                        .name(eRole)
                        .build()
        );
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public RoleResponse getRoleByName(String roleName) {
        ERole eRole = ERole.valueOf("ROLE_" + roleName.toUpperCase());
        Role role = roleRepository.findByName(eRole)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado: " + roleName));
        return mapToResponse(role);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleResponse> getAllRoles() {
        return roleRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteRole(Long id) {
        if (!roleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Rol no encontrado con ID: " + id);
        }
        roleRepository.deleteById(id);
    }

    private RoleResponse mapToResponse(Role role) {
        RoleResponse response = new RoleResponse();
        response.setId(role.getId());
        response.setNombre(role.getName().name());
        return response;
    }
}