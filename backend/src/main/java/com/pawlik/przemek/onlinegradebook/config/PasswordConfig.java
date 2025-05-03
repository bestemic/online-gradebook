package com.pawlik.przemek.onlinegradebook.config;

import com.pawlik.przemek.onlinegradebook.utils.CustomPasswordGenerator;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class PasswordConfig {

    @Bean
    public PasswordGenerator passwordGenerator() {
        return new PasswordGenerator();
    }

    @Bean
    public List<CharacterRule> passwordRules() {
        return List.of(
                new CharacterRule(EnglishCharacterData.LowerCase, 2),
                new CharacterRule(EnglishCharacterData.UpperCase, 2),
                new CharacterRule(EnglishCharacterData.Digit, 2)
        );
    }

    @Bean
    public CustomPasswordGenerator customPasswordGenerator(PasswordGenerator generator, List<CharacterRule> rules) {
        return new CustomPasswordGenerator(generator, rules);
    }
}
