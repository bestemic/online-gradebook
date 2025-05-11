package com.pawlik.przemek.onlinegradebook.unit.service;

import com.pawlik.przemek.onlinegradebook.constants.SecurityConstants;
import com.pawlik.przemek.onlinegradebook.dto.password.ChangePasswordDto;
import com.pawlik.przemek.onlinegradebook.dto.user.AddUserDto;
import com.pawlik.przemek.onlinegradebook.dto.user.GetUserDto;
import com.pawlik.przemek.onlinegradebook.dto.user.LoginUserDto;
import com.pawlik.przemek.onlinegradebook.exception.CustomValidationException;
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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.contains;
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

    @Test
    @DisplayName("should add user and generate password")
    void addUser_shouldSaveUserAndGeneratePassword() {
        // given
        var dto = new AddUserDto("Jane", "Doe", "new@example.com", "123456789", LocalDate.of(2000, 1, 1), List.of(1L));
        var user = User.builder()
                .firstName(dto.firstName())
                .lastName(dto.lastName())
                .email(dto.email())
                .phoneNumber(dto.phoneNumber())
                .birth(dto.birth())
                .build();
        var password = "password123";
        var role = Role.builder().id(1L).name("ROLE_ADMIN").build();

        when(userRepository.findByEmail(dto.email())).thenReturn(Optional.empty());
        when(userMapper.userAddDtoToUser(dto)).thenReturn(user);
        when(roleRepository.findAllById(dto.roleIds())).thenReturn(List.of(role));
        when(customPasswordGenerator.generatePassword()).thenReturn(password);
        when(passwordEncoder.encode(password)).thenReturn("encodedPassword");

        // when
        var passwordDto = userService.addUser(dto);

        // then
        assertNotNull(passwordDto);
        assertThat(passwordDto.password(), is(password));

        verify(userRepository).save(argThat(savedUser -> {
            assertThat(savedUser.getFirstName(), is(dto.firstName()));
            assertThat(savedUser.getLastName(), is(dto.lastName()));
            assertThat(savedUser.getEmail(), is(dto.email()));
            assertThat(savedUser.getPhoneNumber(), is(dto.phoneNumber()));
            assertThat(savedUser.getBirth(), is(dto.birth()));
            assertThat(savedUser.getPassword(), is("encodedPassword"));
            assertThat(savedUser.getRoles(), hasSize(1));
            assertThat(savedUser.getRoles(), contains(role));
            assertThat(savedUser.getPasswordChanged(), is(false));
            return true;
        }));
        verify(userRepository).findByEmail(dto.email());
        verify(userMapper).userAddDtoToUser(dto);
        verify(roleRepository).findAllById(dto.roleIds());
        verify(customPasswordGenerator).generatePassword();
        verify(passwordEncoder).encode(password);
        verifyNoMoreInteractions(userRepository, roleRepository, userMapper, customPasswordGenerator, passwordEncoder);
        verifyNoInteractions(authenticationManager, pdfService);
    }

    @Test
    @DisplayName("should throw CustomValidationException when adding user with duplicate email")
    void addUser_shouldThrowCustomValidationException_whenEmailAlreadyExists() {
        // given
        var dto = new AddUserDto("John", "Doe", "test@example.com", null, null, List.of());

        when(userRepository.findByEmail(dto.email())).thenReturn(Optional.of(new User()));

        // when
        var exception = assertThrows(CustomValidationException.class, () -> userService.addUser(dto));

        // then
        assertThat(exception.getField(), is("email"));
        assertThat(exception.getMessage(), is("User with provided email already exists"));

        verify(userRepository).findByEmail(dto.email());
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(authenticationManager, roleRepository, userMapper, customPasswordGenerator, passwordEncoder, pdfService);
    }

    @Test
    @DisplayName("should throw CustomValidationException when adding user with invalid roles")
    void addUser_shouldThrowCustomValidationException_whenInvalidRolesProvided() {
        // given
        var dto = new AddUserDto("John", "Doe", "test@example.com", null, null, List.of(999L));
        var user = User.builder().email(dto.email()).build();

        when(userRepository.findByEmail(dto.email())).thenReturn(Optional.empty());
        when(userMapper.userAddDtoToUser(dto)).thenReturn(user);
        when(roleRepository.findAllById(dto.roleIds())).thenReturn(List.of());

        // when
        var exception = assertThrows(CustomValidationException.class, () -> userService.addUser(dto));

        // then
        assertThat(exception.getField(), is("roleIds"));
        assertThat(exception.getMessage(), is("One or more roles do not exist"));

        verify(userRepository).findByEmail(dto.email());
        verify(userMapper).userAddDtoToUser(dto);
        verify(roleRepository).findAllById(dto.roleIds());
        verifyNoMoreInteractions(userRepository, roleRepository, userMapper);
        verifyNoInteractions(authenticationManager, customPasswordGenerator, passwordEncoder, pdfService);
    }

    @Test
    @DisplayName("should add user with role student and validate required field")
    void addUser_shouldAddStudentAndValidateFields() {
        // given
        var dto = new AddUserDto("Student", "Example", "student@example.com", null, LocalDate.of(2010, 5, 15), List.of(2L));
        var user = User.builder()
                .firstName(dto.firstName())
                .lastName(dto.lastName())
                .email(dto.email())
                .birth(dto.birth())
                .build();
        var password = "studentPassword";
        var role = Role.builder().id(2L).name("ROLE_STUDENT").build();

        when(userRepository.findByEmail(dto.email())).thenReturn(Optional.empty());
        when(userMapper.userAddDtoToUser(dto)).thenReturn(user);
        when(roleRepository.findAllById(dto.roleIds())).thenReturn(List.of(role));
        when(customPasswordGenerator.generatePassword()).thenReturn(password);
        when(passwordEncoder.encode(password)).thenReturn("encodedPassword");

        // when
        var passwordDto = userService.addUser(dto);

        // then
        assertNotNull(passwordDto);
        assertThat(passwordDto.password(), is(password));

        verify(userRepository).save(argThat(savedUser -> {
            assertThat(savedUser.getRoles(), hasSize(1));
            assertThat(savedUser.getRoles(), contains(role));
            assertThat(savedUser.getBirth(), is(dto.birth()));
            return true;
        }));
        verify(userRepository).findByEmail(dto.email());
        verify(userMapper).userAddDtoToUser(dto);
        verify(roleRepository).findAllById(dto.roleIds());
        verify(customPasswordGenerator).generatePassword();
        verify(passwordEncoder).encode(password);
        verifyNoMoreInteractions(userRepository, roleRepository, userMapper, customPasswordGenerator, passwordEncoder);
        verifyNoInteractions(authenticationManager, pdfService);
    }

    @Test
    @DisplayName("should add user with role admin or teacher and validate required fields")
    void addUser_shouldAddAdminOrTeacherAndValidateFields() {
        // given
        var dto = new AddUserDto("Teacher", "Example", "teacher@example.com", "123456789", null, List.of(3L));
        var user = User.builder()
                .firstName(dto.firstName())
                .lastName(dto.lastName())
                .email(dto.email())
                .phoneNumber(dto.phoneNumber())
                .build();
        var password = "teacherPassword";
        var role = Role.builder().id(3L).name("ROLE_TEACHER").build();

        when(userRepository.findByEmail(dto.email())).thenReturn(Optional.empty());
        when(userMapper.userAddDtoToUser(dto)).thenReturn(user);
        when(roleRepository.findAllById(dto.roleIds())).thenReturn(List.of(role));
        when(customPasswordGenerator.generatePassword()).thenReturn(password);
        when(passwordEncoder.encode(password)).thenReturn("encodedPassword");

        // when
        var passwordDto = userService.addUser(dto);

        // then
        assertNotNull(passwordDto);
        assertThat(passwordDto.password(), is(password));

        verify(userRepository).save(argThat(savedUser -> {
            assertThat(savedUser.getRoles(), hasSize(1));
            assertThat(savedUser.getRoles(), contains(role));
            assertThat(savedUser.getPhoneNumber(), is(dto.phoneNumber()));
            return true;
        }));
        verify(userRepository).findByEmail(dto.email());
        verify(userMapper).userAddDtoToUser(dto);
        verify(roleRepository).findAllById(dto.roleIds());
        verify(customPasswordGenerator).generatePassword();
        verify(passwordEncoder).encode(password);
        verifyNoMoreInteractions(userRepository, roleRepository, userMapper, customPasswordGenerator, passwordEncoder);
        verifyNoInteractions(authenticationManager, pdfService);
    }

    @Test
    @DisplayName("should throw CustomValidationException when student birth date is missing")
    void addUser_shouldThrowCustomValidationException_whenStudentBirthDateMissing() {
        // given
        var dto = new AddUserDto("Student", "Example", "student@example.com", null, null, List.of(2L));
        var user = User.builder().email(dto.email()).build();
        var role = Role.builder().id(2L).name("ROLE_STUDENT").build();

        when(userRepository.findByEmail(dto.email())).thenReturn(Optional.empty());
        when(userMapper.userAddDtoToUser(dto)).thenReturn(user);
        when(roleRepository.findAllById(dto.roleIds())).thenReturn(List.of(role));

        // when
        var exception = assertThrows(CustomValidationException.class, () -> userService.addUser(dto));

        // then
        assertThat(exception.getField(), is("birth"));
        assertThat(exception.getMessage(), is("Date of birth is required for students"));

        verify(userRepository).findByEmail(dto.email());
        verify(userMapper).userAddDtoToUser(dto);
        verify(roleRepository).findAllById(dto.roleIds());
        verifyNoMoreInteractions(userRepository, roleRepository, userMapper);
        verifyNoInteractions(authenticationManager, customPasswordGenerator, passwordEncoder, pdfService);
    }

    @Test
    @DisplayName("should throw CustomValidationException when admin or teacher phone number is missing")
    void addUser_shouldThrowException_whenAdminOrTeacherPhoneNumberMissing() {
        // given
        var dto = new AddUserDto("Admin", "Example", "admin@example.com", null, null, List.of(1L));
        var user = User.builder().email(dto.email()).build();
        var role = Role.builder().id(1L).name("ROLE_ADMIN").build();

        when(userRepository.findByEmail(dto.email())).thenReturn(Optional.empty());
        when(userMapper.userAddDtoToUser(dto)).thenReturn(user);
        when(roleRepository.findAllById(dto.roleIds())).thenReturn(List.of(role));

        // when
        var exception = assertThrows(CustomValidationException.class, () -> userService.addUser(dto));

        // then
        assertThat(exception.getField(), is("phoneNumber"));
        assertThat(exception.getMessage(), is("Phone number is required for admin or teacher"));

        verify(userRepository).findByEmail(dto.email());
        verify(userMapper).userAddDtoToUser(dto);
        verify(roleRepository).findAllById(dto.roleIds());
        verifyNoMoreInteractions(userRepository, roleRepository, userMapper);
        verifyNoInteractions(authenticationManager, customPasswordGenerator, passwordEncoder, pdfService);
    }

    @Test
    @DisplayName("should throw CustomValidationException when user is both teacher and student")
    void addUser_shouldThrowException_whenUserIsTeacherAndStudent() {
        // given
        var dto = new AddUserDto("MultiRole", "Example", "multi@example.com", "123456789", LocalDate.of(2010, 5, 15), List.of(2L, 3L));
        var user = User.builder()
                .firstName(dto.firstName())
                .lastName(dto.lastName())
                .email(dto.email())
                .phoneNumber(dto.phoneNumber())
                .birth(dto.birth())
                .build();
        var password = "multiRolePassword";
        var studentRole = Role.builder().id(2L).name("ROLE_STUDENT").build();
        var teacherRole = Role.builder().id(3L).name("ROLE_TEACHER").build();

        when(userRepository.findByEmail(dto.email())).thenReturn(Optional.empty());
        when(userMapper.userAddDtoToUser(dto)).thenReturn(user);
        when(roleRepository.findAllById(dto.roleIds())).thenReturn(List.of(studentRole, teacherRole));
        when(customPasswordGenerator.generatePassword()).thenReturn(password);
        when(passwordEncoder.encode(password)).thenReturn("encodedPassword");

        // when
        var passwordDto = userService.addUser(dto);

        // then
        assertNotNull(passwordDto);
        assertThat(passwordDto.password(), is(password));

        verify(userRepository).save(argThat(savedUser -> {
            assertThat(savedUser.getPhoneNumber(), is(dto.phoneNumber()));
            assertThat(savedUser.getBirth(), is(dto.birth()));
            assertThat(savedUser.getRoles(), hasSize(2));
            assertThat(savedUser.getRoles(), containsInAnyOrder(studentRole, teacherRole));
            return true;
        }));
        verify(userRepository).findByEmail(dto.email());
        verify(userMapper).userAddDtoToUser(dto);
        verify(roleRepository).findAllById(dto.roleIds());
        verify(customPasswordGenerator).generatePassword();
        verify(passwordEncoder).encode(password);
        verifyNoMoreInteractions(userRepository, roleRepository, userMapper, customPasswordGenerator, passwordEncoder);
        verifyNoInteractions(authenticationManager, pdfService);
    }

    @Test
    @DisplayName("should throw CustomValidationException when current password is incorrect")
    void changePassword_shouldThrowException_whenCurrentPasswordIsIncorrect() {
        // given
        var userId = 1L;
        var changePasswordDto = new ChangePasswordDto("wrongPassword", "newPassword");
        var user = User.builder().id(userId).password("encodedCorrectPassword").build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(changePasswordDto.currentPassword(), user.getPassword())).thenReturn(false);

        // when
        var exception = assertThrows(CustomValidationException.class, () -> userService.changePassword(userId, changePasswordDto));

        // then
        assertThat(exception.getField(), is("currentPassword"));
        assertThat(exception.getMessage(), is("Current password doesn't match"));

        verify(userRepository).findById(userId);
        verify(passwordEncoder).matches(changePasswordDto.currentPassword(), user.getPassword());
        verifyNoMoreInteractions(userRepository, passwordEncoder);
        verifyNoInteractions(authenticationManager, pdfService, customPasswordGenerator, roleRepository, userMapper);
    }

    @Test
    @DisplayName("should successfully change password when current password is correct")
    void changePassword_shouldChangePassword_whenCurrentPasswordIsCorrect() {
        // given
        var userId = 1L;
        var changePasswordDto = new ChangePasswordDto("correctPassword", "newPassword");
        var user = User.builder().id(userId).password("encodedCorrectPassword").build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(changePasswordDto.currentPassword(), user.getPassword())).thenReturn(true);
        when(passwordEncoder.encode(changePasswordDto.newPassword())).thenReturn("encodedNewPassword");

        // when
        userService.changePassword(userId, changePasswordDto);

        // then
        verify(userRepository).findById(userId);
        verify(passwordEncoder).matches(changePasswordDto.currentPassword(), "encodedCorrectPassword");
        verify(passwordEncoder).encode(changePasswordDto.newPassword());
        verify(userRepository).save(argThat(savedUser -> {
            assertThat(savedUser.getPassword(), is("encodedNewPassword"));
            assertThat(savedUser.getPasswordChanged(), is(true));
            return true;
        }));
        verifyNoMoreInteractions(userRepository, passwordEncoder);
        verifyNoInteractions(authenticationManager, pdfService, customPasswordGenerator, roleRepository, userMapper);
    }

    @Test
    @DisplayName("should throw NotFoundException when user does not exist")
    void changePassword_shouldThrowException_whenUserDoesNotExist() {
        // given
        var userId = 1L;
        var changePasswordDto = new ChangePasswordDto("currentPassword", "newPassword");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // when
        var exception = assertThrows(NotFoundException.class, () -> userService.changePassword(userId, changePasswordDto));

        // then
        assertThat(exception.getMessage(), is("User not found with ID: " + userId));

        verify(userRepository).findById(userId);
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(authenticationManager, pdfService, customPasswordGenerator, roleRepository, userMapper, passwordEncoder);
    }

    @Test
    @DisplayName("should successfully reset password and set passwordChanged to false")
    void resetPassword_shouldResetPasswordAndSetFlagToFalse() {
        // given
        var userId = 1L;
        var user = User.builder().id(userId).password("oldEncodedPassword").passwordChanged(true).build();
        var newPassword = "newGeneratedPassword";

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(customPasswordGenerator.generatePassword()).thenReturn(newPassword);
        when(passwordEncoder.encode(newPassword)).thenReturn("encodedNewPassword");

        // when
        var passwordDto = userService.resetPassword(userId);

        // then
        assertNotNull(passwordDto);
        assertThat(passwordDto.password(), is(newPassword));

        verify(userRepository).findById(userId);
        verify(customPasswordGenerator).generatePassword();
        verify(passwordEncoder).encode(newPassword);
        verify(userRepository).save(argThat(savedUser -> {
            assertThat(savedUser.getPassword(), is("encodedNewPassword"));
            assertThat(savedUser.getPasswordChanged(), is(false));
            return true;
        }));
        verifyNoMoreInteractions(userRepository, customPasswordGenerator, passwordEncoder);
        verifyNoInteractions(authenticationManager, pdfService, roleRepository, userMapper);
    }

    @Test
    @DisplayName("should throw NotFoundException when user does not exist")
    void resetPassword_shouldThrowException_whenUserDoesNotExist() {
        // given
        var userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // when
        var exception = assertThrows(NotFoundException.class, () -> userService.resetPassword(userId));

        // then
        assertThat(exception.getMessage(), is("User not found with ID: " + userId));

        verify(userRepository).findById(userId);
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(customPasswordGenerator, passwordEncoder, authenticationManager, pdfService, roleRepository, userMapper);
    }

    @Test
    @DisplayName("should handle reset password when user already has passwordChanged set to false")
    void resetPassword_shouldHandleAlreadyFalsePasswordChangedFlag() {
        // given
        var userId = 1L;
        var user = User.builder().id(userId).password("oldEncodedPassword").passwordChanged(false).build();
        var newPassword = "newGeneratedPassword";

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(customPasswordGenerator.generatePassword()).thenReturn(newPassword);
        when(passwordEncoder.encode(newPassword)).thenReturn("encodedNewPassword");

        // when
        var passwordDto = userService.resetPassword(userId);

        // then
        assertNotNull(passwordDto);
        assertThat(passwordDto.password(), is(newPassword));

        verify(userRepository).findById(userId);
        verify(customPasswordGenerator).generatePassword();
        verify(passwordEncoder).encode(newPassword);
        verify(userRepository).save(argThat(savedUser -> {
            assertThat(savedUser.getPassword(), is("encodedNewPassword"));
            assertThat(savedUser.getPasswordChanged(), is(false));
            return true;
        }));
        verifyNoMoreInteractions(userRepository, customPasswordGenerator, passwordEncoder);
        verifyNoInteractions(authenticationManager, pdfService, roleRepository, userMapper);
    }

    @Test
    @DisplayName("should throw NotFoundException when one user is not found")
    void resetPasswords_shouldThrowException_whenOneUserNotFound() {
        // given
        var userIds = List.of(1L, 2L, 3L);
        var user1 = User.builder().id(1L).build();
        var user2 = User.builder().id(2L).build();

        when(userRepository.findAllById(userIds)).thenReturn(List.of(user1, user2));

        // when
        var exception = assertThrows(NotFoundException.class, () -> userService.resetPasswords(userIds));

        // then
        assertThat(exception.getMessage(), is("One or more users not found"));

        verify(userRepository).findAllById(userIds);
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(authenticationManager, pdfService, roleRepository, userMapper, customPasswordGenerator, passwordEncoder);
    }

    @Test
    @DisplayName("should throw NotFoundException when all users are not found")
    void resetPasswords_shouldThrowException_whenAllUsersNotFound() {
        // given
        var userIds = List.of(1L, 2L, 3L);

        when(userRepository.findAllById(userIds)).thenReturn(List.of());

        // when
        var exception = assertThrows(NotFoundException.class, () -> userService.resetPasswords(userIds));

        // then
        assertThat(exception.getMessage(), is("One or more users not found"));

        verify(userRepository).findAllById(userIds);
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(authenticationManager, pdfService, roleRepository, userMapper, customPasswordGenerator, passwordEncoder);
    }

    @Test
    @DisplayName("should reset passwords for multiple users successfully")
    void resetPasswords_shouldResetPasswordsForMultipleUsers() {
        // given
        var userIds = List.of(1L, 2L);
        var user1 = User.builder().id(1L).password("oldPassword1").passwordChanged(true).build();
        var user2 = User.builder().id(2L).password("oldPassword2").passwordChanged(true).build();
        var newPassword1 = "newPassword1";
        var newPassword2 = "newPassword2";

        when(userRepository.findAllById(userIds)).thenReturn(List.of(user1, user2));
        when(customPasswordGenerator.generatePassword()).thenReturn(newPassword1, newPassword2);
        when(passwordEncoder.encode(newPassword1)).thenReturn("encodedNewPassword1");
        when(passwordEncoder.encode(newPassword2)).thenReturn("encodedNewPassword2");
        when(pdfService.generateFileWithPasswords(any())).thenReturn(new byte[0]);

        // when
        var pdfBytes = userService.resetPasswords(userIds);

        // then
        assertNotNull(pdfBytes);

        verify(userRepository).findAllById(userIds);
        verify(customPasswordGenerator, times(2)).generatePassword();
        verify(passwordEncoder).encode(newPassword1);
        verify(passwordEncoder).encode(newPassword2);
        verify(userRepository).saveAll(argThat((List<User> users) -> {
            assertThat(users, hasSize(2));
            assertThat(users, containsInAnyOrder(user1, user2));
            assertThat(users.get(0).getPassword(), is("encodedNewPassword1"));
            assertThat(users.get(0).getPasswordChanged(), is(false));
            assertThat(users.get(1).getPassword(), is("encodedNewPassword2"));
            assertThat(users.get(1).getPasswordChanged(), is(false));
            return true;
        }));
        verify(pdfService).generateFileWithPasswords(anyMap());
        verifyNoMoreInteractions(userRepository, customPasswordGenerator, passwordEncoder, pdfService);
        verifyNoInteractions(authenticationManager, roleRepository, userMapper);
    }

    @Test
    @DisplayName("should return all users when no role is specified")
    void getUsersByRole_shouldReturnAllUsers_whenNoRoleSpecified() {
        // given
        var firstUser = User.builder().id(1L).email("user1@example.com").build();
        var secondUser = User.builder().id(2L).email("user2@example.com").build();
        var users = List.of(firstUser, secondUser);

        when(userRepository.findAll()).thenReturn(users);
        when(userMapper.userToUserDto(any(User.class))).thenAnswer(invocation -> {
            var user = invocation.getArgument(0, User.class);
            return new GetUserDto(user.getId(), null, null, user.getEmail(), null, null, null, null);
        });

        // when
        var result = userService.getUsersByRole(null);

        // then
        assertNotNull(result);
        assertThat(result.users(), hasSize(2));
        assertThat(result.users().stream().map(GetUserDto::email).toList(), containsInAnyOrder("user1@example.com", "user2@example.com"));

        verify(userRepository).findAll();
        verify(userMapper, times(2)).userToUserDto(any(User.class));
        verifyNoMoreInteractions(userRepository, userMapper);
        verifyNoInteractions(authenticationManager, roleRepository, customPasswordGenerator, passwordEncoder, pdfService);
    }

    @Test
    @DisplayName("should return all users when empty role specified")
    void getUsersByRole_shouldReturnAllUsers_whenEmptyRoleSpecified() {
        // given
        var firstUser = User.builder().id(1L).email("user1@example.com").build();
        var secondUser = User.builder().id(2L).email("user2@example.com").build();
        var users = List.of(firstUser, secondUser);

        when(userRepository.findAll()).thenReturn(users);
        when(userMapper.userToUserDto(any(User.class))).thenAnswer(invocation -> {
            var user = invocation.getArgument(0, User.class);
            return new GetUserDto(user.getId(), null, null, user.getEmail(), null, null, null, null);
        });

        // when
        var result = userService.getUsersByRole("");

        // then
        assertNotNull(result);
        assertThat(result.users(), hasSize(2));
        assertThat(result.users().stream().map(GetUserDto::email).toList(), containsInAnyOrder("user1@example.com", "user2@example.com"));

        verify(userRepository).findAll();
        verify(userMapper, times(2)).userToUserDto(any(User.class));
        verifyNoMoreInteractions(userRepository, userMapper);
        verifyNoInteractions(authenticationManager, roleRepository, customPasswordGenerator, passwordEncoder, pdfService);
    }

    @Test
    @DisplayName("should return users with specific role")
    void getUsersByRole_shouldReturnUsersWithSpecificRole() {
        // given
        var roleName = "ROLE_ADMIN";
        var firstUser = User.builder().id(1L).email("admin1@example.com").build();
        var secondUser = User.builder().id(2L).email("admin2@example.com").build();
        var users = List.of(firstUser, secondUser);

        when(userRepository.findByRolesName(roleName)).thenReturn(users);
        when(userMapper.userToUserDto(any(User.class))).thenAnswer(invocation -> {
            var user = invocation.getArgument(0, User.class);
            return new GetUserDto(user.getId(), null, null, user.getEmail(), null, null, null, null);
        });

        // when
        var result = userService.getUsersByRole(roleName);

        // then
        assertNotNull(result);
        assertThat(result.users(), hasSize(2));
        assertThat(result.users().stream().map(GetUserDto::email).toList(), containsInAnyOrder("admin1@example.com", "admin2@example.com"));

        verify(userRepository).findByRolesName(roleName);
        verify(userMapper, times(2)).userToUserDto(any(User.class));
        verifyNoMoreInteractions(userRepository, userMapper);
        verifyNoInteractions(authenticationManager, roleRepository, customPasswordGenerator, passwordEncoder, pdfService);
    }

    @Test
    @DisplayName("should return empty list when no users are found for specific role")
    void getUsersByRole_shouldReturnEmptyList_whenNoUsersFoundForRole() {
        // given
        var roleName = "ROLE_TEACHER";

        when(userRepository.findByRolesName(roleName)).thenReturn(List.of());

        // when
        var result = userService.getUsersByRole(roleName);

        // then
        assertNotNull(result);
        assertThat(result.users(), is(empty()));

        verify(userRepository).findByRolesName(roleName);
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(userMapper, authenticationManager, roleRepository, customPasswordGenerator, passwordEncoder, pdfService);
    }

    @Test
    @DisplayName("should return user details when user is same person")
    void getUserById_shouldReturnUser_whenUserIsSamePerson() {
        // given
        var userId = 1L;
        var user = User.builder().id(userId).email("user@example.com").build();
        var authentication = new TestingAuthenticationToken("user@example.com", null, List.of(new SimpleGrantedAuthority("ROLE_USER")));

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail(authentication.getName())).thenReturn(Optional.of(user));
        when(userMapper.userToUserDto(user)).thenReturn(new GetUserDto(user.getId(), null, null, user.getEmail(), null, null, null, null));

        // when
        var result = userService.getUserById(userId, authentication);

        // then
        assertNotNull(result);
        assertThat(result.id(), is(userId));
        assertThat(result.email(), is("user@example.com"));

        verify(userRepository).findById(userId);
        verify(userRepository).findByEmail(authentication.getName());
        verify(userMapper).userToUserDto(user);
        verifyNoMoreInteractions(userRepository, userMapper);
        verifyNoInteractions(authenticationManager, roleRepository, customPasswordGenerator, passwordEncoder, pdfService);
    }

    @Test
    @DisplayName("should allow admin to access any user")
    void getUserById_shouldAllowAdminAccessToAnyUser() {
        // given
        var userId = 1L;
        var user = User.builder().id(userId).email("user@example.com").build();
        var adminUser = User.builder().id(2L).email("admin@example.com").build();
        var authentication = new TestingAuthenticationToken("admin@example.com", null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail(authentication.getName())).thenReturn(Optional.of(adminUser));
        when(userMapper.userToUserDto(user)).thenReturn(new GetUserDto(user.getId(), null, null, user.getEmail(), null, null, null, null));

        // when
        var result = userService.getUserById(userId, authentication);

        // then
        assertNotNull(result);
        assertThat(result.id(), is(userId));
        assertThat(result.email(), is("user@example.com"));

        verify(userRepository).findById(userId);
        verify(userRepository).findByEmail(authentication.getName());
        verify(userMapper).userToUserDto(user);
        verifyNoMoreInteractions(userRepository, userMapper);
        verifyNoInteractions(authenticationManager, roleRepository, customPasswordGenerator, passwordEncoder, pdfService);
    }

    @Test
    @DisplayName("should throw NotFoundException when admin search for not existing user")
    void getUserById_shouldThrowNotFoundException_whenAdminSearchNotExistingUser() {
        // given
        var userId = 1L;
        var adminUser = User.builder().id(2L).email("admin@example.com").build();
        var authentication = new TestingAuthenticationToken("admin@example.com", null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));

        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        when(userRepository.findByEmail(authentication.getName())).thenReturn(Optional.of(adminUser));

        // when
        var exception = assertThrows(NotFoundException.class, () -> userService.getUserById(userId, authentication));

        // then
        assertThat(exception.getMessage(), is("User not found with ID: " + userId));

        verify(userRepository).findById(userId);
        verify(userRepository).findByEmail(authentication.getName());
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(userMapper, authenticationManager, roleRepository, customPasswordGenerator, passwordEncoder, pdfService);
    }

    @Test
    @DisplayName("should throw AccessDeniedException when access to user is denied")
    void getUserById_shouldThrowAccessDeniedException_whenAccessDenied() {
        // given
        var userId = 1L;
        var otherUser = User.builder().id(2L).email("otheruser@example.com").build();
        var authentication = new TestingAuthenticationToken("otheruser@example.com", null, List.of(new SimpleGrantedAuthority("ROLE_USER")));

        when(userRepository.findByEmail(authentication.getName())).thenReturn(Optional.of(otherUser));

        // when
        var exception = assertThrows(AccessDeniedException.class, () -> userService.getUserById(userId, authentication));

        // then
        assertThat(exception.getMessage(), is("You do not have permission to access this user"));

        verify(userRepository).findByEmail(authentication.getName());
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(userMapper, authenticationManager, roleRepository, customPasswordGenerator, passwordEncoder, pdfService);
    }

    @Test
    @DisplayName("should throw AccessDeniedException when requester not exists")
    void getUserById_shouldThrowAccessDeniedException_whenRequesterNotExists() {
        // given
        var userId = 1L;
        var authentication = new TestingAuthenticationToken("none.existing@example.com", null, List.of(new SimpleGrantedAuthority("ROLE_USER")));

        when(userRepository.findByEmail(authentication.getName())).thenReturn(Optional.empty());

        // when
        var exception = assertThrows(AccessDeniedException.class, () -> userService.getUserById(userId, authentication));

        // then
        assertThat(exception.getMessage(), is("You do not have permission to access this user"));

        verify(userRepository).findByEmail(authentication.getName());
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(userMapper, authenticationManager, roleRepository, customPasswordGenerator, passwordEncoder, pdfService);
    }

    private Claims parseToken(String jwt) {
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(SecurityConstants.JWT_KEY.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseSignedClaims(jwt)
                .getPayload();
    }
}
