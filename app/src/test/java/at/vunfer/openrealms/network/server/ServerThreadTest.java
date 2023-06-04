/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.network.server;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import android.annotation.SuppressLint;
import android.content.Context;
import at.vunfer.openrealms.model.Card;
import at.vunfer.openrealms.model.Deck;
import at.vunfer.openrealms.model.Faction;
import at.vunfer.openrealms.model.Market;
import at.vunfer.openrealms.model.Player;
import at.vunfer.openrealms.model.PlayerFactory;
import at.vunfer.openrealms.model.effects.DamageEffect;
import at.vunfer.openrealms.network.Communication;
import at.vunfer.openrealms.network.DeckType;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

public class ServerThreadTest {
    private ServerThread serverThread;
    private MockedStatic<Communication> communication;

    @BeforeEach
    void setUp() {
        ServerThread normalThread = new ServerThread(mock(Context.class), 69);
        serverThread = Mockito.spy(normalThread);
        communication = mockStatic(Communication.class, CALLS_REAL_METHODS);
    }

    @AfterEach
    void tearDown() {
        communication.close();
        communication = null;
    }

    @Test
    void testRun() throws Exception {
        // PowerMockito.when(serverThread, "getIPAddress").thenReturn("127.0.0.1");
        // doReturn("127.0.0.1").when(serverThread,"getIPAddress");
        // PowerMockito.suppress(PowerMockito.method(ServerThread.class, "getIPAddress"));
        // TODO: Calls the methode it is supposed to mock for some fucking reason:
        //    when(serverThread, "getIPAddress").thenReturn("127.0.0.1");
        /*
                ServerSocket mockedSocket = mock(ServerSocket.class);
                whenNew(ServerSocket.class).withAnyArguments().thenReturn(mockedSocket);
                doReturn(mock(Socket.class)).when(mockedSocket).accept();

                mockStatic(DeckGenerator.class);
                when(DeckGenerator.generatePlayerStarterDeck(any())).thenReturn(new Deck<>());
                when(DeckGenerator.generateMarketDeck(any())).thenReturn(new Deck<>());

                serverThread.run();

                verify(serverThread.getIpAddr());
                verify(serverThread.getIpAddr(), times(2));
                verify(DeckGenerator.generateMarketDeck(any()));
                verify(DeckGenerator.generatePlayerStarterDeck(any()), times(2));
        */
    }

    @SuppressLint("CheckResult")
    @Test
    void testSendTurnNotificationToAllClients() throws IOException {
        serverThread.sendTurnNotificationToAllClients(5);

        verify(serverThread).sendMessageToAllClients(any());
        communication.verify(() -> Communication.createTurnNotificationMessage(5));
    }

    @SuppressLint("CheckResult")
    @Test
    void testSendTurnNotificationToAllClientsThrows() throws IOException {
        doThrow(new IOException()).when(serverThread).sendMessageToAllClients(any());

        assertThrows(
                RuntimeException.class, () -> serverThread.sendTurnNotificationToAllClients(5));

        communication.verify(() -> Communication.createTurnNotificationMessage(5));
    }

    @SuppressLint("CheckResult")
    @Test
    void testSendRestockDeckFromDiscard() throws IOException {
        Deck<Card> deck = new Deck<>();
        deck.add(new Card("name", 0, Faction.NONE, List.of(new DamageEffect(0))));
        deck.add(new Card("name2", 0, Faction.NONE, List.of(new DamageEffect(0))));

        doNothing().when(serverThread).sendMessageToAllClients(any());

        serverThread.sendRestockDeckFromDiscard(1, deck);

        verify(serverThread, times(deck.size() * 2)).sendMessageToAllClients(any());
        communication.verify(
                () -> Communication.createRemoveCardMessage(eq(1), eq(DeckType.DISCARD), anyInt()),
                times(deck.size()));
        communication.verify(
                () -> Communication.createAddCardMessage(eq(1), eq(DeckType.DECK), anyInt()),
                times(deck.size()));
    }

    @SuppressLint("CheckResult")
    @Test
    void testSendRestockDeckFromDiscardThrows() throws IOException {
        Deck<Card> deck = new Deck<>();
        deck.add(new Card("name", 0, Faction.NONE, List.of(new DamageEffect(0))));
        deck.add(new Card("name2", 0, Faction.NONE, List.of(new DamageEffect(0))));

        doThrow(new IOException()).when(serverThread).sendMessageToAllClients(any());

        assertThrows(
                RuntimeException.class, () -> serverThread.sendRestockDeckFromDiscard(1, deck));
    }

    @SuppressLint("CheckResult")
    @Test
    void testDealMarketCardToPurchaseAreToAll() throws IOException {
        Deck<Card> deck = new Deck<>();
        deck.add(new Card("name", 0, Faction.NONE, List.of(new DamageEffect(0))));
        deck.add(new Card("name2", 0, Faction.NONE, List.of(new DamageEffect(0))));
        Market.getInstance().setNewToPurchase(deck);

        doNothing().when(serverThread).sendMessageToAllClients(any());

        serverThread.dealMarketCardsToPurchaseAreaToAll();

        verify(serverThread, times(deck.size() * 2)).sendMessageToAllClients(any());
        communication.verify(
                () -> Communication.createRemoveMarketCardMessage(eq(DeckType.MARKET), anyInt()),
                times(deck.size()));
        communication.verify(
                () -> Communication.createAddMarketCardMessage(eq(DeckType.FOR_PURCHASE), anyInt()),
                times(deck.size()));
        assertTrue(Market.getInstance().getNewToPurchase().isEmpty());
    }

