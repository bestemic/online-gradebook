package com.bestemic.onlinegradebook.service;

import com.bestemic.onlinegradebook.constants.SecurityConstants;
import com.bestemic.onlinegradebook.dto.UserLoginDto;
import com.bestemic.onlinegradebook.model.User;
import com.bestemic.onlinegradebook.repository.UserRepository;
import com.bestemic.onlinegradebook.utils.RoleUtils;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final AuthenticationManager authenticationManager;

    public UserService(UserRepository userRepository, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
    }

    public String generateToken(UserLoginDto userLoginDto) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userLoginDto.getEmail(),
                userLoginDto.getPassword());
        try {


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
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw e;
//            System.out.println(e.getMessage());
//            return "test";
        }
    }

    public User getUserByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        return user.orElse(null);
    }

}
