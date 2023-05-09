/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.network.client;

import at.vunfer.openrealms.UIUpdateListener;
import at.vunfer.openrealms.model.Card;
import at.vunfer.openrealms.network.DataKey;
import at.vunfer.openrealms.network.Message;
import at.vunfer.openrealms.network.MessageType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;

public class ClientMessageHandlerTest {
    private final List<Message> updatedMessages = new ArrayList<>();

    @Test
    public void handleMessage() {
        UIUpdateListener uiUpdater =
                new UIUpdateListener() {
                    @Override
                    public void updateUI(Message message) {
                        updatedMessages.add(message);
                    }

                    @Override
                    public void addCardToPlayArea(Card card) {
                        // implementation goes here
                    }

                    @Override
                    public void removeCardFromPlayArea(Card card) {
                        // implementation goes here
                    }

                    @Override
                    public String displayOptions(List<String> options) {
                        // implementation goes here
                        return null;
                    }
                };

        ClientMessageHandler handler = new ClientMessageHandler(uiUpdater);

        // Test ADD_CARD message
        Message addCardMessage = new Message(MessageType.ADD_CARD);
        addCardMessage.setData(DataKey.CARD_ID, new Card("Card Name", 5, new ArrayList<>()));
        handler.handleMessage(addCardMessage);
        assert updatedMessages.size() == 1;
        assert updatedMessages.get(0).getType() == MessageType.ADD_CARD;

        // Test REMOVE_CARD message
        Message removeCardMessage = new Message(MessageType.REMOVE_CARD);
        removeCardMessage.setData(DataKey.CARD_ID, new Card("Card Name", 5, new ArrayList<>()));
        handler.handleMessage(removeCardMessage);
        assert updatedMessages.size() == 2;
        assert updatedMessages.get(1).getType() == MessageType.REMOVE_CARD;

        // Test CHOOSE_OPTION message
        Message chooseOptionMessage = new Message(MessageType.CHOOSE_OPTION);
        chooseOptionMessage.setOptions(Arrays.asList("Option 1", "Option 2", "Option 3"));
        handler.handleMessage(chooseOptionMessage);
        assert updatedMessages.size() == 3;
        assert updatedMessages.get(2).getType() == MessageType.CHOOSE_OPTION;
        assert updatedMessages.get(2).getSelectedOption() != null;
        assert chooseOptionMessage
                .getSelectedOption()
                .equals(updatedMessages.get(2).getSelectedOption());

        // Test unknown message type
        Message unknownMessage = new Message(MessageType.END_TURN);
        handler.handleMessage(unknownMessage);
        assert updatedMessages.size() == 3; // should not have updated UI for unknown message type
    }
}
