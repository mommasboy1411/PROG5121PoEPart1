package com.mycompany.prog5121poepart1;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class MessageTest {

    @BeforeEach
    public void setUp() {
        PROG5121PoEPart1.clearSessionData();

        // Populate exact Part 3 Test Data Messages 1 - 5
        Message msg1 = new Message(1, "+27834557896", "Did you get the cake?", "Sent");
        msg1.setMessageID("1000000001");

        Message msg2 = new Message(2, "+27838884567", "Where are you? You are late! I have asked you to be on time.", "Stored");
        msg2.setMessageID("1000000002");

        Message msg3 = new Message(3, "+27834484567", "Yohoooo, I am at your gate.", "Disregard");
        msg3.setMessageID("1000000003");

        Message msg4 = new Message(4, "0838884567", "It is dinner time !", "Sent");
        msg4.setMessageID("0838884567");

        Message msg5 = new Message(5, "+27838884567", "Ok, I am leaving without you.", "Stored");
        msg5.setMessageID("1000000005");

        PROG5121PoEPart1.addMessageToSession(msg1);
        PROG5121PoEPart1.addMessageToSession(msg2);
        PROG5121PoEPart1.addMessageToSession(msg3);
        PROG5121PoEPart1.addMessageToSession(msg4);
        PROG5121PoEPart1.addMessageToSession(msg5);
    }

    // --- PART 2 UNIT TESTS ---

    @Test
    public void testMessageLengthSuccessAndFailure() {
        Message validator = new Message();
        assertEquals("Message ready to send.", validator.checkMessageLength("Hi Mike, can you join us for dinner tonight"));

        String longMessage = "A".repeat(255);
        assertEquals("Message exceeds 250 characters by 5, please reduce size.", validator.checkMessageLength(longMessage));
    }

    @Test
    public void testRecipientNumberValidation() {
        Message validMsg = new Message(1, "+27718693002", "Hello", "Sent");
        assertEquals(1, validMsg.checkRecipientCell());

        // Local number starting with 0 without international code must fail
        Message localMsg = new Message(2, "0857597588", "Hello", "Sent");
        assertEquals(0, localMsg.checkRecipientCell());
    }

    @Test
    public void testMessageHashPunctuationStrippingAndLoop() {
        Message m1 = new Message(0, "+27718693002", "Hi Mike, can you join us for dinner tonight?", "Sent");
        m1.setMessageID("0012345678");
        assertEquals("00:0:HITONIGHT", m1.getMessageHash());

        // Hash loop verification across multiple messages
        String[] sampleTexts = {"First test!", "Second message?", "Third check."};
        String[] expectedWords = {"FIRSTTEST", "SECONDMESSAGE", "THIRDCHECK"};
        for (int i = 0; i < sampleTexts.length; i++) {
            Message loopMsg = new Message(i, "+27718693002", sampleTexts[i], "Sent");
            loopMsg.setMessageID("1199999999");
            assertEquals("11:" + i + ":" + expectedWords[i], loopMsg.getMessageHash());
        }
    }

    @Test
    public void testJsonArrayPersistence() throws IOException {
        String tempFile = "test_stored_messages.json";
        Message stored = new Message(1, "+27838884567", "Stored test message", "Stored");
        stored.setMessageID("5512345678");

        Message.saveMessagesToJsonArray(List.of(stored), tempFile);
        List<Message> loaded = Message.loadMessagesFromJsonArray(tempFile);

        assertEquals(1, loaded.size());
        assertEquals("Stored test message", loaded.get(0).getMessageText());
        assertEquals("55:1:STOREDMESSAGE", loaded.get(0).getMessageHash());

        Files.deleteIfExists(Paths.get(tempFile));
    }

    // --- PART 3 UNIT TESTS (EXACT RUBRIC ASSERTIONS) ---

    @Test
    public void testSentMessagesArrayCorrectlyPopulated() {
        String[] expectedSent = {"Did you get the cake?", "It is dinner time !"};
        assertArrayEquals(expectedSent, PROG5121PoEPart1.getSentMessagesArray());
    }

    @Test
    public void testDisplayLongestMessage() {
        String expectedLongest = "Where are you? You are late! I have asked you to be on time.";
        assertEquals(expectedLongest, PROG5121PoEPart1.displayLongestMessage());
    }

    @Test
    public void testSearchForMessageID() {
        String result = PROG5121PoEPart1.searchByMessageID("0838884567");
        assertTrue(result.contains("It is dinner time !"));
    }

    @Test
    public void testSearchAllMessagesForParticularRecipient() {
        List<String> results = PROG5121PoEPart1.searchByRecipientList("+27838884567");
        assertEquals(2, results.size());
        assertEquals("Where are you? You are late! I have asked you to be on time.", results.get(0));
        assertEquals("Ok, I am leaving without you.", results.get(1));
    }

    @Test
    public void testDeleteMessageUsingMessageHash() {
        // Message 2 has ID 1000000002, number 2, text "Where are you? You are late! I have asked you to be on time."
        String targetHash = "10:2:WHERETIME";
        String response = PROG5121PoEPart1.deleteMessageByHash(targetHash);
        assertEquals("Message \"Where are you? You are late! I have asked you to be on time.\" successfully deleted.", response);
    }

    @Test
    public void testDisplaySentMessagesReport() {
        String report = PROG5121PoEPart1.displaySentMessagesReport();
        assertTrue(report.contains("Message Hash:"));
        assertTrue(report.contains("Recipient: +27834557896"));
        assertTrue(report.contains("Message: Did you get the cake?"));
        assertTrue(report.contains("Message: It is dinner time !"));
    }
}