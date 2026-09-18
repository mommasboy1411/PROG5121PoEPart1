package com.mycompany.prog5121poepart1;

/**
 * Worker class keeping track of user credentials and validating input.
 */
public class Login {

    private String username;
    private String password;
    private String cellNumber;
    private String firstName;
    private String lastName;

    /**
     * Checks if username has an underscore and stays <= 5 chars.
     */
    public boolean checkUserName(String username) {
        return username != null && username.contains("_") && username.length() <= 5;
    }

    /**
     * Ensures password is secure: 8+ chars, capital letter, digit, & special char.
     */
    public boolean checkPasswordComplexity(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }

        boolean hasUpper = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasUpper = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            } else if (!Character.isLetterOrDigit(c)) {
                hasSpecial = true;
            }
        }

        return hasUpper && hasDigit && hasSpecial;
    }

    /**
     * Makes sure cell number uses international format (+27 and 9 digits).
     */
    public boolean checkCellPhoneNumber(String cellNumber) {
        if (cellNumber == null) {
            return false;
        }
        String regex = "^\\+27[0-9]{9}$";
        return cellNumber.matches(regex);
    }

    /**
     * Validates inputs and registers user if everything is sharp.
     */
    public String registerUser(String username, String password, String cellNumber, String firstName, String lastName) {
        if (!checkUserName(username)) {
            return "Username is not correctly formatted, please ensure that your username contains an underscore and is no more than 5 characters in length.";
        }

        if (!checkPasswordComplexity(password)) {
            return "Password is not correctly formatted, please ensure that the password contains at least 8 characters, a capital letter, a number, and a special character.";
        }

        if (!checkCellPhoneNumber(cellNumber)) {
            return "Cell phone number is incorrectly formatted or does not contain the international code (+27).";
        }

        this.username = username;
        this.password = password;
        this.cellNumber = cellNumber;
        this.firstName = firstName;
        this.lastName = lastName;

        return "User successfully registered.";
    }

    /**
     * Verifies stored login credentials.
     */
    public boolean loginUser(String enteredUsername, String enteredPassword) {
        return enteredUsername != null && enteredPassword != null &&
               enteredUsername.equals(this.username) && 
               enteredPassword.equals(this.password);
    }

    /**
     * Returns the official greeting or error string for the rubric.
     */
    public String returnLoginStatus(boolean isLoggedIn) {
        if (isLoggedIn) {
            return "Welcome " + this.firstName + ", " + this.lastName + " it is great to see you.";
        } else {
            return "Username or password incorrect, please try again.";
        }
    }
}