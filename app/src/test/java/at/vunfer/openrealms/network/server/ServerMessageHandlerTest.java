/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.network.server;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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
import org.junit.jupiter.api.BeforeAll;
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

    @BeforeEach
    void setUp() throws IOException {
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
                () -> Communication.createAddCardMessage(eq(1), eq(DeckType.DISCARD), anyInt()),
                times(1));
        communication.verify(() -> Communication.createPlayerStatsMessage(eq(1), any()), times(1));
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
        communication.verify(() -> Communication.createPlayerStatsMessage(eq(1), any()), times(1));
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
                () -> Communication.createAddCardMessage(eq(1), eq(DeckType.DISCARD), anyInt()),
                times(1));
        communication.verify(() -> Communication.createPlayerStatsMessage(eq(1), any()), times(1));
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
    void testHandleCheat() {
        //mockStatic(Log.class);

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
