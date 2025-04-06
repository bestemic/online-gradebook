package com.pawlik.przemek.onlinegradebook.unit.service;

import com.pawlik.przemek.onlinegradebook.dto.role.GetRoleDto;
import com.pawlik.przemek.onlinegradebook.mapper.RoleMapper;
import com.pawlik.przemek.onlinegradebook.model.Role;
import com.pawlik.przemek.onlinegradebook.repository.RoleRepository;
import com.pawlik.przemek.onlinegradebook.service.RoleService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private RoleMapper roleMapper;

    @InjectMocks
    private RoleService roleService;

    @Test
    @DisplayName("should return multiple roles when they exist in the database")
    void getAllRoles_shouldReturnMultipleRoles_whenTheyExist() {
        // given
        var role1 = Role.builder().name("ROLE_ADMIN").build();
        var role2 = Role.builder().name("ROLE_TEACHER").build();
        var roleEntities = List.of(role1, role2);

        var roleDto1 = new GetRoleDto(1L, "ROLE_ADMIN");
        var roleDto2 = new GetRoleDto(2L, "ROLE_TEACHER");

        when(roleRepository.findAll()).thenReturn(roleEntities);
        when(roleMapper.roleToRoleDto(role1)).thenReturn(roleDto1);
        when(roleMapper.roleToRoleDto(role2)).thenReturn(roleDto2);

        // when
        var result = roleService.getAllRoles();

        // then
        assertNotNull(result);
        assertThat(result.roles(), hasSize(2));
        assertThat(result.roles().get(0).name(), is("ROLE_ADMIN"));
        assertThat(result.roles().get(1).name(), is("ROLE_TEACHER"));

        verify(roleRepository).findAll();
        verify(roleMapper).roleToRoleDto(role1);
        verify(roleMapper).roleToRoleDto(role2);
        verifyNoMoreInteractions(roleRepository, roleMapper);
    }

    @Test
    @DisplayName("should return an empty list when no roles are present in the database")
    void getAllRoles_shouldReturnEmptyList_whenNoRolesExist() {
        // given
        when(roleRepository.findAll()).thenReturn(Collections.emptyList());

        // when
        var result = roleService.getAllRoles();

        // then
        assertNotNull(result);
        assertThat(result.roles(), hasSize(0));

        verify(roleRepository).findAll();
        verifyNoInteractions(roleMapper);
    }

    @Test
    @DisplayName("should return a single role when only one is present")
    void getAllRoles_shouldReturnSingleRole_whenOnlyOneRoleExists() {
        // given
        var role = Role.builder().name("ROLE_ADMIN").build();
        var roleDto = new GetRoleDto(1L, "ROLE_ADMIN");

        when(roleRepository.findAll()).thenReturn(List.of(role));
        when(roleMapper.roleToRoleDto(role)).thenReturn(roleDto);

        // when
        var result = roleService.getAllRoles();

        // then
        assertNotNull(result);
        assertThat(result.roles(), hasSize(1));
        assertThat(result.roles().getFirst().name(), is("ROLE_ADMIN"));

        verify(roleRepository).findAll();
        verify(roleMapper).roleToRoleDto(role);
        verifyNoMoreInteractions(roleRepository, roleMapper);
    }

    @Test
    @DisplayName("should save all roles when none are present in the database")
    void initRoles_shouldSaveAllRoles_whenNoneExist() {
        // given
        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.empty());
        when(roleRepository.findByName("ROLE_TEACHER")).thenReturn(Optional.empty());
        when(roleRepository.findByName("ROLE_STUDENT")).thenReturn(Optional.empty());

        // when
        roleService.initRoles();

        // then
        verify(roleRepository).save(argThat(role -> "ROLE_ADMIN".equals(role.getName())));
        verify(roleRepository).save(argThat(role -> "ROLE_TEACHER".equals(role.getName())));
        verify(roleRepository).save(argThat(role -> "ROLE_STUDENT".equals(role.getName())));
    }

    @Test
    @DisplayName("should not save roles if all are already present")
    void initRoles_shouldDoNothing_whenAllRolesAlreadyExist() {
        // given
        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.of(Role.builder().name("ROLE_ADMIN").build()));
        when(roleRepository.findByName("ROLE_TEACHER")).thenReturn(Optional.of(Role.builder().name("ROLE_TEACHER").build()));
        when(roleRepository.findByName("ROLE_STUDENT")).thenReturn(Optional.of(Role.builder().name("ROLE_STUDENT").build()));

        // when
        roleService.initRoles();

        // then
        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    @DisplayName("should save only missing roles when some already exist")
    void initRoles_shouldSaveMissingRoles_whenSomeRolesAlreadyExist() {
        // given
        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.of(Role.builder().name("ROLE_ADMIN").build()));
        when(roleRepository.findByName("ROLE_TEACHER")).thenReturn(Optional.empty());
        when(roleRepository.findByName("ROLE_STUDENT")).thenReturn(Optional.empty());

        // when
        roleService.initRoles();

        // then
        verify(roleRepository, never()).save(argThat(role -> "ROLE_ADMIN".equals(role.getName())));
        verify(roleRepository).save(argThat(role -> "ROLE_TEACHER".equals(role.getName())));
        verify(roleRepository).save(argThat(role -> "ROLE_STUDENT".equals(role.getName())));
    }
}
