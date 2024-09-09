package com.pawlik.przemek.onlinegradebook.config;

import com.pawlik.przemek.onlinegradebook.service.RoleService;
import com.pawlik.przemek.onlinegradebook.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

import static java.lang.System.exit;

@Component
public class Initializer implements CommandLineRunner {

    private final static Logger LOGGER = LoggerFactory.getLogger(Initializer.class);

    private final UserService userService;
    private final RoleService roleService;

    public Initializer(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @Override
    public void run(String... args) throws Exception {
        roleService.initRoles();
        parseArgsAndInitUser(args);
    }

    private void parseArgsAndInitUser(String... args) {
        String firstName = null;
        String lastName = null;
        String email = null;
        String password = null;
        String phoneNumber = null;
        LocalDate birth = null;
        String roles = null;

        for (String arg : args) {
            if (arg.startsWith("--firstName=")) {
                firstName = arg.substring("--firstName=".length());
            } else if (arg.startsWith("--lastName=")) {
                lastName = arg.substring("--lastName=".length());
            } else if (arg.startsWith("--email=")) {
                email = arg.substring("--email=".length());
            } else if (arg.startsWith("--password=")) {
                password = arg.substring("--password=".length());
            } else if (arg.startsWith("--roles=")) {
                roles = arg.substring("--roles=".length());
            } else if (arg.startsWith("--phoneNumber=")) {
                phoneNumber = arg.substring("--phoneNumber=".length());
            } else if (arg.startsWith("--birth=")) {
                birth = LocalDate.parse(arg.substring("--birth=".length()));
            }
        }

        if (firstName == null || lastName == null || email == null || password == null || roles == null) {
            LOGGER.error("Missing required user data (firstName, lastName, email, password, roles)");
            exit(1);
        }

        try {
            userService.initUser(firstName, lastName, email, password, phoneNumber, birth, roles);
        } catch (Exception e) {
            LOGGER.error("Error while initializing user: " + e.getMessage());
            exit(1);
        }
    }
}
