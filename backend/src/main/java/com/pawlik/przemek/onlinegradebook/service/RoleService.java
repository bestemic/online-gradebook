package com.pawlik.przemek.onlinegradebook.service;

import com.pawlik.przemek.onlinegradebook.dto.role.GetRoleDto;
import com.pawlik.przemek.onlinegradebook.dto.role.GetRolesDto;
import com.pawlik.przemek.onlinegradebook.mapper.RoleMapper;
import com.pawlik.przemek.onlinegradebook.model.Role;
import com.pawlik.przemek.onlinegradebook.repository.RoleRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@AllArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    public GetRolesDto getAllRoles() {
        List<GetRoleDto> roles = roleRepository.findAll().stream()
                .map(roleMapper::roleToRoleDto)
                .toList();

        return new GetRolesDto(roles);
    }

    public void initRoles() {
        String[] roleNames = {"ROLE_ADMIN", "ROLE_TEACHER", "ROLE_STUDENT"};

        Arrays.stream(roleNames)
                .filter(roleName -> roleRepository.findByName(roleName).isEmpty())
                .forEach(roleName -> {
                    var role = Role.builder().name(roleName).build();
                    roleRepository.save(role);
                });
    }
}
