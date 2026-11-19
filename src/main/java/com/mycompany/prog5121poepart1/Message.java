package com.mycompany.prog5121poepart1;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Worker class representing a QuickChat message, validation rules,
 * hash generation, and JSON array persistence.
 *
 * Reference for JSON file handling and string escaping:
 * Oracle Corporation. (2024). Java Platform, Standard Edition Documentation:
 * java.nio.file.Files and java.lang.String. https://docs.oracle.com/en/java/javase/17/docs/api/
 */
public class Message {

    private String messageID;
    private int numMessagesSent;
    private String recipient;
    private String messageText;
    private String messageHash;
    private String flag; // "Sent", "Stored", or "Disregard"

    private static int totalMessagesSent = 0;

    public Message() {
    }

    public Message(int messageNumber, String recipient, String messageText, String flag) {
        this.numMessagesSent = messageNumber;
        this.messageID = generateMessageID();
        this.recipient = recipient;
        this.messageText = messageText;
        this.flag = flag;
        this.messageHash = createMessageHash();
    }

    public Message(String messageID, int messageNumber, String recipient, String messageText, String messageHash, String flag) {
        this.messageID = messageID;
        this.numMessagesSent = messageNumber;
        this.recipient = recipient;
        this.messageText = messageText;
        this.messageHash = messageHash;
        this.flag = flag;
    }

    /**
     * Generates a 10-digit random Message ID using String manipulation (substring).
     */
    public final String generateMessageID() {
        Random random = new Random();
        long raw = 1000000000L + (long) (random.nextDouble() * 9000000000L);
        String idString = String.valueOf(raw);
        return idString.length() > 10 ? idString.substring(0, 10) : idString;
    }

    /**
     * Ensures the Message ID is not null and is at most 10 characters long.
     */
    public boolean checkMessageID() {
        return messageID != null && messageID.length() <= 10;
    }

    /**
     * Validates that the recipient cell phone number includes the international code (+27)
     * followed by 9 digits (12 characters total). Rejects local numbers starting with 0.
     *
     * Reference:
     * Oracle Corporation. (2024). Pattern (Java SE 17 & JDK 17).
     * https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/regex/Pattern.html
     */
    public int checkRecipientCell() {
        if (recipient != null && recipient.matches("^\\+27\\d{9}$")) {
            return 1;
        }
        return 0;
    }

    /**
     * Validates message character length (maximum 250 characters).
     */
    public String checkMessageLength(String text) {
        if (text == null) {
            return "Message exceeds 250 characters by 0, please reduce size.";
        }
        if (text.length() <= 250) {
            return "Message ready to send.";
        } else {
            int excess = text.length() - 250;
            return "Message exceeds 250 characters by " + excess + ", please reduce size.";
        }
    }

    /**
     * Creates the Message Hash in format:
     * [First 2 digits of MessageID]:[MessageNumber]:[FIRSTWORDLASTWORD]
     * Punctuation is stripped from words and converted to uppercase.
     */
    public final String createMessageHash() {
        String idPrefix = (messageID != null && messageID.length() >= 2)
                ? messageID.substring(0, 2)
                : "00";

        if (messageText == null || messageText.trim().isEmpty()) {
            return idPrefix + ":" + numMessagesSent + ":";
        }

        // Remove punctuation so words like "tonight?" become "tonight"
        String cleanedText = messageText.replaceAll("[^a-zA-Z0-9\\s]", "").trim();
        if (cleanedText.isEmpty()) {
            return idPrefix + ":" + numMessagesSent + ":";
        }

        String[] words = cleanedText.split("\\s+");
        String firstWord = words[0].toUpperCase();
        String lastWord = words[words.length - 1].toUpperCase();

        return idPrefix + ":" + numMessagesSent + ":" + firstWord + lastWord;
    }

    /**
     * Handles user action selection:
     * 1 = Send Message, 2 = Disregard Message, 3 = Store Message
     */
    public String sentMessage(int choice) {
        switch (choice) {
            case 1:
                this.flag = "Sent";
                totalMessagesSent++;
                return "Message successfully sent.";
            case 2:
                this.flag = "Disregard";
                return "Press 0 to delete message.";
            case 3:
                this.flag = "Stored";
                return "Message successfully stored.";
            default:
                return "Invalid option selected.";
        }
    }

    public String printMessages() {
        return "Message ID: " + messageID
                + "\nMessage Hash: " + messageHash
                + "\nRecipient: " + recipient
                + "\nMessage: " + messageText;
    }

    public static int returnTotalMessages() {
        return totalMessagesSent;
    }

