package com.pawlik.przemek.onlinegradebook.web.controller;

import com.pawlik.przemek.onlinegradebook.config.SecurityConfig;
import com.pawlik.przemek.onlinegradebook.controller.RoleController;
import com.pawlik.przemek.onlinegradebook.dto.role.GetRoleDto;
import com.pawlik.przemek.onlinegradebook.dto.role.GetRolesDto;
import com.pawlik.przemek.onlinegradebook.service.RoleService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RoleController.class)
@Import(SecurityConfig.class)
class RoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RoleService roleService;

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("should return all roles when user has ADMIN role")
    void getAllRoles_shouldReturnAllRoles_whenUserHasAdminRole() throws Exception {
        // given
        var role1 = new GetRoleDto(1L, "ROLE_ADMIN");
        var role2 = new GetRoleDto(2L, "ROLE_TEACHER");
        var role3 = new GetRoleDto(3L, "ROLE_STUDENT");
        GetRolesDto rolesDto = new GetRolesDto(List.of(role1, role2, role3));

        when(roleService.getAllRoles()).thenReturn(rolesDto);

        // when & then
        mockMvc.perform(get("/api/v1/roles").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.roles").exists())
                .andExpect(jsonPath("$.roles", hasSize(3)))
                .andExpect(jsonPath("$.roles[0].id").value(1L))
                .andExpect(jsonPath("$.roles[0].name").value("ROLE_ADMIN"))
                .andExpect(jsonPath("$.roles[1].id").value(2L))
                .andExpect(jsonPath("$.roles[1].name").value("ROLE_TEACHER"))
                .andExpect(jsonPath("$.roles[2].id").value(3L))
                .andExpect(jsonPath("$.roles[2].name").value("ROLE_STUDENT"));

        verify(roleService, times(1)).getAllRoles();
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "TEACHER"})
    @DisplayName("should return roles when user has at least one valid role")
    void getAllRoles_shouldReturnRoles_whenUserHasAtLeastOneValidRole() throws Exception {
        // given
        var role = new GetRoleDto(6L, "ROLE_ADMIN");
        GetRolesDto rolesDto = new GetRolesDto(List.of(role));

        when(roleService.getAllRoles()).thenReturn(rolesDto);

        // when & then
        mockMvc.perform(get("/api/v1/roles").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.roles").exists())
                .andExpect(jsonPath("$.roles", hasSize(1)))
                .andExpect(jsonPath("$.roles[0].id").value(6L))
                .andExpect(jsonPath("$.roles[0].name").value("ROLE_ADMIN"));

        verify(roleService, times(1)).getAllRoles();
    }

    @Test
    @WithAnonymousUser
    @DisplayName("should return 401 Unauthorized when user is not authenticated")
    void getAllRoles_shouldReturnUnauthorized_whenUserIsNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/roles").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.title").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Full authentication is required to access this resource"));
    }

    @Test
    @WithMockUser(roles = {"STUDENT", "TEACHER"})
    @DisplayName("should return 403 Forbidden when user lacks ADMIN role")
    void getAllRoles_shouldReturnForbidden_whenUserLacksAdminRole() throws Exception {
        mockMvc.perform(get("/api/v1/roles").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.title").value("Forbidden"))
                .andExpect(jsonPath("$.message").value("Access Denied"));
    }
}
