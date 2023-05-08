/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.network;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class MessageTest {
    private Message touchedMessage;

    @Test
    void testGetTypeTouched() {
        Message touchedMessage = new Message(MessageType.TOUCHED);
        assertEquals(MessageType.TOUCHED, touchedMessage.getType());
    }

    @Test
    void testGetTypeChoice() {
        Message choiceMessage = new Message(MessageType.CHOICE);
        assertEquals(MessageType.CHOICE, choiceMessage.getType());
    }

    @Test
    void testGetTypeEndTurn() {
        Message endTurnMessage = new Message(MessageType.END_TURN);
        assertEquals(MessageType.END_TURN, endTurnMessage.getType());
    }

    @Test
    void testGetTypeAddCard() {
        Message addCardMessage = new Message(MessageType.ADD_CARD);
        assertEquals(MessageType.ADD_CARD, addCardMessage.getType());
    }

    @Test
    void testGetTypeRemoveCard() {
        Message removeCardMessage = new Message(MessageType.REMOVE_CARD);
        assertEquals(MessageType.REMOVE_CARD, removeCardMessage.getType());
    }

    @Test
    void testGetTypeChooseOption() {
        Message chooseOptionMessage = new Message(MessageType.CHOOSE_OPTION);
        assertEquals(MessageType.CHOOSE_OPTION, chooseOptionMessage.getType());
    }

    @Test
    void testGetTypeUpdatePlayerStats() {
        Message updatePlayerStatsMessage = new Message(MessageType.UPDATE_PLAYER_STATS);
        assertEquals(MessageType.UPDATE_PLAYER_STATS, updatePlayerStatsMessage.getType());
    }

    /* Test setData() for each DataKey*/

    @Test
    void testSetDataCardID() {
        Message message = new Message(MessageType.TOUCHED);
        assertDoesNotThrow(() -> message.setData(DataKey.CARD_ID, 123));
        assertThrows(
                IllegalArgumentException.class,
                () -> message.setData(DataKey.CARD_ID, "invalid_card_id"));
    }

    @Test
    void testSetDataDeck() {
        Message message = new Message(MessageType.CHOICE);
        assertDoesNotThrow(() -> message.setData(DataKey.DECK, DeckType.DECK));
        assertThrows(IllegalArgumentException.class, () -> message.setData(DataKey.DECK, 456));
    }

    @Test
    void testSetDataPlayerStats() {
        Message message = new Message(MessageType.UPDATE_PLAYER_STATS);
        assertDoesNotThrow(() -> message.setData(DataKey.PLAYER_STATS, "invalid_player_stats"));
        assertThrows(
                IllegalArgumentException.class, () -> message.setData(DataKey.PLAYER_STATS, 5));
    }

    @Test
    void testSetDataChooseOptions() {
        Message message = new Message(MessageType.CHOOSE_OPTION);
        assertDoesNotThrow(() -> message.setData(DataKey.OPTIONS, "option"));
        assertThrows(IllegalArgumentException.class, () -> message.setData(DataKey.OPTIONS, 767));
    }

    /* Test getData() for each DataKey*/

    @Test
    void testGetDataCardID() {
        Message message = new Message(MessageType.TOUCHED);
        message.setData(DataKey.CARD_ID, 123);
        assertEquals(123, message.getData(DataKey.CARD_ID));
    }

    @Test
    void testGetDataDeck() {
        Message message = new Message(MessageType.CHOICE);
        message.setData(DataKey.DECK, DeckType.MARKET);
        assertEquals(DeckType.MARKET, message.getData(DataKey.DECK));
    }

    @Test
    void testGetDataChoice() {
        Message message = new Message(MessageType.CHOICE);
        message.setData(DataKey.CHOICE, "some choice");
        assertEquals("some choice", message.getData(DataKey.CHOICE));
    }

    @Test
    void testGetDataPlayerStats() {
        Message message = new Message(MessageType.UPDATE_PLAYER_STATS);
        message.setData(DataKey.PLAYER_STATS, "some stats");
        assertEquals("some stats", message.getData(DataKey.PLAYER_STATS));
    }

    @Test
    void testGetDataOptions() {
        Message message = new Message(MessageType.CHOOSE_OPTION);
        message.setData(DataKey.OPTIONS, "some options");
        assertEquals("some options", message.getData(DataKey.OPTIONS));
    }
}
