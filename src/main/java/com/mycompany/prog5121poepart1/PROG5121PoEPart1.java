package com.mycompany.prog5121poepart1;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Scanner;

/** Main runner: Part 1 registration/login, followed by Part 2 QuickChat. */
public class PROG5121PoEPart1 {

    private static final String VALID_RECIPIENT = "Cell phone number successfully captured.";
    private static final String VALID_MESSAGE = "Message ready to send.";
    private static int messageCounter = 0;
    private static final Path STORED_MESSAGES_FILE = Path.of("quickchat-stored-messages.jsonl");

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            Login loginApp = new Login();

            System.out.println("=== User Registration ===");
            System.out.print("First name: ");
            String firstName = scanner.nextLine();
            System.out.print("Last name: ");
            String lastName = scanner.nextLine();

            String username;
            do {
                System.out.print("Create username (max 5 characters, include '_'): ");
                username = scanner.nextLine();
                if (!loginApp.checkUserName(username)) {
                    System.out.println("Username is not correctly formatted, please ensure that your username contains an underscore and is no more than 5 characters in length.");
                }
            } while (!loginApp.checkUserName(username));

            String password;
            do {
                System.out.print("Create password (8+ characters, uppercase, number, special character): ");
                password = scanner.nextLine();
                if (!loginApp.checkPasswordComplexity(password)) {
                    System.out.println("Password is not correctly formatted, please ensure that the password contains at least 8 characters, a capital letter, a number, and a special character.");
                }
            } while (!loginApp.checkPasswordComplexity(password));

            String cellNumber;
            do {
                System.out.print("Cell number (e.g. +27831234567): ");
                cellNumber = scanner.nextLine();
                if (!loginApp.checkCellPhoneNumber(cellNumber)) {
                    System.out.println("Cell phone number is incorrectly formatted or does not contain the international code (+27).");
                }
            } while (!loginApp.checkCellPhoneNumber(cellNumber));

            System.out.println(loginApp.registerUser(username, password, cellNumber, firstName, lastName));

            System.out.println("\n=== Login ===");
            boolean loggedIn = false;
            while (!loggedIn) {
                System.out.print("Username (or 0 to exit): ");
                String enteredUsername = scanner.nextLine();
                if (enteredUsername.equals("0")) {
                    return;
                }
                System.out.print("Password: ");
                String enteredPassword = scanner.nextLine();

                loggedIn = loginApp.loginUser(enteredUsername, enteredPassword);
                System.out.println(loginApp.returnLoginStatus(loggedIn));
            }

            // The application is NOT accessible before successful authentication.
            System.out.println("Welcome to QuickChat.");
            runQuickChat(scanner);
        }
    }

    private static void runQuickChat(Scanner scanner) {
        while (true) {
            System.out.println("\n1) Send Messages");
            System.out.println("2) Show recently sent messages");
            System.out.println("3) Quit");

            int choice = readNumber(scanner, "Choose an option: ", 1, 3);
            switch (choice) {
                case 1:
                    enterMessages(scanner);
                    break;
                case 2:
                    System.out.println("Coming Soon.");
                    break;
                case 3:
                    System.out.println("Goodbye.");
                    return;
                default:
                    // readNumber ensures this cannot happen.
                    break;
            }
        }
    }

    private static void enterMessages(Scanner scanner) {
        int total = readNumber(scanner, "How many messages do you wish to enter? ", 1, Integer.MAX_VALUE);

        for (int messageCount = 0; messageCount < total; messageCount++) {
            System.out.println("\nMessage " + (messageCount + 1) + " of " + total);

            Message message = new Message();
            String recipient;
            do {
                System.out.print("Recipient cell number: ");
                recipient = scanner.nextLine();
                String result = message.checkRecipientCell(recipient);
                System.out.println(result);
            } while (!VALID_RECIPIENT.equals(message.checkRecipientCell(recipient)));

            String text;
            do {
                System.out.print("Message text: ");
                text = scanner.nextLine();
                String result = message.checkMessageLength(text);
                System.out.println(result);
            } while (!VALID_MESSAGE.equals(message.checkMessageLength(text)));

            // Use one message object, with a zero-based running count.
            message.setRecipientCell(recipient);
            message.setMessageText(text);
            message.createMessageHash(messageCounter++);

            System.out.println("1) Send Message");
            System.out.println("2) Disregard Message");
            System.out.println("3) Store Message to send later");
            int action = readNumber(scanner, "Choose message action: ", 1, 3);

            if (action == 2) {
                System.out.println(message.SentMessage(2));
                while (!scanner.nextLine().trim().equals("0")) {
                    System.out.println("Press 0 to delete the message.");
                }
                continue; // Disregarded messages are never sent or stored.
            }

            if (action == 3) {
                try {
                    saveStoredMessage(message);
                    System.out.println(message.SentMessage(3));
                    System.out.println(message.printMessages());
                } catch (IOException ex) {
                    System.out.println("Unable to store the message: " + ex.getMessage());
                }
            } else {
                System.out.println(message.SentMessage(1));
                System.out.println(message.printMessages());
            }
        }
    }

    private static int readNumber(Scanner scanner, String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) {
                    return value;
                }
            } catch (NumberFormatException ignored) {
                // Ask again for a valid menu number.
            }
            System.out.println("Please enter a whole number between " + min + " and " + max + ".");
        }
    }

    /** Stores each deferred message as a JSON object on its own line. */
    private static void saveStoredMessage(Message message) throws IOException {
        String json = "{\"messageID\":\"" + escapeJson(message.getMessageID())
                + "\",\"messageHash\":\"" + escapeJson(message.getMessageHash())
                + "\",\"recipient\":\"" + escapeJson(message.getRecipientCell())
                + "\",\"messageText\":\"" + escapeJson(message.getMessageText()) + "\"}"
                + System.lineSeparator();
        Files.writeString(STORED_MESSAGES_FILE, json, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

    private static String escapeJson(String text) {
        StringBuilder result = new StringBuilder();
        for (char ch : text.toCharArray()) {
            switch (ch) {
                case '\\': result.append("\\\\"); break;
                case '"': result.append("\\\""); break;
                case '\n': result.append("\\n"); break;
                case '\r': result.append("\\r"); break;
                case '\t': result.append("\\t"); break;
                default:
                    if (ch < 0x20) {
                        result.append(String.format("\\u%04x", (int) ch));
                    } else {
                        result.append(ch);
                    }
            }
        }
        return result.toString();
    }
}
