package com.mycompany.prog5121poepart1;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Represents one QuickChat message and its identifying information.
 */
public class Message {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final Set<String> GENERATED_IDS = new HashSet<>();

    private String messageID;
    private String messageHash;
    private String recipientCell;
    private String messageText;

    public Message() {
        this(null, null);
    }

    /** Creates a new message with an automatically generated ID. */
    public Message(String recipientCell, String messageText) {
        this.recipientCell = recipientCell;
        this.messageText = messageText;
        generateMessageID();
    }

    /**
     * Lets tests or imported data supply a known ID, so the hash is predictable.
     */
    public Message(String messageID, String recipientCell, String messageText) {
        this.messageID = messageID;
        this.recipientCell = recipientCell;
        this.messageText = messageText;
    }

    public String checkMessageLength(String messageText) {
        if (messageText == null || messageText.trim().isEmpty()) {
            return "Please enter a message of less than 250 characters.";
        }
        if (messageText.length() <= 250) {
            return "Message ready to send.";
        }
        return "Message exceeds 250 characters by " + (messageText.length() - 250)
                + "; please reduce the size.";
    }

    public String checkRecipientCell(String recipientCell) {
        // South African local format: 0 followed by nine digits (10 total).
        // International format: +27 followed by the same nine digits.
        // The +27 country code makes an international number 11 digits long.
        boolean valid = recipientCell != null &&
                (recipientCell.matches("^0[0-9]{9}$")
                || recipientCell.matches("^\\+27[0-9]{9}$"));

        if (valid) {
            return "Cell phone number successfully captured.";
        }
        return "Cell phone number is incorrectly formatted or does not contain an international code. "
                + "Please correct the number and try again.";
    }

    /** Creates a unique, zero-padded 10-digit ID during this program run. */
    public final String generateMessageID() {
        synchronized (GENERATED_IDS) {
            String candidate;
            do {
                candidate = String.format(Locale.ROOT, "%010d", RANDOM.nextLong(10_000_000_000L));
            } while (!GENERATED_IDS.add(candidate));
            this.messageID = candidate;
            this.messageHash = null;
            return candidate;
        }
    }

    public boolean checkMessageID() {
        return messageID != null && !messageID.isEmpty() && messageID.length() <= 10;
    }

    /**
     * Example: ID 0012345678, count 0, "Hi tonight" -> 00:0:HITONIGHT
     */
    public String createMessageHash(int messageCount) {
        if (messageID == null || messageID.length() < 2
                || messageText == null || messageText.trim().isEmpty()) {
            throw new IllegalStateException("A valid message ID and text are required to create a hash.");
        }
        String[] words = messageText.trim().split("\\s+");
        messageHash = messageID.substring(0, 2) + ":" + messageCount + ":"
                + (words[0] + words[words.length - 1]).toUpperCase(Locale.ROOT);
        return messageHash;
    }

    /** Returns the exact status for the chosen send action. */
    public String SentMessage(int option) {
        switch (option) {
            case 1:
                return "Message successfully sent.";
            case 2:
                return "Press 0 to delete the message.";
            case 3:
                return "Message successfully stored.";
            default:
                return "Invalid option.";
        }
    }

    public String printMessages() {
        return "Message ID: " + messageID + System.lineSeparator()
                + "Message Hash: " + messageHash + System.lineSeparator()
                + "Recipient: " + recipientCell + System.lineSeparator()
                + "Message text: " + messageText;
    }

    public void setRecipientCell(String recipientCell) {
        this.recipientCell = recipientCell;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
        this.messageHash = null;
    }

    public String getMessageID() {
        return messageID;
    }

    public String getMessageHash() {
        return messageHash;
    }

    public String getRecipientCell() {
        return recipientCell;
    }

    public String getMessageText() {
        return messageText;
    }
}