    public static void resetTotalMessages() {
        totalMessagesSent = 0;
    }

    /**
     * Saves a list of stored Message objects into a valid JSON array file ([...]).
     *
     * Reference:
     * Baeldung. (2024). Reading and Writing Files in Java. https://www.baeldung.com/java-write-to-file
     */
    public static void saveMessagesToJsonArray(List<Message> messages, String filePath) throws IOException {
        Path path = Paths.get(filePath);
        StringBuilder json = new StringBuilder();
        json.append("[\n");

        for (int i = 0; i < messages.size(); i++) {
            Message m = messages.get(i);
            json.append("  {\n");
            json.append("    \"messageID\": \"").append(escapeJson(m.getMessageID())).append("\",\n");
            json.append("    \"messageNumber\": ").append(m.getNumMessagesSent()).append(",\n");
            json.append("    \"recipient\": \"").append(escapeJson(m.getRecipient())).append("\",\n");
            json.append("    \"messageText\": \"").append(escapeJson(m.getMessageText())).append("\",\n");
            json.append("    \"messageHash\": \"").append(escapeJson(m.getMessageHash())).append("\",\n");
            json.append("    \"flag\": \"").append(escapeJson(m.getFlag())).append("\"\n");
            json.append("  }");
            if (i < messages.size() - 1) {
                json.append(",");
            }
            json.append("\n");
        }

        json.append("]");
        Files.writeString(path, json.toString(), StandardCharsets.UTF_8);
    }

    /**
     * Reads a JSON array file ([...]) back into a List of Message objects.
     *
     * Reference:
     * Baeldung. (2024). Reading and Writing Files in Java. https://www.baeldung.com/reading-file-in-java
     */
    public static List<Message> loadMessagesFromJsonArray(String filePath) throws IOException {
        List<Message> loaded = new ArrayList<>();
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            return loaded;
        }

        String content = Files.readString(path, StandardCharsets.UTF_8).trim();
        if (content.isEmpty() || content.equals("[]")) {
            return loaded;
        }

        if (content.startsWith("[")) {
            content = content.substring(1);
        }
        if (content.endsWith("]")) {
            content = content.substring(0, content.length() - 1);
        }

        String[] objects = content.split("\\},\\s*\\{");
        for (String obj : objects) {
            String cleanObj = obj.replace("{", "").replace("}", "").trim();
            if (cleanObj.isEmpty()) {
                continue;
            }
            String id = extractJsonValue(cleanObj, "messageID");
            String numStr = extractJsonNumeric(cleanObj, "messageNumber");
            String rec = extractJsonValue(cleanObj, "recipient");
            String text = extractJsonValue(cleanObj, "messageText");
            String hash = extractJsonValue(cleanObj, "messageHash");
            String flg = extractJsonValue(cleanObj, "flag");

            int msgNum = numStr.isEmpty() ? 0 : Integer.parseInt(numStr);
            loaded.add(new Message(id, msgNum, rec, text, hash, flg));
        }

        return loaded;
    }

    private static String extractJsonValue(String block, String key) {
        String search = "\"" + key + "\": \"";
        int start = block.indexOf(search);
        if (start == -1) {
            return "";
        }
        start += search.length();
        int end = block.indexOf("\"", start);
        while (end > 0 && block.charAt(end - 1) == '\\') {
            end = block.indexOf("\"", end + 1);
        }
        if (end == -1) {
            return "";
        }
        return unescapeJson(block.substring(start, end));
    }

    private static String extractJsonNumeric(String block, String key) {
        String search = "\"" + key + "\": ";
        int start = block.indexOf(search);
        if (start == -1) {
            return "0";
        }
        start += search.length();
        int end = block.indexOf(",", start);
        if (end == -1) {
            end = block.length();
        }
        return block.substring(start, end).trim();
    }

    private static String escapeJson(String input) {
        if (input == null) {
            return "";
        }
        return input.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private static String unescapeJson(String input) {
        return input.replace("\\\"", "\"")
                .replace("\\n", "\n")
                .replace("\\r", "\r")
                .replace("\\t", "\t")
                .replace("\\\\", "\\");
    }

    public String getMessageID() { return messageID; }
    public void setMessageID(String messageID) {
        this.messageID = messageID;
        this.messageHash = createMessageHash();
    }
    public int getNumMessagesSent() { return numMessagesSent; }
    public String getRecipient() { return recipient; }
    public String getMessageText() { return messageText; }
    public String getMessageHash() { return messageHash; }
    public String getFlag() { return flag; }
}