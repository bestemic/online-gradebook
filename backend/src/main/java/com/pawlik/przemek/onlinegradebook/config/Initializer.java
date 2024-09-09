package com.pawlik.przemek.onlinegradebook.config;

import com.pawlik.przemek.onlinegradebook.service.RoleService;
import com.pawlik.przemek.onlinegradebook.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class Initializer implements CommandLineRunner {

    private final UserService userService;
    private final RoleService roleService;

    public Initializer(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }
    
    @Override
    public void run(String... args) throws Exception {
        roleService.initRoles();
    }
}
