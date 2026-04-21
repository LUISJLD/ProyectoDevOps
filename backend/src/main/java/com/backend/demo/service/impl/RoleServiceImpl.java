package com.backend.demo.service.impl;

import com.backend.demo.dto.response.RoleResponse;
import com.backend.demo.exception.BadRequestException;
import com.backend.demo.exception.ResourceNotFoundException;
import com.backend.demo.mapper.RoleMapper;
import com.backend.demo.model.entity.Role;
import com.backend.demo.model.enums.ERole;
import com.backend.demo.repository.RoleRepository;
import com.backend.demo.service.IRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class RoleServiceImpl implements IRoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    @Override
    public RoleResponse createRole(String roleName) {
        ERole eRole = parseRole(roleName);
        if (roleRepository.findByName(eRole).isPresent()) {
            throw new BadRequestException("El rol ya existe: " + roleName);
        }
        Role role = Role.builder()
                .name(eRole)
                .build();

        return roleMapper.toResponse(roleRepository.save(role));
    }

    @Override
    @Transactional(readOnly = true)
    public RoleResponse getRoleByName(String roleName) {
        ERole eRole = parseRole(roleName);
        Role role = roleRepository.findByName(eRole)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Rol no encontrado: " + roleName));

        return roleMapper.toResponse(role);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleResponse> getAllRoles() {
        return roleRepository.findAll()
                .stream()
                .map(roleMapper::toResponse)
                .toList();
    }

    @Override
    public void deleteRole(Long id) {
        if (!roleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Rol no encontrado con ID: " + id);
        }
        roleRepository.deleteById(id);
    }

    // Metodo auxiliar

    private ERole parseRole(String roleName) {
        try {
            return ERole.valueOf("ROLE_" + roleName.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Rol no válido: " + roleName);
        }
    }
}