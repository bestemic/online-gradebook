package com.pawlik.przemek.onlinegradebook.config;

import com.pawlik.przemek.onlinegradebook.service.RoleService;
import com.pawlik.przemek.onlinegradebook.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

import static java.lang.System.exit;

@Component
@AllArgsConstructor
@Slf4j
public class Initializer implements CommandLineRunner {

    private final UserService userService;
    private final RoleService roleService;

    @Override
    public void run(String... args) throws Exception {
        roleService.initRoles();
        initUser();
    }

    private void initUser() {
        String createUser = System.getenv("INIT_USER");
        if (createUser != null && createUser.equals("true")) {
            String firstName = System.getenv("FIRST_NAME");
            String lastName = System.getenv("LAST_NAME");
            String email = System.getenv("EMAIL");
            String password = System.getenv("PASSWORD");
            String phoneNumber = System.getenv("PHONE_NUMBER");
            String roles = System.getenv("ROLES");
            String birthString = System.getenv("BIRTH");
            LocalDate birth = birthString != null && !birthString.isEmpty() ? LocalDate.parse(birthString) : null;

            if (firstName == null || lastName == null || email == null || password == null || roles == null) {
                log.error("Missing required user data (firstName, lastName, email, password, roles)");
                exit(1);
            }

            try {
                userService.initUser(firstName, lastName, email, password, phoneNumber, birth, roles);
            } catch (Exception e) {
                log.error("Error while initializing user: {}", e.getMessage());
                exit(1);
            }
        }
    }
}
