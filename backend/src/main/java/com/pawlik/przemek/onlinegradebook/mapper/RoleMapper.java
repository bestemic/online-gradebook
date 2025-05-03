package com.pawlik.przemek.onlinegradebook.mapper;

import com.pawlik.przemek.onlinegradebook.dto.role.GetRoleDto;
import com.pawlik.przemek.onlinegradebook.dto.role.GetRolesDto;
import com.pawlik.przemek.onlinegradebook.model.Role;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.Set;


@Mapper(componentModel = "spring")
public interface RoleMapper {

    GetRoleDto roleToRoleDto(Role role);

    default GetRolesDto rolesToGetRolesDto(Set<Role> roles) {
        List<GetRoleDto> roleDtos = roles.stream()
                .map(this::roleToRoleDto)
                .toList();
        return new GetRolesDto(roleDtos);
    }
}
