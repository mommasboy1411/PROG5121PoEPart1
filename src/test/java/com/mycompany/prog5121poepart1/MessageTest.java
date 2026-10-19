package com.mycompany.prog5121poepart1;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Tests the Part 2 QuickChat message requirements. */
public class MessageTest {

    private Message message;

    @BeforeEach
    public void setUp() {
        // Fixed ID means the expected hash can be asserted exactly.
        message = new Message("0012345678", "+27831234567", "Hi tonight");
    }

    @Test
    public void testCheckMessageLengthSuccess() {
        assertEquals("Message ready to send.", message.checkMessageLength("Hi tonight"));
        assertEquals("Message ready to send.", message.checkMessageLength("A".repeat(250)));
    }

    @Test
    public void testCheckMessageLengthTooLong() {
        assertEquals("Message exceeds 250 characters by 1; please reduce the size.",
                message.checkMessageLength("A".repeat(251)));
        assertEquals("Message exceeds 250 characters by 10; please reduce the size.",
                message.checkMessageLength("B".repeat(260)));
    }

    @Test
    public void testCheckMessageLengthEmptyOrNull() {
        assertEquals("Please enter a message of less than 250 characters.",
                message.checkMessageLength(""));
        assertEquals("Please enter a message of less than 250 characters.",
                message.checkMessageLength("  "));
        assertEquals("Please enter a message of less than 250 characters.",
                message.checkMessageLength(null));
    }

    @Test
    public void testCheckRecipientCellSuccess() {
        assertEquals("Cell phone number successfully captured.",
                message.checkRecipientCell("+27831234567"));
        assertEquals("Cell phone number successfully captured.",
                message.checkRecipientCell("0831234567"));
    }

    @Test
    public void testCheckRecipientCellFailure() {
        String error = "Cell phone number is incorrectly formatted or does not contain an international code. "
                + "Please correct the number and try again.";
        assertEquals(error, message.checkRecipientCell("083123"));
        assertEquals(error, message.checkRecipientCell("+278312345678"));
        assertEquals(error, message.checkRecipientCell("27831234567"));
        assertEquals(error, message.checkRecipientCell("083ABC4567"));
        assertEquals(error, message.checkRecipientCell(null));
    }

    @Test
    public void testGenerateMessageIDUniqueTenDigits() {
        Message first = new Message();
        Message second = new Message();
        assertTrue(first.getMessageID().matches("[0-9]{10}"));
        assertTrue(second.getMessageID().matches("[0-9]{10}"));
        assertNotEquals(first.getMessageID(), second.getMessageID());
        assertTrue(first.checkMessageID());
    }

    @Test
    public void testCheckMessageIDInvalid() {
        assertFalse(new Message(null, "0831234567", "Hello").checkMessageID());
        assertFalse(new Message("12345678901", "0831234567", "Hello").checkMessageID());
        assertTrue(message.checkMessageID());
    }

    @Test
    public void testCreateMessageHashExact() {
        assertEquals("00:0:HITONIGHT", message.createMessageHash(0));
        assertEquals("00:0:HITONIGHT", message.getMessageHash());
    }

    @Test
    public void testCreateMessageHashOtherCountAndCapitalization() {
        Message another = new Message("9876543210", "0831234567", "hello to My friends");
        assertEquals("98:2:HELLOFRIENDS", another.createMessageHash(2));
    }

    @Test
    public void testSentMessageOptionOne() {
        assertEquals("Message successfully sent.", message.SentMessage(1));
    }

    @Test
    public void testSentMessageOptionTwo() {
        assertEquals("Press 0 to delete the message.", message.SentMessage(2));
    }

    @Test
    public void testSentMessageOptionThree() {
        assertEquals("Message successfully stored.", message.SentMessage(3));
    }

    @Test
    public void testPrintMessages() {
        message.createMessageHash(0);
        String printed = message.printMessages();
        assertTrue(printed.contains("Message ID: 0012345678"));
        assertTrue(printed.contains("Message Hash: 00:0:HITONIGHT"));
        assertTrue(printed.contains("Recipient: +27831234567"));
        assertTrue(printed.contains("Message text: Hi tonight"));
    }
}
