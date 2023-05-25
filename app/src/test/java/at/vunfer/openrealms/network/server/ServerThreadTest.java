package at.vunfer.openrealms.network.server;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import android.annotation.SuppressLint;
import android.content.Context;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;


import java.io.IOException;
import java.util.List;

import at.vunfer.openrealms.model.Card;
import at.vunfer.openrealms.model.Deck;
import at.vunfer.openrealms.model.Market;
import at.vunfer.openrealms.model.effects.DamageEffect;
import at.vunfer.openrealms.network.Communication;
import at.vunfer.openrealms.network.DeckType;

public class ServerThreadTest {
    private ServerThread serverThread;

    @BeforeEach
    void setUp() {
        ServerThread normalThread = new ServerThread(mock(Context.class), 69);
        serverThread = Mockito.spy(normalThread);
    }

    @Test
    void testRun() throws Exception {
        //PowerMockito.when(serverThread, "getIPAddress").thenReturn("127.0.0.1");
        //doReturn("127.0.0.1").when(serverThread,"getIPAddress");
        //PowerMockito.suppress(PowerMockito.method(ServerThread.class, "getIPAddress"));
        //TODO: Calls the methode it is supposed to mock for some fucking reason:
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

    @Test
    void testGetInstance() {
        assertEquals(serverThread, ServerThread.getInstance());
    }

    @SuppressLint("CheckResult")
    @Test
    void testDealMarketCardToPurchaseAreToAll() throws IOException {
        Deck<Card> deck = new Deck<>();
        deck.add(new Card("name", 0, List.of(new DamageEffect(0))));
        deck.add(new Card("name2", 0, List.of(new DamageEffect(0))));
        Market.getInstance().setNewToPurchase(deck);

        doNothing().when(serverThread).sendMessageToAllClients(any());
        MockedStatic<Communication> communication = mockStatic(Communication.class, InvocationOnMock::callRealMethod);

        serverThread.dealMarketCardsToPurchaseAreaToAll();

        verify(serverThread, times(deck.size() * 2)).sendMessageToAllClients(any());
        //TODO: fix verification for correct Messages to be sent
        communication.verify(() -> Communication.createRemoveMarketCardMessage(DeckType.MARKET, any()), times(deck.size()));
        assertTrue(Market.getInstance().getForPurchase().isEmpty());

        communication.close();
    }

    @SuppressLint("CheckResult")
    @Test
    void testDealMarketCardToPurchaseAreToAllException() throws IOException {
        Deck<Card> deck = new Deck<>();
        deck.add(new Card("name", 0, List.of(new DamageEffect(0))));
        deck.add(new Card("name2", 0, List.of(new DamageEffect(0))));
        Market.getInstance().setNewToPurchase(deck);

        doThrow(new IOException()).when(serverThread).sendMessageToAllClients(any());

        assertThrows(RuntimeException.class, () -> serverThread.dealMarketCardsToPurchaseAreaToAll());
    }

    @SuppressLint("CheckResult")
    @Test
    void testDealMarketCardsToPurchaseArea() throws IOException {
        //TODO
        Deck<Card> deck = new Deck<>();
        deck.add(new Card("name", 0, List.of(new DamageEffect(0))));
        deck.add(new Card("name2", 0, List.of(new DamageEffect(0))));
        Market.getInstance().setNewToPurchase(deck);

        doThrow(new IOException()).when(serverThread).sendMessageToAllClients(any());

        assertThrows(RuntimeException.class, () -> serverThread.dealMarketCardsToPurchaseAreaToAll());
    }
}
