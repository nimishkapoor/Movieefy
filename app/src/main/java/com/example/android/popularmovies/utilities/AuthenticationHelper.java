package com.example.android.popularmovies.utilities;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AuthenticationHelper {
    /**
     * Checks whether an email id is of the correct format or not using regular expression functionality
     * in java. eg - sauravvihak89@gmail.com
     * @param email The email address to be checked for valid format
     * @return boolean denotes whether or not the passed email address has the correct format
     */
    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}