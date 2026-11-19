package com.mycompany.prog5121poepart1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Main QuickChat application integrating Part 1 (Registration & Login),
 * Part 2 (Message Sending, Validation, Hash, JSON Array Storage), and
 * Part 3 (Parallel Arrays, Search, Delete by Hash, and Task Reports).
 *
 * @author Ntokozo Ntshalintshali (INTSAKALO)
 */
public class PROG5121PoEPart1 {

    public static final String JSON_FILE_PATH = "stored_messages.json";

    // Part 3 Parallel Arrays (dynamically populated, zero hard-coding)
    private static String[] sentMessages = new String[0];
    private static String[] disregardedMessages = new String[0];
    private static String[] storedMessages = new String[0];
    private static String[] messageHashes = new String[0];
    private static String[] messageIDs = new String[0];

    // Parallel metadata arrays to support sender, recipient, and flag lookups
    private static String[] messageSenders = new String[0];
    private static String[] messageRecipients = new String[0];
    private static String[] messageFlags = new String[0];

    private static final List<Message> sessionMessages = new ArrayList<>();
    private static String currentLoggedInSender = "Developer";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Login loginSystem = new Login();

        System.out.println("=== QuickChat // INTSAKALO Edition — Registration ===");
        boolean registered = false;
        while (!registered) {
            System.out.print("Enter First Name: ");
            String firstName = scanner.nextLine();
            System.out.print("Enter Last Name: ");
            String lastName = scanner.nextLine();
            System.out.print("Enter Username (contains '_' and max 5 chars): ");
            String username = scanner.nextLine();
            System.out.print("Enter Password (8+ chars, capital, number, special char): ");
            String password = scanner.nextLine();
            System.out.print("Enter Cell Phone Number (+27xxxxxxxxx): ");
            String cellNumber = scanner.nextLine();

            String regResult = loginSystem.registerUser(username, password, cellNumber, firstName, lastName);
            System.out.println(regResult);
            if (regResult.contains("successfully registered")) {
                registered = true;
                currentLoggedInSender = firstName + " " + lastName;
            }
        }

        System.out.println("\n=== QuickChat Login ===");
        boolean loggedIn = false;
        while (!loggedIn) {
            System.out.print("Enter Username: ");
            String loginUser = scanner.nextLine();
            System.out.print("Enter Password: ");
            String loginPass = scanner.nextLine();

            loggedIn = loginSystem.loginUser(loginUser, loginPass);
            System.out.println(loginSystem.returnLoginStatus(loggedIn));
        }

        // Load previously stored JSON messages into the arrays on startup
        loadStoredJsonIntoSession();
        rebuildParallelArrays();

