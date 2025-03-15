package com.pawlik.przemek.onlinegradebook.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.passay.CharacterData;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CustomPasswordGenerator {

    public static String generatePassword() {
        PasswordGenerator generator = new PasswordGenerator();

        CharacterData lowerCaseChars = EnglishCharacterData.LowerCase;
        CharacterRule lowerCaseRule = new CharacterRule(lowerCaseChars);
        lowerCaseRule.setNumberOfCharacters(2);

        CharacterData upperCaseChars = EnglishCharacterData.UpperCase;
        CharacterRule upperCaseRule = new CharacterRule(upperCaseChars);
        upperCaseRule.setNumberOfCharacters(2);

        CharacterData digitChars = EnglishCharacterData.Digit;
        CharacterRule digitRule = new CharacterRule(digitChars);
        digitRule.setNumberOfCharacters(2);

        return generator.generatePassword(10, lowerCaseRule, upperCaseRule, digitRule);
    }
}
