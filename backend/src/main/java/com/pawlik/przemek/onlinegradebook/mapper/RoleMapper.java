package com.pawlik.przemek.onlinegradebook.mapper;

import com.pawlik.przemek.onlinegradebook.dto.role.GetRoleDto;
import com.pawlik.przemek.onlinegradebook.model.Role;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface RoleMapper {
    GetRoleDto roleToRoleDto(Role role);
}
