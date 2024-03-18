package com.bestemic.onlinegradebook.utils;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class RoleUtils {

     public static String populateRoles(Collection<? extends GrantedAuthority> collection) {
        Set<String> roles = new HashSet<>();
        for (GrantedAuthority authority : collection) {
            roles.add(authority.getAuthority());
        }
        return String.join(",", roles);
    }
}
