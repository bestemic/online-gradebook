package com.pawlik.przemek.onlinegradebook.service;

import com.pawlik.przemek.onlinegradebook.dto.role.RoleDto;
import com.pawlik.przemek.onlinegradebook.mapper.RoleMapper;
import com.pawlik.przemek.onlinegradebook.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RoleService {
    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    public RoleService(RoleRepository roleRepository, RoleMapper roleMapper) {
        this.roleRepository = roleRepository;
        this.roleMapper = roleMapper;
    }

    public List<RoleDto> getAll() {
        List<RoleDto> roles = new ArrayList<>();
        roleRepository.findAll().forEach(role -> roles.add(roleMapper.roleToRoleDto(role)));
        return roles;
    }
}