        System.out.println("\nWelcome to QuickChat.");
        runMainMenu(scanner);
        scanner.close();
    }

    public static void runMainMenu(Scanner scanner) {
        boolean running = true;
        while (running) {
            System.out.println("\nPlease select an option:");
            System.out.println("1) Send Messages");
            System.out.println("2) Show recently sent messages");
            System.out.println("3) Quit");
            System.out.println("4) Stored Messages & Reports");
            System.out.print("Choice: ");

            int choice = readInt(scanner);
            switch (choice) {
                case 1:
                    handleSendMessages(scanner);
                    break;
                case 2:
                    System.out.println(displaySentMessagesReport());
                    break;
                case 3:
                    System.out.println("Total number of messages sent: " + Message.returnTotalMessages());
                    System.out.println("Exiting QuickChat. Goodbye!");
                    running = false;
                    break;
                case 4:
                    handleStoredMessagesSubMenu(scanner);
                    break;
                default:
                    System.out.println("Invalid option. Please choose 1, 2, 3, or 4.");
            }
        }
    }

    private static void handleSendMessages(Scanner scanner) {
        System.out.print("How many messages do you wish to enter? ");
        int count = readInt(scanner);

        for (int i = 0; i < count; i++) {
            System.out.println("\n--- Message " + (i + 1) + " of " + count + " ---");

            String recipient;
            Message tempValidator;
            while (true) {
                System.out.print("Enter Recipient Cell Number (+27xxxxxxxxx): ");
                recipient = scanner.nextLine();
                tempValidator = new Message(i + 1, recipient, "", "Pending");
                if (tempValidator.checkRecipientCell() == 1) {
                    System.out.println("Cell phone number successfully captured.");
                    break;
                } else {
                    System.out.println("Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.");
                }
            }

            String text;
            while (true) {
                System.out.print("Enter Message (max 250 characters): ");
                text = scanner.nextLine();
                String lenStatus = tempValidator.checkMessageLength(text);
                System.out.println(lenStatus);
                if (lenStatus.equals("Message ready to send.")) {
                    break;
                }
            }

            Message msg = new Message(sessionMessages.size() + 1, recipient, text, "Pending");

            System.out.println("Choose action: 1) Send Message  2) Disregard Message  3) Store Message");
            int action = readInt(scanner);
            String actionResponse = msg.sentMessage(action);
            System.out.println(actionResponse);

            if (action == 1 || action == 2 || action == 3) {
                sessionMessages.add(msg);
                if (action == 3) {
                    saveAllStoredMessagesToJson();
                }
                rebuildParallelArrays();
            }

            if (action == 1) {
                System.out.println(msg.printMessages());
            }
        }

        System.out.println("\nTotal number of messages sent: " + Message.returnTotalMessages());
    }

    private static void handleStoredMessagesSubMenu(Scanner scanner) {
        System.out.println("\n--- Stored Messages & Reports Menu ---");
        System.out.println("a) Display sender and recipient of all sent/stored messages");
        System.out.println("b) Display the longest message");
        System.out.println("c) Search for a message ID and display corresponding recipient and message");
        System.out.println("d) Search for all messages sent or stored for a particular recipient");
        System.out.println("e) Delete a message using the message hash");
        System.out.println("f) Display a report that lists the full details of all sent messages");
        System.out.print("Select sub-option (a-f): ");

        String sub = scanner.nextLine().trim().toLowerCase();
        switch (sub) {
            case "a":
                System.out.println(displaySendersAndRecipients());
                break;
            case "b":
                System.out.println(displayLongestMessage());
                break;
            case "c":
                System.out.print("Enter Message ID to search: ");
                String id = scanner.nextLine().trim();
                System.out.println(searchByMessageID(id));
                break;
            case "d":
                System.out.print("Enter Recipient number to search: ");
                String rec = scanner.nextLine().trim();
                System.out.println(searchByRecipient(rec));
                break;
            case "e":
                System.out.print("Enter Message Hash to delete: ");
                String hash = scanner.nextLine().trim();
                System.out.println(deleteMessageByHash(hash));
                break;
            case "f":
                System.out.println(displaySentMessagesReport());
                break;
            default:
                System.out.println("Invalid sub-option.");
        }
    }

    /**
     * Dynamically populates all Part 3 parallel arrays from active and JSON-loaded messages.
     */
    public static void rebuildParallelArrays() {
        List<String> sentList = new ArrayList<>();
        List<String> disregardList = new ArrayList<>();
        List<String> storedList = new ArrayList<>();
        List<String> hashList = new ArrayList<>();
        List<String> idList = new ArrayList<>();
        List<String> senderList = new ArrayList<>();
        List<String> recipientList = new ArrayList<>();
        List<String> flagList = new ArrayList<>();

        for (Message m : sessionMessages) {
            String f = m.getFlag() == null ? "" : m.getFlag();
            if (f.equalsIgnoreCase("Sent")) {
                sentList.add(m.getMessageText());
            } else if (f.equalsIgnoreCase("Disregard")) {
                disregardList.add(m.getMessageText());
            } else if (f.equalsIgnoreCase("Stored")) {
                storedList.add(m.getMessageText());
            }

            hashList.add(m.getMessageHash());
            idList.add(m.getMessageID());
            senderList.add(currentLoggedInSender);
            recipientList.add(m.getRecipient());
            flagList.add(f);
        }

        sentMessages = sentList.toArray(new String[0]);
        disregardedMessages = disregardList.toArray(new String[0]);
        storedMessages = storedList.toArray(new String[0]);
        messageHashes = hashList.toArray(new String[0]);
        messageIDs = idList.toArray(new String[0]);
        messageSenders = senderList.toArray(new String[0]);
        messageRecipients = recipientList.toArray(new String[0]);
        messageFlags = flagList.toArray(new String[0]);
    }

    public static void loadStoredJsonIntoSession() {
        try {
            List<Message> fromFile = Message.loadMessagesFromJsonArray(JSON_FILE_PATH);
            sessionMessages.addAll(fromFile);
        } catch (IOException e) {
            System.out.println("Note: No existing JSON message archive found.");
        }
    }

    public static void saveAllStoredMessagesToJson() {
        List<Message> toSave = new ArrayList<>();
        for (Message m : sessionMessages) {
            if ("Stored".equalsIgnoreCase(m.getFlag())) {
                toSave.add(m);
            }
        }
        try {
            Message.saveMessagesToJsonArray(toSave, JSON_FILE_PATH);
        } catch (IOException e) {
            System.out.println("Error saving JSON file: " + e.getMessage());
        }
    }

    // 2a: Display sender and recipient of all messages
    public static String displaySendersAndRecipients() {
        if (messageRecipients.length == 0) {
            return "No messages available.";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < messageRecipients.length; i++) {
            sb.append("Sender: ").append(messageSenders[i])
              .append(" | Recipient: ").append(messageRecipients[i])
              .append(" (").append(messageFlags[i]).append(")\n");
        }
        return sb.toString().trim();
    }

    // 2b: Display the longest message across Sent and Stored messages
    public static String displayLongestMessage() {
        String longest = "";
        for (Message m : sessionMessages) {
            if (("Sent".equalsIgnoreCase(m.getFlag()) || "Stored".equalsIgnoreCase(m.getFlag()))
                    && m.getMessageText() != null
                    && m.getMessageText().length() > longest.length()) {
                longest = m.getMessageText();
            }
        }
        return longest;
    }

    // 2c: Search for a Message ID and return corresponding recipient and message
    public static String searchByMessageID(String searchID) {
        for (int i = 0; i < messageIDs.length; i++) {
            if (messageIDs[i] != null && messageIDs[i].equals(searchID)) {
                Message m = sessionMessages.get(i);
                return "Recipient: " + m.getRecipient() + "\nMessage: " + m.getMessageText();
            }
        }
        return "Message ID not found.";
    }

    // 2d: Search all messages sent or stored regarding a particular recipient
    public static List<String> searchByRecipientList(String recipientNumber) {
        List<String> matches = new ArrayList<>();
        for (int i = 0; i < messageRecipients.length; i++) {
            if (messageRecipients[i] != null && messageRecipients[i].equals(recipientNumber)
                    && ("Sent".equalsIgnoreCase(messageFlags[i]) || "Stored".equalsIgnoreCase(messageFlags[i]))) {
                matches.add(sessionMessages.get(i).getMessageText());
            }
        }
        return matches;
    }

    public static String searchByRecipient(String recipientNumber) {
        List<String> matches = searchByRecipientList(recipientNumber);
        if (matches.isEmpty()) {
            return "No messages found for recipient: " + recipientNumber;
        }
        return String.join("\n", matches);
    }

    // 2e: Delete a message using the Message Hash
    public static String deleteMessageByHash(String targetHash) {
        for (int i = 0; i < messageHashes.length; i++) {
            if (messageHashes[i] != null && messageHashes[i].equalsIgnoreCase(targetHash)) {
                String deletedText = sessionMessages.get(i).getMessageText();
                sessionMessages.remove(i);
                saveAllStoredMessagesToJson();
                rebuildParallelArrays();
                return "Message \"" + deletedText + "\" successfully deleted.";
            }
        }
        return "Message hash not found.";
    }

    // 2f: Display report showing Message Hash, Recipient, and Message for all Sent messages
    public static String displaySentMessagesReport() {
        StringBuilder report = new StringBuilder();
        boolean found = false;
        for (Message m : sessionMessages) {
            if ("Sent".equalsIgnoreCase(m.getFlag())) {
                found = true;
                report.append("Message Hash: ").append(m.getMessageHash()).append("\n")
                      .append("Recipient: ").append(m.getRecipient()).append("\n")
                      .append("Message: ").append(m.getMessageText()).append("\n\n");
            }
        }
        if (!found) {
            return "No sent messages to display.";
        }
        return report.toString().trim();
    }

    // Helpers for JUnit 5 testing
    public static void clearSessionData() {
        sessionMessages.clear();
        Message.resetTotalMessages();
        rebuildParallelArrays();
    }

    public static void addMessageToSession(Message m) {
        sessionMessages.add(m);
        rebuildParallelArrays();
    }

    public static String[] getSentMessagesArray() { return sentMessages; }
    public static String[] getDisregardedMessagesArray() { return disregardedMessages; }
    public static String[] getStoredMessagesArray() { return storedMessages; }
    public static String[] getMessageHashesArray() { return messageHashes; }
    public static String[] getMessageIDsArray() { return messageIDs; }

    private static int readInt(Scanner scanner) {
        while (true) {
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid number: ");
            }
        }
    }
}