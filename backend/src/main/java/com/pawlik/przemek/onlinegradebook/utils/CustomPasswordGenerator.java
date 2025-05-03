package com.pawlik.przemek.onlinegradebook.utils;

import lombok.AllArgsConstructor;
import org.passay.CharacterRule;
import org.passay.PasswordGenerator;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class CustomPasswordGenerator {

    private static final int DEFAULT_PASSWORD_LENGTH = 10;

    private final PasswordGenerator generator;
    private final List<CharacterRule> rules;

    public String generatePassword() {
        return generator.generatePassword(DEFAULT_PASSWORD_LENGTH, rules);
    }
}
