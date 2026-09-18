package com.mycompany.prog5121poepart1;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Login class ensuring credential validation and 
 * authentication logic meet specification criteria.
 */
public class LoginTest {

    private Login login;

    @BeforeEach
    public void setUp() {
        login = new Login();
    }

    @Test
    public void testCheckUserNameValid() {
        // Valid username: contains '_' and is <= 5 characters
        assertTrue(login.checkUserName("kyl_1"));
    }

    @Test
    public void testCheckUserNameInvalid() {
        // Invalid username: longer than 5 characters
        assertFalse(login.checkUserName("kyle!!!!!!!"));
    }

    @Test
    public void testCheckPasswordSuccess() {
        // Valid password: 8+ chars, upper, digit, special character
        assertTrue(login.checkPasswordComplexity("Ch3ss#123"));
    }

    @Test
    public void testCheckPasswordFailure() {
        // Invalid password: lacks uppercase, digits, special characters, and length
        assertFalse(login.checkPasswordComplexity("password"));
    }

    @Test
    public void testCheckCellPhoneNumberValid() {
        // Valid SA number format with +27 international code
        assertTrue(login.checkCellPhoneNumber("+27831234567"));
    }

    @Test
    public void testCheckCellPhoneNumberInvalid() {
        // Invalid number format: missing +27 international code
        assertFalse(login.checkCellPhoneNumber("0831234567"));
    }

    @Test
    public void testLoginUserSuccess() {
        // Test successful user registration and matching login credentials
        login.registerUser("kyl_1", "Ch3ss#123", "+27831234567", "Kyle", "Ntokozo");
        assertTrue(login.loginUser("kyl_1", "Ch3ss#123"));
    }

    @Test
    public void testLoginUserFailure() {
        // Test login failure with mismatched password
        login.registerUser("kyl_1", "Ch3ss#123", "+27831234567", "Kyle", "Ntokozo");
        assertFalse(login.loginUser("kyl_1", "wrong_pass"));
    }

    @Test
    public void testReturnLoginStatusSuccess() {
        // Test return message for successful login
        login.registerUser("kyl_1", "Ch3ss#123", "+27831234567", "Kyle", "Ntokozo");
        boolean status = login.loginUser("kyl_1", "Ch3ss#123");
        String message = login.returnLoginStatus(status);
        assertEquals("Welcome Kyle, Ntokozo it is great to see you.", message);
    }

    @Test
    public void testReturnLoginStatusFailure() {
        // Test return message for failed login
        boolean status = login.loginUser("wrong_user", "wrong_pass");
        String message = login.returnLoginStatus(status);
        assertEquals("Username or password incorrect, please try again.", message);
    }
}