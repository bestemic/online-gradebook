package com.pawlik.przemek.onlinegradebook.service;

import com.pawlik.przemek.onlinegradebook.constants.SecurityConstants;
import com.pawlik.przemek.onlinegradebook.dto.password.ChangePasswordDto;
import com.pawlik.przemek.onlinegradebook.dto.password.GetPasswordDto;
import com.pawlik.przemek.onlinegradebook.dto.token.GetTokenDto;
import com.pawlik.przemek.onlinegradebook.dto.user.AddUserDto;
import com.pawlik.przemek.onlinegradebook.dto.user.GetUserDto;
import com.pawlik.przemek.onlinegradebook.dto.user.GetUsersDto;
import com.pawlik.przemek.onlinegradebook.dto.user.LoginUserDto;
import com.pawlik.przemek.onlinegradebook.exception.CustomValidationException;
import com.pawlik.przemek.onlinegradebook.exception.NotFoundException;
import com.pawlik.przemek.onlinegradebook.mapper.UserMapper;
import com.pawlik.przemek.onlinegradebook.model.Role;
import com.pawlik.przemek.onlinegradebook.model.User;
import com.pawlik.przemek.onlinegradebook.repository.RoleRepository;
import com.pawlik.przemek.onlinegradebook.repository.UserRepository;
import com.pawlik.przemek.onlinegradebook.utils.CustomPasswordGenerator;
import com.pawlik.przemek.onlinegradebook.utils.RoleUtils;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;

