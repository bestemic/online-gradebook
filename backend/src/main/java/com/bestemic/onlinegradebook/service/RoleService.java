package com.bestemic.onlinegradebook.service;

import com.bestemic.onlinegradebook.dto.role.RoleDto;
import com.bestemic.onlinegradebook.mapper.RoleMapper;
import com.bestemic.onlinegradebook.repository.RoleRepository;
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
