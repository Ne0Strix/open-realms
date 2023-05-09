/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.network.server;

import static org.junit.Assert.*;

import at.vunfer.openrealms.network.Message;
import at.vunfer.openrealms.network.MessageType;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

public class MessageHandlerTest {

    @Test
    public void handleMessage() {
        MessageHandler handler = new MessageHandler();

        // Test TOUCHED message
        Message touchedMessage = new Message(MessageType.TOUCHED);
        assertTrue(handler.handleMessage(touchedMessage));

        // Test CHOICE message
        Map<String, Object> choiceData = new HashMap<>();
        choiceData.put("option", "Option 1");
        Message choiceMessage = new Message(MessageType.CHOICE);
        assertTrue(handler.handleMessage(choiceMessage));

        // Test END_TURN message
        Message endTurnMessage = new Message(MessageType.END_TURN);
        assertTrue(handler.handleMessage(endTurnMessage));

        // Test unknown message type
        Message unknownMessage = new Message(MessageType.ADD_CARD);
        assertFalse(handler.handleMessage(unknownMessage));
    }
}
