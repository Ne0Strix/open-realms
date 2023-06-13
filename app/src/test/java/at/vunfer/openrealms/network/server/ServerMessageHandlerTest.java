/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.network.server;

import static at.vunfer.openrealms.network.Communication.createAddCardMessage;
import static at.vunfer.openrealms.network.Communication.createPlayerStatsMessage;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.testng.AssertJUnit.assertNotNull;

import android.content.Context;
import android.util.Log;
import at.vunfer.openrealms.model.Card;
import at.vunfer.openrealms.model.Deck;
import at.vunfer.openrealms.model.GameSession;
import at.vunfer.openrealms.model.PlayArea;
import at.vunfer.openrealms.model.Player;
import at.vunfer.openrealms.network.Communication;
import at.vunfer.openrealms.network.DataKey;
import at.vunfer.openrealms.network.DeckType;
import at.vunfer.openrealms.network.Message;
import at.vunfer.openrealms.network.MessageType;
import java.io.IOException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

class ServerMessageHandlerTest {
    private ServerThread serverThread;
    private MockedStatic<Communication> communication;
    private MockedStatic<ServerThread> mockedServerThread;
    private ServerMessageHandler serverMessageHandler;
    private GameSession gameSession;
    private Player player;
    private PlayArea playArea;
    private Message message;
    private Card card;
    private Deck<Card> discardedChampions;
    private MockedStatic<ServerMessageHandlerTest> mockedStatic;

    @BeforeEach
    void setUp() throws IOException {
        mockedStatic = mockStatic(ServerMessageHandlerTest.class);
        ServerThread normalThread = new ServerThread(mock(Context.class), 69);
        serverThread = Mockito.spy(normalThread);

        ServerMessageHandler normalHandler = new ServerMessageHandler();
        serverMessageHandler = Mockito.spy(normalHandler);

        gameSession = Mockito.mock(GameSession.class);
        player = Mockito.mock(Player.class);
        playArea = Mockito.mock(PlayArea.class);
        message = Mockito.mock(Message.class);
        card = Mockito.mock(Card.class);
        discardedChampions = new Deck<>();
        discardedChampions.add(card);

        when(gameSession.getCurrentPlayer()).thenReturn(player);
        when(gameSession.getOpponent(any())).thenReturn(player);
        when(gameSession.getPlayerTurnNumber(any())).thenReturn(1);
        when(serverThread.getGameSession()).thenReturn(gameSession);
        when(player.getPlayerName()).thenReturn("Player");
        when(player.getPlayArea()).thenReturn(playArea);
        when(playArea.getHealth()).thenReturn(1);
        when(playArea.getTurnDamage()).thenReturn(2);
        when(playArea.getTurnHealing()).thenReturn(3);
        when(playArea.getTurnCoins()).thenReturn(4);
        when(playArea.getAtTurnEndDiscardedChampions()).thenReturn(discardedChampions);

        // Mock static getInstance() method of ServerThread
        mockedServerThread = Mockito.mockStatic(ServerThread.class);
        mockedServerThread.when(ServerThread::getInstance).thenReturn(serverThread);

        communication = mockStatic(Communication.class, CALLS_REAL_METHODS);
        doNothing().when(serverThread).sendMessageToAllClients(any());
    }

    @AfterEach
    void tearDown() {
        communication.close();
        communication = null;

        mockedServerThread.close();
        mockedServerThread = null;
    }

    @Test
    void testSendChampionsKilledToAllClients() throws IOException {

        serverMessageHandler.ensureServerThreadInitialized();

        serverMessageHandler.sendChampionKilledToAllClients(gameSession, player, 1);

        verify(serverThread, times(3)).sendMessageToAllClients(any());
        communication.verify(
                () ->
                        Communication.createRemoveCardMessage(
                                eq(1), eq(DeckType.CHAMPIONS), anyInt()),
                times(1));
        communication.verify(
                () -> createAddCardMessage(eq(1), eq(DeckType.DISCARD), anyInt()), times(1));
        communication.verify(() -> createPlayerStatsMessage(eq(1), any()), times(1));
    }

