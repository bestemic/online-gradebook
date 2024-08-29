package com.pawlik.przemek.onlinegradebook.service;

import com.pawlik.przemek.onlinegradebook.constants.SecurityConstants;
import com.pawlik.przemek.onlinegradebook.dto.password.ChangePasswordDto;
import com.pawlik.przemek.onlinegradebook.dto.user.UserAddDto;
import com.pawlik.przemek.onlinegradebook.dto.user.UserDto;
import com.pawlik.przemek.onlinegradebook.dto.user.UserLoginDto;
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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class UserService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final PdfService pdfService;

    public UserService(AuthenticationManager authenticationManager, UserRepository userRepository, RoleRepository roleRepository, UserMapper userMapper, PasswordEncoder passwordEncoder, PdfService pdfService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.pdfService = pdfService;
    }

    public String authenticateAndGenerateToken(UserLoginDto userLoginDto) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                userLoginDto.getEmail(),
                userLoginDto.getPassword()
        );

        Authentication authentication = authenticationManager.authenticate(token);

        String jwt = null;
        if (authentication != null) {
            SecretKey key = Keys.hmacShaKeyFor(SecurityConstants.JWT_KEY.getBytes(StandardCharsets.UTF_8));
            Date createDate = new Date();

            User user = userRepository.findByEmail(authentication.getName()).get();
            boolean passwordChanged = user.getPasswordChanged();

            jwt = Jwts.builder()
                    .claim("id", user.getId())
                    .claim("email", authentication.getName())
                    .claim("roles", RoleUtils.populateRoles(authentication.getAuthorities()))
                    .claim("changed", passwordChanged)
                    .issuedAt(createDate)
                    .expiration(new Date(createDate.getTime() + 2700000))
                    .signWith(key).compact();
        }
        return jwt;
    }

    @Transactional
    public String createUser(UserAddDto userAddDto) {
        if (userRepository.findByEmail(userAddDto.getEmail()).isPresent()) {
            throw new CustomValidationException("email", "User with provided email already exists");
        }

        User user = userMapper.userAddDtoToUser(userAddDto);
        Set<Role> roles = StreamSupport.stream(roleRepository.findAllById(userAddDto.getRoleIds()).spliterator(), false)
                .collect(Collectors.toSet());

        if (roles.size() != userAddDto.getRoleIds().size()) {
            throw new CustomValidationException("roleIds", "One or more roles do not exist");
        }

        boolean isStudent = roles.stream().anyMatch(role -> role.getName().equals("ROLE_STUDENT"));
        if (isStudent && user.getBirth() == null) {
            throw new CustomValidationException("birth", "Date of birth is required for students");
        }

        boolean isOtherRole = roles.stream().anyMatch(role -> !role.getName().equals("ROLE_STUDENT"));
        if (isOtherRole && user.getPhoneNumber() == null) {
            throw new CustomValidationException("phoneNumber", "Phone number is required for admin and teacher");
        }

        user.setRoles(roles);
        String password = CustomPasswordGenerator.generatePassword();
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
        return password;
    }

    @Transactional
    public void changePassword(Long userId, ChangePasswordDto changePasswordDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User with provided id does not exist"));
        if (!passwordEncoder.matches(changePasswordDto.getCurrentPassword(), user.getPassword())) {
            throw new CustomValidationException("currentPassword", "Current password don't match");
        }

        user.setPassword(passwordEncoder.encode(changePasswordDto.getNewPassword()));
        user.setPasswordChanged(true);
        userRepository.save(user);
    }

    @Transactional
    public String resetPassword(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User with provided id does not exist"));
        String password = CustomPasswordGenerator.generatePassword();
        user.setPassword(passwordEncoder.encode(password));
        user.setPasswordChanged(false);
        userRepository.save(user);
        return password;
    }

    @Transactional
    public byte[] resetPasswords(List<Long> userIds) {
        List<User> users = (List<User>) userRepository.findAllById(userIds);

        if (users.size() != userIds.size()) {
            throw new NotFoundException("One or more users not found");
        }

        Map<User, String> userPasswordMap = new HashMap<>();

        for (User user : users) {
            String password = CustomPasswordGenerator.generatePassword();
            user.setPassword(passwordEncoder.encode(password));
            user.setPasswordChanged(false);
            userPasswordMap.put(user, password);
        }

        byte[] pdfBytes = pdfService.generateFileWithPasswords(userPasswordMap);
        userRepository.saveAll(users);
        return pdfBytes;
    }

    public List<UserDto> getAllUsers(String roleName) {
        if (roleName != null && !roleName.isEmpty()) {
            return userRepository.findByRolesName(roleName)
                    .stream()
                    .map(userMapper::userToUserDto)
                    .collect(Collectors.toList());
        } else {
            return ((List<User>) userRepository.findAll())
                    .stream()
                    .map(userMapper::userToUserDto)
                    .collect(Collectors.toList());
        }
    }

    public UserDto getUserById(Long userId) {
        User user = getUserObjectById(userId);
        if (hasAccessToUser(userId)) {
            return userMapper.userToUserDto(user);
        } else {
            throw new AccessDeniedException("You do not have permission to access this user");
        }
    }

    public User getUserObjectById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));
    }

    public List<User> getAllStudentsWithIds(List<Long> studentsIds) throws Exception {
        List<User> students = (List<User>) userRepository.findAllById(studentsIds);

        if (students.size() != studentsIds.size()) {
            throw new Exception("Some students do not exist");
        }

        for (User user : students) {
            boolean isStudent = user.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_STUDENT"));
            if (!isStudent) {
                throw new Exception("User with ID: " + user.getId() + " is not a student");
            }
        }

        return students;
    }

    private boolean hasAccessToUser(Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        if (principal instanceof String email) {
            User currentUser = userRepository.findByEmail(email).orElse(null);
            return currentUser != null && (currentUser.getId().equals(userId) || authentication.getAuthorities().stream().anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN")));
        }
        return false;
    }
}