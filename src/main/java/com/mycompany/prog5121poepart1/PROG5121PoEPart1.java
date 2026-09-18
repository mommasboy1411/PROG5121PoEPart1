package com.mycompany.prog5121poepart1;

import java.util.Scanner;

/**
 * INTSAKALO CONNECT - The Digital Network for SA Youth & Creatives.
 * Host: 2026 Jozi Baddie Receptionist screening artist profiles at the door.
 */
public class PROG5121PoEPart1 {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Login loginApp = new Login();

        System.out.println("===================================================================");
        System.out.println("     ⚡ INTSAKALO CONNECT // THE SA CREATIVE NETWORK ⚡           ");
        System.out.println("   Producers • Vocalists • Graphic Designers • Streetwear Brands   ");
        System.out.println("===================================================================");
        System.out.println("Yooo, welcome bestie! I'm your host for tonight.");
        System.out.println("Before I let you into the hub, let's get your creator profile locked in.\n");

        System.out.print(">>> Soo, what's your First Name?: ");
        String firstName = scanner.nextLine();

        System.out.print(">>> And your Last Name?: ");
        String lastName = scanner.nextLine();

        System.out.print(">>> What's your Stage / Brand Alias (e.g. NtokeySA, W4DE)?: ");
        String alias = scanner.nextLine();

        String username;
        while (true) {
            System.out.print("\n>>> Create your Handle ID (max 5 chars, must have '_'): ");
            username = scanner.nextLine();
            if (loginApp.checkUserName(username)) {
                System.out.println("Abo'mbali! Handle captured, super clean!");
                break;
            } else {
                System.out.println("Eish babe, look: Username is not correctly formatted, please ensure that your username contains an underscore and is no more than 5 characters in length.");
            }
        }

        String password;
        while (true) {
            System.out.print("\n>>> Set up your Vault Key (8+ chars, uppercase, digit & special char): ");
            password = scanner.nextLine();
            if (loginApp.checkPasswordComplexity(password)) {
                System.out.println("Lock it in! That password is heavier than a 3 AM log drum drop!");
                break;
            } else {
                System.out.println("Ayaye! Password is not correctly formatted, please ensure that the password contains at least 8 characters, a capital letter, a number, and a special character.");
            }
        }

        String cellNumber;
        while (true) {
            System.out.print("\n>>> Drop your WhatsApp / Collab Digits (+27 format): ");
            cellNumber = scanner.nextLine();
            if (loginApp.checkCellPhoneNumber(cellNumber)) {
                System.out.println("Digits captured successfully.");
                break;
            } else {
                System.out.println("Hectic! Cell phone number is incorrectly formatted or does not contain the international code (+27).");
            }
        }

        // Register and display output
        String regStatus = loginApp.registerUser(username, password, cellNumber, firstName, lastName);
        System.out.println("\n-------------------------------------------------------------------");
        System.out.println("STATUS: " + regStatus);
        System.out.println("ARTIST PROFILE: " + alias + " (" + firstName + " " + lastName + ")");
        System.out.println("-------------------------------------------------------------------");

        System.out.println("\n===================================================================");
        System.out.println("              INTSAKALO CONNECT // VIP DOOR CHECK                 ");
        System.out.println("   Alright star, prove it's you so I can open the velvet rope...  ");
        System.out.println("===================================================================");

        System.out.print("\n>>> Enter your Handle ID to log in: ");
        String loginUsername = scanner.nextLine();

        System.out.print(">>> Enter your Vault Key: ");
        String loginPassword = scanner.nextLine();

        boolean isLoggedIn = loginApp.loginUser(loginUsername, loginPassword);
        System.out.println("\n>>> " + loginApp.returnLoginStatus(isLoggedIn));

        if (isLoggedIn) {
            System.out.println("\n✨ You're officially live on INTSAKALO Connect!");
            System.out.println("• Collab Feed: Active");
            System.out.println("• Studio Access: Granted");
            System.out.println("• Sample & Graphics Vault: Unlocked");
            System.out.println("Dont be shy");
        }

        scanner.close();
    }
}