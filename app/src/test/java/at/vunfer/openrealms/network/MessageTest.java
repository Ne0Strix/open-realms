/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.network;

import static org.junit.jupiter.api.Assertions.*;

import at.vunfer.openrealms.model.Card;
import at.vunfer.openrealms.model.CardType;
import at.vunfer.openrealms.model.Deck;
import at.vunfer.openrealms.model.Effect;
import at.vunfer.openrealms.model.effects.CoinEffect;
import java.util.ArrayList;
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

    @Test
    void testGetTypeFullCardDeck() {
        Message updatePlayerStatsMessage = new Message(MessageType.FULL_CARD_DECK);
        assertEquals(MessageType.FULL_CARD_DECK, updatePlayerStatsMessage.getType());
    }

    @Test
    void testGetTypeTurnNotification() {
        Message updatePlayerStatsMessage = new Message(MessageType.TURN_NOTIFICATION);
        assertEquals(MessageType.TURN_NOTIFICATION, updatePlayerStatsMessage.getType());
    }

    /* Test setData() for each DataKey*/

    @Test
    void testSetDataTargetPlayer() {
        Message message = new Message(MessageType.TURN_NOTIFICATION);
        assertDoesNotThrow(() -> message.setData(DataKey.TARGET_PLAYER, 1));
        assertThrows(
                IllegalArgumentException.class,
                () -> message.setData(DataKey.TARGET_PLAYER, "invalid_player_id"));
    }

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
        PlayerStats playerStats = new PlayerStats("Name", 70, 1, 2, 3);
        assertDoesNotThrow(() -> message.setData(DataKey.PLAYER_STATS, playerStats));
        assertThrows(
                IllegalArgumentException.class, () -> message.setData(DataKey.PLAYER_STATS, 5));
    }

    @Test
    void testSetDataChooseOptions() {
        Message message = new Message(MessageType.CHOOSE_OPTION);
        assertDoesNotThrow(() -> message.setData(DataKey.OPTIONS, "option"));
        assertThrows(IllegalArgumentException.class, () -> message.setData(DataKey.OPTIONS, 767));
    }

    @Test
    void testSetDataChoice() {
        Message message = new Message(MessageType.CHOICE);
        assertDoesNotThrow(() -> message.setData(DataKey.CHOICE, "option"));
        assertThrows(IllegalArgumentException.class, () -> message.setData(DataKey.CHOICE, 767));
    }

    @Test
    void testSetDataYourTurn() {
        Message message = new Message(MessageType.TURN_NOTIFICATION);
        assertDoesNotThrow(() -> message.setData(DataKey.TARGET_PLAYER, 0));
        assertThrows(
                IllegalArgumentException.class,
                () -> message.setData(DataKey.TARGET_PLAYER, "player1"));
    }

    @Test
    void testSetDataCollection() {
        Message message = new Message(MessageType.FULL_CARD_DECK);
        Effect testEffect = new CoinEffect(1);
        ArrayList effectlist = new ArrayList<>();
        effectlist.add(testEffect);
        Card card1 = new Card("Test", 1, CardType.NONE, effectlist);
        Deck<Card> cards = new Deck<>();
        cards.add(card1);
        assertDoesNotThrow(() -> message.setData(DataKey.COLLECTION, cards));
        assertThrows(
                IllegalArgumentException.class, () -> message.setData(DataKey.COLLECTION, 767));
    }

    /* Test getData() for each DataKey*/

    @Test
    void testGetDataTargetPlayer() {
        Message message = new Message(MessageType.TURN_NOTIFICATION);
        message.setData(DataKey.TARGET_PLAYER, 1);
        assertEquals(1, (int) message.getData(DataKey.TARGET_PLAYER));
    }

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
    void testGetDataOptions() {
        Message message = new Message(MessageType.CHOOSE_OPTION);
        message.setData(DataKey.OPTIONS, "some options");
        assertEquals("some options", message.getData(DataKey.OPTIONS));
    }

    @Test
    void testGetDataPlayerStats() {
        Message message = new Message(MessageType.UPDATE_PLAYER_STATS);
        PlayerStats playerStats = new PlayerStats("Name", 70, 1, 3, 5);
        message.setData(DataKey.PLAYER_STATS, playerStats);
        assertEquals(playerStats, message.getData(DataKey.PLAYER_STATS));
    }

    @Test
    void testGetDataYourTurn() {
        Message message = new Message(MessageType.TURN_NOTIFICATION);
        message.setData(DataKey.TARGET_PLAYER, 0);
        assertEquals(0, (int) message.getData(DataKey.TARGET_PLAYER));
    }

    @Test
    void testGetDataCollection() {
        Message message = new Message(MessageType.FULL_CARD_DECK);
        Effect testEffect = new CoinEffect(1);
        ArrayList effectlist = new ArrayList<>();
        effectlist.add(testEffect);
        Card card1 = new Card("Test", 1, CardType.NONE, effectlist);
        Deck<Card> cards = new Deck<>();
        cards.add(card1);
        message.setData(DataKey.COLLECTION, cards);
        assertEquals(cards, message.getData(DataKey.COLLECTION));
    }
}