    @Test
    void testSendChampionExpendToAllClients() throws IOException {
        serverMessageHandler.ensureServerThreadInitialized();

        // check case, if it is already initialized
        serverMessageHandler.ensureServerThreadInitialized();

        serverMessageHandler.sendChampionExpendedToAllClients(gameSession, player, 1);

        verify(serverThread, times(2)).sendMessageToAllClients(any());
        communication.verify(
                () -> Communication.createExpendChampionMessage(eq(1), anyInt()), times(1));
    }

    @Test
    void testHandleKilledChampionsAtTurnEnd() throws IOException {
        serverMessageHandler.ensureServerThreadInitialized();
        serverMessageHandler.handleKilledChampionsAtTurnEnd(gameSession, player);

        verify(serverThread, times(3)).sendMessageToAllClients(any());
        verify(serverMessageHandler, times(1))
                .sendChampionKilledToAllClients(any(), any(), anyInt());

        communication.verify(
                () ->
                        Communication.createRemoveCardMessage(
                                eq(1), eq(DeckType.CHAMPIONS), anyInt()),
                times(1));
        communication.verify(
                () -> createAddCardMessage(eq(1), eq(DeckType.DISCARD), anyInt()), times(1));
        communication.verify(() -> createPlayerStatsMessage(eq(1), any()), times(1));
    }

    @Test
    void testHandleKilledChampionsAtTurnEndException() throws IOException {
        serverMessageHandler.ensureServerThreadInitialized();

        doThrow(new IOException()).when(serverThread).sendMessageToAllClients(any());
        assertThrows(
                RuntimeException.class,
                () -> serverMessageHandler.handleKilledChampionsAtTurnEnd(gameSession, player));
    }

    @Test
    void handleCheat() {
        try (MockedStatic<Log> mockedLog = mockStatic(Log.class)) {
            serverMessageHandler.ensureServerThreadInitialized();

            Message message = Mockito.mock(Message.class);
            when(message.getData(DataKey.CHEAT_ACTIVATE)).thenReturn(true);

            GameSession gameSession = serverThread.getGameSession();
            Player currentPlayer = gameSession.getCurrentPlayer();
            PlayArea playArea = currentPlayer.getPlayArea();

            serverMessageHandler.handleCheat(message, currentPlayer);

            verify(playArea, times(1)).setCheat(true);
            verify(serverThread, times(1)).sendCheatStatusToAll(true);
            Log.i(eq(ServerMessageHandler.TAG), anyString());
        }
    }

    @Test
    void testHandleMessageChoice() {
        ServerMessageHandler handler = new ServerMessageHandler();
        Message message = new Message(MessageType.CHOICE);

        assertDoesNotThrow(() -> handler.handleMessage(message));
    }

    @Test
    void testHandleMessageUncoverCheat() {
        ServerMessageHandler handler = new ServerMessageHandler();
        Message message = new Message(MessageType.UNCOVER_CHEAT);

        assertDoesNotThrow(() -> handler.handleMessage(message));
    }

    @Test
    void testHandleTouchedCardInvalidCardId() throws IOException {
        try (MockedStatic<Log> mockedLog = mockStatic(Log.class)) {

            serverMessageHandler.ensureServerThreadInitialized();

            Message message = Mockito.mock(Message.class);
            when(message.getType()).thenReturn(MessageType.TOUCHED);
            when(message.getData(DataKey.CARD_ID)).thenReturn(999); // Invalid card ID

            serverMessageHandler.handleTouchedCard(message, gameSession, player);

            verify(serverThread, never()).sendMessageToAllClients(any());
        }
    }

