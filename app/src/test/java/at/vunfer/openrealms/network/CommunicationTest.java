/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.network;

import static org.mockito.Mockito.*;

import android.util.Log;
import at.vunfer.openrealms.model.Player;
import at.vunfer.openrealms.model.PlayerFactory;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class CommunicationTest {

    ObjectInputStream input;

    ObjectOutputStream output;

    IHandleMessage messageHandler;

    Communication communication;

    @BeforeEach
    void setUp() {
        input = Mockito.mock(ObjectInputStream.class);
        output = Mockito.mock(ObjectOutputStream.class);
        messageHandler = Mockito.mock(IHandleMessage.class);
        communication = new Communication(input, output, messageHandler);
    }

    @Test
    void sendMessageTest() throws IOException, InterruptedException {
        mockStatic(Log.class);
        Message msg = new Message(MessageType.TOUCHED);
        communication.sendMessage(msg);
        Thread.sleep(200); // Make sure the executor has time to run
        verify(output).writeObject(msg);
    }

    @Test
    void testCreateAddCardMessage() {
        Message m = Communication.createAddCardMessage(0, DeckType.HAND, 5);
        Assertions.assertEquals(MessageType.ADD_CARD, m.getType());
        Assertions.assertEquals(0, m.getData(DataKey.TARGET_PLAYER));
        Assertions.assertEquals(DeckType.HAND, m.getData(DataKey.DECK));
        Assertions.assertEquals(5, m.getData(DataKey.CARD_ID));
    }

    @Test
    void testCreateRemoveCardMessage() {
        Message m = Communication.createRemoveCardMessage(0, DeckType.HAND, 5);
        Assertions.assertEquals(MessageType.REMOVE_CARD, m.getType());
        Assertions.assertEquals(0, m.getData(DataKey.TARGET_PLAYER));
        Assertions.assertEquals(DeckType.HAND, m.getData(DataKey.DECK));
        Assertions.assertEquals(5, m.getData(DataKey.CARD_ID));
    }

    @Test
    void testCreateTurnNotificationMessage() {
        Message m = Communication.createTurnNotificationMessage(10);
        Assertions.assertEquals(MessageType.TURN_NOTIFICATION, m.getType());
        Assertions.assertEquals(10, m.getData(DataKey.TARGET_PLAYER));
    }

    @Test
    void testCreateRemoveMarketCardMessage() {
        Message m = Communication.createRemoveMarketCardMessage(DeckType.HAND, 5);
        Assertions.assertEquals(MessageType.REMOVE_CARD, m.getType());
        Assertions.assertEquals(DeckType.HAND, m.getData(DataKey.DECK));
        Assertions.assertEquals(5, m.getData(DataKey.CARD_ID));
    }

    @Test
    void testCreateAddMarketCardMessage() {
        Message m = Communication.createAddMarketCardMessage(DeckType.HAND, 5);
        Assertions.assertEquals(MessageType.ADD_CARD, m.getType());
        Assertions.assertEquals(DeckType.HAND, m.getData(DataKey.DECK));
        Assertions.assertEquals(5, m.getData(DataKey.CARD_ID));
    }

    @Test
    void testCreatePlayerStatsMessage() {
        Player p = PlayerFactory.createPlayer("Name");
        Message m = Communication.createPlayerStatsMessage(5, p);
        Assertions.assertEquals(MessageType.UPDATE_PLAYER_STATS, m.getType());
        Assertions.assertEquals(5, m.getData(DataKey.TARGET_PLAYER));
        PlayerStats returnedStats = (PlayerStats) m.getData(DataKey.PLAYER_STATS);
        Assertions.assertEquals("Name", returnedStats.getPlayerName());
        Assertions.assertEquals(30, returnedStats.getPlayerHealth());
        Assertions.assertEquals(0, returnedStats.getTurnDamage());
        Assertions.assertEquals(0, returnedStats.getTurnCoin());
        Assertions.assertEquals(0, returnedStats.getTurnHealing());
    }
    // TODO listen for message test; cannot verify(messageHandler).handleMessage(msg) easily because
    // of the executor service
}
