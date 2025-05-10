package com.pawlik.przemek.onlinegradebook.unit.service;

import com.pawlik.przemek.onlinegradebook.constants.SecurityConstants;
import com.pawlik.przemek.onlinegradebook.dto.user.LoginUserDto;
import com.pawlik.przemek.onlinegradebook.exception.NotFoundException;
import com.pawlik.przemek.onlinegradebook.mapper.UserMapper;
import com.pawlik.przemek.onlinegradebook.model.Role;
import com.pawlik.przemek.onlinegradebook.model.User;
import com.pawlik.przemek.onlinegradebook.repository.RoleRepository;
import com.pawlik.przemek.onlinegradebook.repository.UserRepository;
import com.pawlik.przemek.onlinegradebook.service.PdfService;
import com.pawlik.przemek.onlinegradebook.service.UserService;
import com.pawlik.przemek.onlinegradebook.utils.CustomPasswordGenerator;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private CustomPasswordGenerator customPasswordGenerator;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private PdfService pdfService;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("should return token when user credentials are valid")
    void loginUser_shouldReturnToken_whenCredentialsAreValid() {
        // given
        var email = "admin@example.com";
        var password = "secret";
        var loginDto = new LoginUserDto(email, password);
        var user = User.builder()
                .id(1L)
                .email(email)
                .roles(Set.of(Role.builder().name("ROLE_ADMIN").build()))
                .build();

        var auth = new TestingAuthenticationToken(email, password, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));

        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // when
        var tokenDto = userService.loginUser(loginDto);

        // then
        assertNotNull(tokenDto);
        assertNotNull(tokenDto.token());
        assertThat(tokenDto.token(), Matchers.startsWith("ey"));

        verify(authenticationManager).authenticate(any());
        verify(userRepository).findByEmail(email);
        verifyNoMoreInteractions(authenticationManager, userRepository);
        verifyNoInteractions(roleRepository, userMapper, customPasswordGenerator, passwordEncoder, pdfService);
    }

    @Test
    @DisplayName("should throw NotFoundException when user is not found after authentication")
    void loginUser_shouldThrowNotFoundException_whenUserNotFound() {
        // given
        var email = "nonexistent@example.com";
        var password = "12345";
        var loginDto = new LoginUserDto(email, password);

        var auth = new TestingAuthenticationToken(email, password, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));

        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // when
        var exception = assertThrows(NotFoundException.class, () -> userService.loginUser(loginDto));

        // then
        assertThat(exception.getMessage(), containsString("User not found"));

        verify(authenticationManager).authenticate(any());
        verify(userRepository).findByEmail(email);
        verifyNoMoreInteractions(authenticationManager, userRepository);
        verifyNoInteractions(roleRepository, userMapper, customPasswordGenerator, passwordEncoder, pdfService);
    }

    @Test
    @DisplayName("should include all expected claims in token")
    void loginUser_shouldIncludeAllClaims() {
        // given
        var email = "admin@example.com";
        var password = "admin123";
        var loginDto = new LoginUserDto(email, password);
        var user = User.builder()
                .id(42L)
                .email(email)
                .passwordChanged(true)
                .build();

        var auth = new TestingAuthenticationToken(email, password, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));

        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // when
        var tokenDto = userService.loginUser(loginDto);

        // then
        assertNotNull(tokenDto);

        var token = tokenDto.token();
        assertNotNull(token);

        var claims = parseToken(token);
        assertNotNull(claims);
        assertThat(claims.entrySet(), hasSize(6));
        assertThat(claims.get("id", Integer.class), is(42));
        assertThat(claims.get("email", String.class), is(email));
        assertThat(claims.get("changed", Boolean.class), is(true));
        assertThat(claims.get("iat", Long.class), is(notNullValue()));
        assertThat(claims.get("exp", Long.class), is(notNullValue()));
        assertThat(claims.getExpiration().after(claims.getIssuedAt()), is(true));

        var role = claims.get("roles", String.class);
        assertNotNull(role);
        assertThat(role, is("ROLE_ADMIN"));
    }

    @Test
    @DisplayName("should include multiple roles in JWT claims")
    void loginUser_shouldIncludeMultipleRolesInClaims() {
        // given
        var email = "teacher@example.com";
        var password = "!@#$%^&";
        var loginDto = new LoginUserDto(email, password);
        var user = User.builder()
                .id(55L)
                .email(email)
                .passwordChanged(false)
                .build();

        var authorities = List.of(
                new SimpleGrantedAuthority("ROLE_TEACHER"),
                new SimpleGrantedAuthority("ROLE_ADMIN")
        );
        var auth = new TestingAuthenticationToken(email, password, authorities);

        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // when
        var tokenDto = userService.loginUser(loginDto);

        // then
        assertNotNull(tokenDto);

        var token = tokenDto.token();
        assertNotNull(token);

        var claims = parseToken(token);
        assertNotNull(claims);

        var rolesString = claims.get("roles", String.class);
        assertNotNull(rolesString);

        var roles = Arrays.stream(rolesString.split(",")).toList();
        assertThat(roles, hasSize(2));
        assertThat(roles, containsInAnyOrder("ROLE_TEACHER", "ROLE_ADMIN"));
    }

    @Test
    @DisplayName("should handle empty roles in JWT claims when user has no roles")
    void loginUser_shouldHaveEmptyRolesInClaims() {
        // given
        var email = "none@example.com";
        var password = "qwerty";
        var loginDto = new LoginUserDto(email, password);
        var user = User.builder()
                .id(111L)
                .email(email)
                .passwordChanged(true)
                .build();

        var auth = new TestingAuthenticationToken(email, password, List.of());

        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // when
        var tokenDto = userService.loginUser(loginDto);

        // then
        assertNotNull(tokenDto);

        var token = tokenDto.token();
        assertNotNull(token);

        var claims = parseToken(token);
        assertNotNull(claims);

        var rolesString = claims.get("roles", String.class);
        assertNotNull(rolesString);
        assertThat(rolesString, is(emptyString()));
    }

    @Test
    @DisplayName("should have changed=false when user passwordChanged flag is false")
    void loginUser_shouldSetChangedToFalseWhenPasswordNotChanged() {
        // given
        var email = "user@example.com";
        var password = "abc123";
        var loginDto = new LoginUserDto(email, password);
        var user = User.builder()
                .id(99L)
                .email(email)
                .passwordChanged(false)
                .build();

        var auth = new TestingAuthenticationToken(email, password, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));

        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // when
        var tokenDto = userService.loginUser(loginDto);

        // then
        assertNotNull(tokenDto);

        var token = tokenDto.token();
        assertNotNull(token);

        var claims = parseToken(token);
        assertNotNull(claims);
        assertThat(claims.get("changed", Boolean.class), is(false));
    }


    private Claims parseToken(String jwt) {
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(SecurityConstants.JWT_KEY.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseSignedClaims(jwt)
                .getPayload();
    }
}
