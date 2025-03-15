package com.pawlik.przemek.onlinegradebook.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RoleUtils {

    public static String populateRoles(Collection<? extends GrantedAuthority> collection) {
        Set<String> roles = new HashSet<>();
        for (GrantedAuthority authority : collection) {
            roles.add(authority.getAuthority());
        }
        return String.join(",", roles);
    }
}