    @Test
    void testHandleTouchedCardValidCardId() throws IOException {
        try (MockedStatic<Log> mockedLog = mockStatic(Log.class)) {

            serverMessageHandler.ensureServerThreadInitialized();

            Message message = Mockito.mock(Message.class);
            when(message.getType()).thenReturn(MessageType.TOUCHED);
            when(message.getData(DataKey.CARD_ID)).thenReturn(1);

            serverMessageHandler.handleTouchedCard(message, gameSession, player);

            verify(player.getPlayArea(), times(1)).playCardById(eq(1));
        }
    }

    @Test
    void testHandleTouchedCardCheatActivated() throws IOException {
        try (MockedStatic<Log> mockedLog = mockStatic(Log.class)) {

            serverMessageHandler.ensureServerThreadInitialized();

            Message message = Mockito.mock(Message.class);
            when(message.getType()).thenReturn(MessageType.TOUCHED);
            when(message.getData(DataKey.CARD_ID))
                    .thenReturn(2); // Card ID that triggers cheat activation

            serverMessageHandler.handleTouchedCard(message, gameSession, player);

            verify(player.getPlayArea(), times(1)).playCardById(eq(2));
            Log.i(eq(ServerMessageHandler.TAG), eq("Cheat mode set to true"));
        }
    }

    @Test
    void testEnsureServerThreadInitialized() {
        serverMessageHandler.ensureServerThreadInitialized();

        assertNotNull(serverThread);
    }

    @Test
    void testHandleTouchedCardCheatAlreadyActivated() throws IOException {
        try (MockedStatic<Log> mockedLog = mockStatic(Log.class)) {
            serverMessageHandler.ensureServerThreadInitialized();

            Message message = Mockito.mock(Message.class);
            when(message.getType()).thenReturn(MessageType.TOUCHED);
            when(message.getData(DataKey.CARD_ID))
                    .thenReturn(2); // Card ID that triggers cheat activation

            Player currentPlayer = gameSession.getCurrentPlayer();
            PlayArea playArea = currentPlayer.getPlayArea();
            playArea.setCheat(true);

            serverMessageHandler.handleTouchedCard(message, gameSession, player);

            verify(player.getPlayArea(), times(1)).playCardById(eq(2));
            mockedLog.verify(
                    () -> Log.i(eq(ServerMessageHandler.TAG), eq("Cheat mode set to true")),
                    never());
        }
    }

    @Test
    void testHandleTouchedCardNullPointerException() throws NullPointerException {
        serverMessageHandler.ensureServerThreadInitialized();

        Message message = Mockito.mock(Message.class);
        when(message.getType()).thenReturn(MessageType.TOUCHED);
        when(message.getData(DataKey.CARD_ID)).thenReturn(1);

        PlayArea playArea = Mockito.mock(PlayArea.class);
        when(player.getPlayArea()).thenReturn(playArea);

        doThrow(new NullPointerException()).when(playArea).playCardById(eq(1));

        assertThrows(
                NullPointerException.class,
                () -> serverMessageHandler.handleTouchedCard(message, gameSession, player));
    }

    @Test
    void testHandleEndTurnException() throws IOException {
        serverMessageHandler.ensureServerThreadInitialized();

        GameSession gameSession = serverThread.getGameSession();
        Player currentPlayer = gameSession.getCurrentPlayer();

        doThrow(new IOException()).when(serverThread).sendMessageToAllClients(any());

        assertThrows(
                RuntimeException.class,
                () -> serverMessageHandler.handleEndTurn(gameSession, currentPlayer));
    }

    @Test
    void testCheckDrawnCardException() throws IOException {
        serverMessageHandler.ensureServerThreadInitialized();

        doThrow(new IOException()).when(serverThread).sendMessageToAllClients(any());

        assertThrows(
                RuntimeException.class,
                () -> serverMessageHandler.checkDrawnCard(gameSession, player));
    }

    @AfterEach
    public void cleanup() {
        mockedStatic.close();
    }
}
