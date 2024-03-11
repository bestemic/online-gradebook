package com.bestemic.onlinegradebook.controller;

import com.bestemic.onlinegradebook.model.User;
import com.bestemic.onlinegradebook.repository.UserRepository;
import com.bestemic.onlinegradebook.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Optional;

@RestController
@RequestMapping(path = "/api/v1/users")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @RequestMapping("/login")
    public User getUserDetailsAfterLogin(Authentication authentication) {
        Optional<User> user = userRepository.findByEmail(authentication.getName());
        return user.orElse(null);
    }

    @PostMapping
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        User savedCustomer = null;
        ResponseEntity response = null;
        try {
            String hashPwd = passwordEncoder.encode(user.getPassword());
            user.setPassword(hashPwd);
            user.setCreateDt(String.valueOf(new Date(System.currentTimeMillis())));
            savedCustomer = userRepository.save(user);
            if (savedCustomer.getId() > 0) {
                response = ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body("Given user details are successfully registered");
            }
        } catch (Exception ex) {
            response = ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An exception occured due to " + ex.getMessage());
        }
        return response;
    }

//    @PostMapping("/signup")
//    public ResponseEntity<?> registerUser(@RequestBody SignUpDto signUpDto) {
//        // checking for username exists in a database
//        if (userRepository.existsByUserName(signUpDto.getUsername())) {
//            return new ResponseEntity<>("Username is already exist!", HttpStatus.BAD_REQUEST);
//        }
//        // checking for email exists in a database
//        if (userRepository.existsByEmail(signUpDto.getEmail())) {
//            return new ResponseEntity<>("Email is already exist!", HttpStatus.BAD_REQUEST);
//        }
//        // creating user object
//        User user = new User();
//        user.setName(signUpDto.getName());
//        user.setUserName(signUpDto.getUsername());
//        user.setEmail(signUpDto.getEmail());
//        user.setPassword(passwordEncoder.encode(signUpDto.getPassword()));
//        Role roles = roleRepository.findByName("ROLE_ADMIN").get();
//        user.setRoles(Collections.singleton(roles));
//        userRepository.save(user);
//        return new ResponseEntity<>("User is registered successfully!", HttpStatus.OK);
//    }

}