@Service
@AllArgsConstructor
@Slf4j
public class UserService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final CustomPasswordGenerator customPasswordGenerator;
    private final PasswordEncoder passwordEncoder;
    private final PdfService pdfService;

    public GetTokenDto loginUser(LoginUserDto loginUserDto) {
        var authToken = new UsernamePasswordAuthenticationToken(loginUserDto.email(), loginUserDto.password());
        var authentication = authenticationManager.authenticate(authToken);

        var user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new NotFoundException("User not found"));

        var jwt = generateJwtToken(authentication, user);
        return new GetTokenDto(jwt);
    }

    @Transactional
    public GetPasswordDto addUser(AddUserDto addUserDto) {
        userRepository.findByEmail(addUserDto.email()).ifPresent(user -> {
            throw new CustomValidationException("email", "User with provided email already exists");
        });

        var user = userMapper.userAddDtoToUser(addUserDto);
        var roles = validateRolesExist(addUserDto.roleIds());
        validateUserRequiredFields(user, roles);

        user.setRoles(roles);
        var password = customPasswordGenerator.generatePassword();
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);

        return new GetPasswordDto(password);
    }

    @Transactional
    public void changePassword(Long userId, ChangePasswordDto changePasswordDto) {
        var user = getUserEntityById(userId);
        if (!passwordEncoder.matches(changePasswordDto.currentPassword(), user.getPassword())) {
            throw new CustomValidationException("currentPassword", "Current password doesn't match");
        }

        user.setPassword(passwordEncoder.encode(changePasswordDto.newPassword()));
        user.setPasswordChanged(true);
        userRepository.save(user);
    }

    @Transactional
    public GetPasswordDto resetPassword(Long userId) {
        var user = getUserEntityById(userId);
        var password = resetPasswordForUser(user);
        userRepository.save(user);

        return new GetPasswordDto(password);
    }

    @Transactional
    public byte[] resetPasswords(List<Long> userIds) {
        var users = userRepository.findAllById(userIds);
        if (users.size() != userIds.size()) {
            throw new NotFoundException("One or more users not found");
        }

        var userPasswordMap = resetPasswordsForUsers(users);
        userRepository.saveAll(users);
        return pdfService.generateFileWithPasswords(userPasswordMap);
    }

    public GetUsersDto getUsersByRole(String roleName) {
        var users = (roleName != null && !roleName.isEmpty())
                ? userRepository.findByRolesName(roleName)
                : userRepository.findAll();

        var userDtos = users.stream().map(userMapper::userToUserDto).toList();
        return new GetUsersDto(userDtos);
    }

    public GetUserDto getUserById(Long userId, Authentication authentication) {
        if (!hasAccessToUser(userId, authentication)) {
            throw new AccessDeniedException("You do not have permission to access this user");
        }

        var user = getUserEntityById(userId);
        return userMapper.userToUserDto(user);
    }

    public List<User> getAllStudentsWithIds(List<Long> studentIds) {
        var students = userRepository.findAllById(studentIds);
        if (students.size() != studentIds.size()) {
            throw new NotFoundException("Some students do not exist");
        }

        validateUsersAreStudents(students);
        return students;
    }

    @Transactional
    public void initUser(String firstName, String lastName, String email, String password, String phoneNumber, LocalDate birth, String roles) {
        if (userRepository.findByEmail(email).isPresent()) {
            log.info("User with provided email already exists");
            return;
        }

        var userRoles = validateInitialRoles(roles);
        var user = User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .password(passwordEncoder.encode(password))
                .phoneNumber(phoneNumber)
                .birth(birth)
                .roles(userRoles)
                .build();

        validateUserRequiredFields(user, userRoles);
        userRepository.save(user);
    }

    public User getUserEntityById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));
    }

    private String generateJwtToken(Authentication authentication, User user) {
        var key = Keys.hmacShaKeyFor(SecurityConstants.JWT_KEY.getBytes(StandardCharsets.UTF_8));
        var issuedAt = new Date();
        var expiration = new Date(issuedAt.getTime() + SecurityConstants.JWT_EXPIRATION_MILLIS);

        return Jwts.builder()
                .claim("id", user.getId())
                .claim("email", authentication.getName())
                .claim("roles", RoleUtils.populateRoles(authentication.getAuthorities()))
                .claim("changed", user.getPasswordChanged())
                .issuedAt(issuedAt)
                .expiration(expiration)
                .signWith(key)
                .compact();
    }

    private boolean hasAccessToUser(Long userId, Authentication authentication) {
        var currentUser = getCurrentUser(authentication);
        return currentUser != null && (isSameUser(currentUser.getId(), userId) || isAdmin(authentication));
    }

    private User getCurrentUser(Authentication authentication) {
        return userRepository.findByEmail(authentication.getName()).orElse(null);
    }

    private boolean isSameUser(Long currentUserId, Long targetUserId) {
        return currentUserId.equals(targetUserId);
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(auth -> "ROLE_ADMIN".equals(auth.getAuthority()));
    }

    private Set<Role> validateRolesExist(List<Long> roleIds) {
        var roles = new HashSet<>(roleRepository.findAllById(roleIds));
        if (roles.size() != roleIds.size()) {
            throw new CustomValidationException("roleIds", "One or more roles do not exist");
        }

        return roles;
    }

    private void validateUserRequiredFields(User user, Set<Role> roles) {
        var isStudent = roles.stream().anyMatch(role -> role.getName().equals("ROLE_STUDENT"));
        var isOther = roles.stream().anyMatch(role -> !role.getName().equals("ROLE_STUDENT"));

        if (isStudent && user.getBirth() == null) {
            throw new CustomValidationException("birth", "Date of birth is required for students");
        }
        if (isOther && user.getPhoneNumber() == null) {
            throw new CustomValidationException("phoneNumber", "Phone number is required for admin or teacher");
        }
    }

    private Set<Role> validateInitialRoles(String rolesString) {
        var rolesArray = rolesString.split(",");
        var roles = new HashSet<Role>();

        for (var roleName : rolesArray) {
            var role = roleRepository.findByName(roleName.trim().toUpperCase())
                    .orElseThrow(() -> new NotFoundException("Role not found: " + roleName));
            roles.add(role);
        }

        return roles;
    }

    private void validateUsersAreStudents(List<User> users) {
        for (var user : users) {
            boolean isStudent = user.getRoles().stream()
                    .anyMatch(role -> "ROLE_STUDENT".equals(role.getName()));
            if (!isStudent) {
                throw new IllegalStateException("User with ID: " + user.getId() + " is not a student");
            }
        }
    }

    private String resetPasswordForUser(User user) {
        var password = customPasswordGenerator.generatePassword();
        user.setPassword(passwordEncoder.encode(password));
        user.setPasswordChanged(false);
        return password;
    }

    private Map<User, String> resetPasswordsForUsers(List<User> users) {
        var userPasswordMap = new HashMap<User, String>();
        for (var user : users) {
            var password = resetPasswordForUser(user);
            userPasswordMap.put(user, password);
        }
        return userPasswordMap;
    }
}
