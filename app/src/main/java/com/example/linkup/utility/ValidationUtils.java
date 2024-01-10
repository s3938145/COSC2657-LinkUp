package com.example.linkup.utility;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationUtils {

    // Validate that a basic text field is not empty
    public static boolean isNotEmpty(String text) {
        return text != null && !text.trim().isEmpty();
    }

    // Validate standard email format
    public static boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        if (email == null) return false;
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    // Validate password
    public static boolean isValidPassword(String password) {
        if (password == null || password.length() < 8 || password.length() > 26) {
            return false;
        }
        boolean hasLetter = false;
        boolean hasDigit = false;
        boolean hasCapital = false;
        boolean hasSpecial = false;

        for (char c : password.toCharArray()) {
            if (Character.isLetter(c)) {
                hasLetter = true;
                if (Character.isUpperCase(c)) hasCapital = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            } else {
                hasSpecial = true;
            }
        }

        return hasLetter && hasDigit && hasCapital && hasSpecial;
    }

    // Validate RMIT ID
    public static boolean isValidRmitId(String rmitId) {
        String rmitIdRegex = "[0-9]{7}$";
        Pattern pattern = Pattern.compile(rmitIdRegex);
        if (rmitId == null) return false;
        Matcher matcher = pattern.matcher(rmitId);
        return matcher.matches();
    }
}