    @SuppressLint("CheckResult")
    @Test
    void testDealHandCardsBasedOnTurnNumberException() throws IOException {
        Card c1 = new Card("name", 0, Faction.NONE, List.of(new DamageEffect(0)));

        Player player = PlayerFactory.createPlayer("player");
        player.getPlayArea().getPlayerCards().getHandCards().add(c1);
        doThrow(new IOException()).when(serverThread).sendMessageToAllClients(any());

        assertThrows(
                RuntimeException.class,
                () -> serverThread.dealHandCardsBasedOnTurnNumber(1, player));
    }

    @SuppressLint("CheckResult")
    @Test
    void testDealHandCardsBasedOnTurnNumber() throws IOException {
        Card c1 = new Card("name", 0, Faction.NONE, List.of(new DamageEffect(0)));

        Player player = PlayerFactory.createPlayer("player");
        player.getPlayArea().getPlayerCards().getHandCards().add(c1);

        doNothing().when(serverThread).sendMessageToAllClients(any());

        serverThread.dealHandCardsBasedOnTurnNumber(1, player);
    }

    @SuppressLint("CheckResult")
    @Test
    void testDiscardCardsAfterTurn() throws IOException {
        Card c1 = new Card("name", 0, Faction.NONE, List.of(new DamageEffect(0)));
        Card c2 = (new Card("name2", 0, Faction.NONE, List.of(new DamageEffect(0))));

        Player player = PlayerFactory.createPlayer("player");
        player.getPlayArea().getPlayerCards().getHandCards().add(c1);
        player.getPlayArea().getPlayerCards().getHandCards().add(c2);
        player.getPlayArea().getPlayedCards().add(new Card(c1));
        player.getPlayArea().getPlayedCards().add(new Card(c2));
        doNothing().when(serverThread).sendMessageToAllClients(any());

        serverThread.discardCardsAfterTurn(1, player);

        verify(serverThread, times(8)).sendMessageToAllClients(any());
        communication.verify(
                () -> Communication.createRemoveCardMessage(eq(1), eq(DeckType.HAND), anyInt()),
                times(2));
        communication.verify(
                () -> Communication.createRemoveCardMessage(eq(1), eq(DeckType.PLAYED), anyInt()),
                times(2));
        communication.verify(
                () -> Communication.createAddCardMessage(eq(1), eq(DeckType.DISCARD), anyInt()),
                times(4));
    }

    @SuppressLint("CheckResult")
    @Test
    void testDiscardCardsAfterTurnExceptions() throws IOException {
        Card c1 = new Card("name", 0, Faction.NONE, List.of(new DamageEffect(0)));

        Player player = PlayerFactory.createPlayer("player");
        player.getPlayArea().getPlayerCards().getHandCards().add(c1);
        player.getPlayArea().getPlayedCards().add(new Card(c1));
        doThrow(new IOException()).when(serverThread).sendMessageToAllClients(any());

        assertThrows(RuntimeException.class, () -> serverThread.discardCardsAfterTurn(1, player));
        player.getPlayArea().getPlayerCards().getHandCards().clear();
        assertThrows(RuntimeException.class, () -> serverThread.discardCardsAfterTurn(1, player));
    }

    @SuppressLint("CheckResult")
    @Test
    void testDealHandCards() {
        Card c1 = new Card("name", 0, Faction.NONE, List.of(new DamageEffect(0)));
        Card c2 = (new Card("name2", 0, Faction.NONE, List.of(new DamageEffect(0))));

        Player player = PlayerFactory.createPlayer("player");
        player.getPlayArea().getPlayerCards().getHandCards().add(c1);
        player.getPlayArea().getPlayerCards().getHandCards().add(c2);

        ClientHandler clientHandler = mock(ClientHandler.class);

        serverThread.dealHandCards(clientHandler, 1, player);

        verify(clientHandler, times(4)).sendMessage(any());
        communication.verify(
                () -> Communication.createRemoveCardMessage(eq(1), eq(DeckType.DECK), anyInt()),
                times(2));
        communication.verify(
                () -> Communication.createAddCardMessage(eq(1), eq(DeckType.HAND), anyInt()),
                times(2));
    }

    @SuppressLint("CheckResult")
    @Test
    void testDealDeckCards() {
        Card c1 = new Card("name", 0, Faction.NONE, List.of(new DamageEffect(0)));
        Card c2 = (new Card("name2", 0, Faction.NONE, List.of(new DamageEffect(0))));

        Player player = PlayerFactory.createPlayer("player");
        player.getPlayArea().getPlayerCards().getDeckCards().add(c1);
        player.getPlayArea().getPlayerCards().getDeckCards().add(c2);

        ClientHandler clientHandler = mock(ClientHandler.class);

        serverThread.dealDeckCards(clientHandler, 1, player);

        verify(clientHandler, times(4)).sendMessage(any());
        communication.verify(
                () -> Communication.createRemoveCardMessage(eq(1), eq(DeckType.DISCARD), anyInt()),
                times(2));
        communication.verify(
                () -> Communication.createAddCardMessage(eq(1), eq(DeckType.DECK), anyInt()),
                times(2));
    }

    @SuppressLint("CheckResult")
    @Test
    void testDealMarketCardToPurchaseAreToAllException() throws IOException {
        Deck<Card> deck = new Deck<>();
        deck.add(new Card("name", 0, Faction.NONE, List.of(new DamageEffect(0))));
        deck.add(new Card("name2", 0, Faction.NONE, List.of(new DamageEffect(0))));
        Market.getInstance().setNewToPurchase(deck);

        doThrow(new IOException()).when(serverThread).sendMessageToAllClients(any());

        assertThrows(
                RuntimeException.class, () -> serverThread.dealMarketCardsToPurchaseAreaToAll());
    }
}
