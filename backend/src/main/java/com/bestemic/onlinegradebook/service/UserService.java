package com.bestemic.onlinegradebook.service;

import com.bestemic.onlinegradebook.constants.SecurityConstants;
import com.bestemic.onlinegradebook.dto.ChangePasswordDto;
import com.bestemic.onlinegradebook.dto.subject.SubjectDto;
import com.bestemic.onlinegradebook.dto.user.UserAddDto;
import com.bestemic.onlinegradebook.dto.user.UserDto;
import com.bestemic.onlinegradebook.dto.user.UserLoginDto;
import com.bestemic.onlinegradebook.exception.CustomValidationException;
import com.bestemic.onlinegradebook.exception.NotFoundException;
import com.bestemic.onlinegradebook.mapper.SubjectMapper;
import com.bestemic.onlinegradebook.mapper.UserMapper;
import com.bestemic.onlinegradebook.model.Role;
import com.bestemic.onlinegradebook.model.Subject;
import com.bestemic.onlinegradebook.model.User;
import com.bestemic.onlinegradebook.repository.RoleRepository;
import com.bestemic.onlinegradebook.repository.SubjectRepository;
import com.bestemic.onlinegradebook.repository.UserRepository;
import com.bestemic.onlinegradebook.utils.CustomPasswordGenerator;
import com.bestemic.onlinegradebook.utils.RoleUtils;
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
    private final SubjectRepository subjectRepository;
    private final UserMapper userMapper;
    private final SubjectMapper subjectMapper;
    private final PasswordEncoder passwordEncoder;
    private final PdfService pdfService;

    public UserService(AuthenticationManager authenticationManager, UserRepository userRepository, RoleRepository roleRepository, SubjectRepository subjectRepository, UserMapper userMapper, SubjectMapper subjectMapper, PasswordEncoder passwordEncoder, PdfService pdfService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.subjectRepository = subjectRepository;
        this.userMapper = userMapper;
        this.subjectMapper = subjectMapper;
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

    public List<UserDto> getAllUsers() {
        return ((List<User>) userRepository.findAll()).stream()
                .map(userMapper::userToUserDto)
                .collect(Collectors.toList());
    }

    public UserDto getUserById(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));
        if (hasAccessToUser(userId)) {
            return userMapper.userToUserDto(user);
        } else {
            throw new AccessDeniedException("You do not have permission to access this user");
        }
    }

    @Transactional
    public void assignSubjectToTeacher(Long userId, Long subjectId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));

        boolean isTeacher = user.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_TEACHER"));
        if (!isTeacher) {
            throw new CustomValidationException(null, "Only users with role Teacher can be assigned to a subject");
        }

        Subject subject = subjectRepository.findById(subjectId).orElseThrow(() -> new NotFoundException("Subject not found"));
        user.getSubjects().add(subject);
        userRepository.save(user);
    }

    public List<SubjectDto> getSubjectsByUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        return user.getSubjects().stream()
                .map(subjectMapper::subjectToSubjectDto)
                .collect(Collectors.toList());
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