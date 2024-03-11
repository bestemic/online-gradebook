package com.bestemic.onlinegradebook.config;


import com.bestemic.onlinegradebook.model.Role;
import com.bestemic.onlinegradebook.model.User;
import com.bestemic.onlinegradebook.service.UserService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class OnlineGradebookAuthenticationProvider implements AuthenticationProvider {

    private final PasswordEncoder passwordEncoder;

    private final UserService userService;

    public OnlineGradebookAuthenticationProvider(PasswordEncoder passwordEncoder, UserService userService) {
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        User user = userService.getUserByEmail(username);

        if (user != null) {
            if (passwordEncoder.matches(password, user.getPassword())) {
                return new UsernamePasswordAuthenticationToken(username, password, getGrantedAuthorities(user.getRoles()));
            } else {
                throw new BadCredentialsException("Invalid password");
            }
        } else {
            throw new BadCredentialsException("No user found with provided email");
        }
    }

    private List<GrantedAuthority> getGrantedAuthorities(Set<Role> authorities) {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        for (Role authority : authorities) {
            grantedAuthorities.add(new SimpleGrantedAuthority(authority.getName()));
        }
        return grantedAuthorities;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }

}
