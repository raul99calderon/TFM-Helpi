package es.upm.miw.helpiadmin.utils;

import android.util.Patterns;

public class Validators {

    public static String validateEmail(String email) {
        if (email.isEmpty())
            return "Email can not be empty";
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
            return "Invalid email address";
        return null;
    }

    public static String validatePassword(String password) {
        if (password.isEmpty())
            return "Password can not be empty";
        if (password.length() < 8)
            return "Minimum 8 characters";
        return null;
    }

    public static String validateName(String name) {
        if (name.isEmpty())
            return "Name can not be empty";
        if (name.length() < 8)
            return "Minimum 8 characters";
        return null;
    }
}
