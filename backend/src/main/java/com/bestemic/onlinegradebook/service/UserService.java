package com.bestemic.onlinegradebook.service;

import com.bestemic.onlinegradebook.constants.SecurityConstants;
import com.bestemic.onlinegradebook.dto.UserAddDto;
import com.bestemic.onlinegradebook.dto.ChangePasswordDto;
import com.bestemic.onlinegradebook.dto.UserLoginDto;
import com.bestemic.onlinegradebook.exception.CustomValidationException;
import com.bestemic.onlinegradebook.exception.NotFoundException;
import com.bestemic.onlinegradebook.mapper.UserMapper;
import com.bestemic.onlinegradebook.model.Role;
import com.bestemic.onlinegradebook.model.User;
import com.bestemic.onlinegradebook.repository.RoleRepository;
import com.bestemic.onlinegradebook.repository.UserRepository;
import com.bestemic.onlinegradebook.utils.CustomPasswordGenerator;
import com.bestemic.onlinegradebook.utils.RoleUtils;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class UserService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(AuthenticationManager authenticationManager, UserRepository userRepository, RoleRepository roleRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
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
            jwt = Jwts.builder()
                    .claim("email", authentication.getName())
                    .claim("roles", RoleUtils.populateRoles(authentication.getAuthorities()))
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
}