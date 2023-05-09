/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.network.client;

import static org.junit.Assert.*;

import at.vunfer.openrealms.UIUpdateListener;
import at.vunfer.openrealms.model.Card;
import at.vunfer.openrealms.network.DataKey;
import at.vunfer.openrealms.network.Message;
import at.vunfer.openrealms.network.MessageType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;

public class MessageHandlerTest {
    private boolean addCardToPlayAreaCalled = false;
    private boolean removeCardFromPlayAreaCalled = false;
    private boolean displayOptionsCalled = false;

    @Test
    public void handleMessage() {
        UIUpdateListener listener =
                new UIUpdateListener() {
                    @Override
                    public void updateUI(Message message) {
                        // do nothing for the test
                    }

                    @Override
                    public void addCardToPlayArea(Card card) {
                        addCardToPlayAreaCalled = true;
                    }

                    @Override
                    public void removeCardFromPlayArea(Card card) {
                        removeCardFromPlayAreaCalled = true;
                    }

                    @Override
                    public String displayOptions(List<String> options) {
                        displayOptionsCalled = true;
                        return null;
                    }
                };
        MessageHandler handler = new MessageHandler();
        handler.setListener(listener);

        // Test ADD_CARD message
        Message addCardMessage = new Message(MessageType.ADD_CARD);
        addCardMessage.setData(DataKey.CARD_ID, new Card("Card Name", 5, new ArrayList<>()));
        handler.handleMessage(addCardMessage);
        assertTrue(addCardToPlayAreaCalled);

        // Test REMOVE_CARD message
        Message removeCardMessage = new Message(MessageType.REMOVE_CARD);
        removeCardMessage.setData(DataKey.CARD_ID, new Card("Card Name", 5, new ArrayList<>()));
        handler.handleMessage(removeCardMessage);
        assertTrue(removeCardFromPlayAreaCalled);

        // Test CHOOSE_OPTION message
        Message chooseOptionMessage = new Message(MessageType.CHOOSE_OPTION);
        chooseOptionMessage.setData(
                DataKey.OPTIONS, Arrays.asList("Option 1", "Option 2", "Option 3"));
        handler.handleMessage(chooseOptionMessage);
        assertTrue(displayOptionsCalled);

        // Test unknown message type
        Message unknownMessage = new Message(MessageType.END_TURN);
        assertFalse(handler.handleMessage(unknownMessage));
    }
}
