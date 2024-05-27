package com.bestemic.onlinegradebook.mapper;

import com.bestemic.onlinegradebook.dto.RoleDto;
import com.bestemic.onlinegradebook.model.Role;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface RoleMapper {
    RoleDto roleToRoleDto(Role role);
}