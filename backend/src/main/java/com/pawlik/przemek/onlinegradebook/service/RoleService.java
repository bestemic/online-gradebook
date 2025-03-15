package com.pawlik.przemek.onlinegradebook.service;

import com.pawlik.przemek.onlinegradebook.dto.role.RoleDto;
import com.pawlik.przemek.onlinegradebook.mapper.RoleMapper;
import com.pawlik.przemek.onlinegradebook.model.Role;
import com.pawlik.przemek.onlinegradebook.repository.RoleRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    public List<RoleDto> getAll() {
        List<RoleDto> roles = new ArrayList<>();
        roleRepository.findAll().forEach(role -> roles.add(roleMapper.roleToRoleDto(role)));
        return roles;
    }

    public void initRoles() {
        String[] roleNames = {"ROLE_ADMIN", "ROLE_TEACHER", "ROLE_STUDENT"};

        for (String roleName : roleNames) {
            if (roleRepository.findByName(roleName).isEmpty()) {
                Role role = new Role();
                role.setName(roleName);
                roleRepository.save(role);
            }
        }
    }
}
