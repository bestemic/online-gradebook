package com.bestemic.onlinegradebook.config.filter;

import com.bestemic.onlinegradebook.constants.SecurityConstants;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class JWTTokenGeneratorFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            SecretKey key = Keys.hmacShaKeyFor(SecurityConstants.JWT_KEY.getBytes(StandardCharsets.UTF_8));
            Date createDate = new Date();
            String jwt = Jwts.builder()
                    .claim("email", authentication.getName())
                    .claim("roles", populateRoles(authentication.getAuthorities()))
                    .issuedAt(createDate)
                    .expiration(new Date(createDate.getTime() + 2700000))
                    .signWith(key).compact();
            response.setHeader(SecurityConstants.JWT_HEADER, jwt);
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getServletPath().contains("/login");
    }

    private String populateRoles(Collection<? extends GrantedAuthority> collection) {
        Set<String> roles = new HashSet<>();
        for (GrantedAuthority authority : collection) {
            roles.add(authority.getAuthority());
        }
        return String.join(",", roles);
    }

}
