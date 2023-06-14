/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.network.server;

import static at.vunfer.openrealms.network.Communication.createAddCardMessage;
import static at.vunfer.openrealms.network.Communication.createAddMarketCardMessage;
import static at.vunfer.openrealms.network.Communication.createCheatStatusMessage;
import static at.vunfer.openrealms.network.Communication.createPlayerStatsMessage;
import static at.vunfer.openrealms.network.Communication.createRemoveCardMessage;
import static at.vunfer.openrealms.network.Communication.createRemoveMarketCardMessage;
import static at.vunfer.openrealms.network.Communication.createResetChampionMessage;
import static at.vunfer.openrealms.network.Communication.createTurnNotificationMessage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import at.vunfer.openrealms.MainActivity;
import at.vunfer.openrealms.model.Card;
import at.vunfer.openrealms.model.Champion;
import at.vunfer.openrealms.model.Deck;
import at.vunfer.openrealms.model.DeckGenerator;
import at.vunfer.openrealms.model.GameSession;
import at.vunfer.openrealms.model.Market;
import at.vunfer.openrealms.model.Player;
import at.vunfer.openrealms.model.PlayerFactory;
import at.vunfer.openrealms.network.Communication;
import at.vunfer.openrealms.network.DataKey;
import at.vunfer.openrealms.network.DeckType;
import at.vunfer.openrealms.network.Message;
import at.vunfer.openrealms.network.MessageType;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ServerThread extends Thread {
    private static final String TAG = "ServerThread";
    private final Context context;
    private final int port;
    private String ipAddr = "empty";

    @SuppressLint("StaticFieldLeak")
    private static ServerThread instance;

    private GameSession gameSession;
    private ServerSocket serverSocket;
    private final ArrayList<ClientHandler> connections = new ArrayList<>();

    public ServerThread(Context context, int port) {
        this.context = context;
        this.port = port;
        instance = this;
    }

    @Override
    public void run() {
        try {
            setupServer();
            ((MainActivity) context).showIp(new View(context));
            acceptConnections();
        } catch (IOException ex) {
            Log.e(TAG, "IO Exception on Server!", ex);
        }
        if (!serverSocket.isClosed()) {
            createGame();
            ((MainActivity) context)
                    .runOnUiThread(() -> ((MainActivity) context).startGame(new View(context)));
        }
    }

    private void setupServer() throws IOException {
        serverSocket = new ServerSocket(this.port);
        ipAddr = getIPAddress();
        Log.i(TAG, "Server reachable under: " + ipAddr + ":" + serverSocket.getLocalPort());
    }

    private void acceptConnections() throws IOException {
        connections.add(acceptClient("Local client connected!"));
        connections.add(acceptClient("Remote client connected!"));
    }

    private ClientHandler acceptClient(String logMessage) throws IOException {
        ClientHandler client = new ClientHandler(serverSocket.accept());
        Log.i(TAG, logMessage);
        return client;
    }

    public void setupClients() {
        for (ClientHandler client : connections) {
            int playerTurnNumber = getTurnNumber(client);
            Player player = gameSession.getPlayers().get(playerTurnNumber);

            Log.i(
                    "DECK TROUBLE",
                    "Size of deck setupClients: "
                            + player.getPlayArea().getPlayerCards().getDeckCards().size());

            Player opponent = gameSession.getOpponent(player);
            int opponentTurnNumber = gameSession.getPlayers().indexOf(opponent);

            sendPlayerAndOpponentStats(
                    client, playerTurnNumber, player, opponentTurnNumber, opponent);
            sendFullDeck(client);
            dealCardsToPlayerAndOpponent(
                    client, playerTurnNumber, player, opponentTurnNumber, opponent);
            dealMarketCardsToPurchaseArea(client);
        }
        sendTurnNotificationToAllClients(
                gameSession.getPlayerTurnNumber(gameSession.getCurrentPlayer()));
    }

    private void createGame() {
        Player player1 = PlayerFactory.createPlayer("Player 1");
        Player player2 = PlayerFactory.createPlayer("Player 2");
        player1.getPlayArea()
                .getPlayerCards()
                .setDeckCards(DeckGenerator.generatePlayerStarterDeck(context));
        player2.getPlayArea()
                .getPlayerCards()
                .setDeckCards(DeckGenerator.generatePlayerStarterDeck(context));
        Market.getInstance().setMarketDeck(DeckGenerator.generateMarketDeck(context));
        List<Player> players = List.of(player1, player2);
        gameSession = new GameSession(players, player1);
        Log.i(
                "DECK TROUBLE",
                "Size of deck createGame: "
                        + player1.getPlayArea().getPlayerCards().getDeckCards().size());
    }

    public int getTurnNumber(ClientHandler client) {
        return connections.indexOf(client);
    }

    public void stopServer() throws IOException {
        int i = 1;
        Log.i(TAG, "Stopping server...");
        for (ClientHandler client : connections) {
            Log.i(TAG, "Disconnecting client " + i);
            client.disconnectClient();
            Log.i(TAG, "Client " + i + " disconnected!");
            i++;
        }
        Log.i(TAG, "All clients disconnected!");
        Log.i(TAG, "Closing server socket...");
        serverSocket.close();
        Log.i(TAG, "Server socket closed!");
    }

    public ArrayList<ClientHandler> getConnections() {
        return connections;
    }

    public InetAddress getLocalAddress() {
        return serverSocket.getInetAddress();
    }

    public void sendMessageToAllClients(Message message) throws IOException {
        for (ClientHandler client : connections) {
            client.sendMessage(message);
        }
    }

    public String getIpAddr() {
        return ipAddr;
    }

    private String getIPAddress() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                WifiManager wifiManager =
                        (WifiManager)
                                context.getApplicationContext()
                                        .getSystemService(Context.WIFI_SERVICE);
                int ipAddress = wifiManager.getConnectionInfo().getIpAddress();
                return Formatter.formatIpAddress(ipAddress);
            } else if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                try {
                    for (NetworkInterface networkInterface :
                            Collections.list(NetworkInterface.getNetworkInterfaces())) {
                        if (!networkInterface.isLoopback() && networkInterface.isUp()) {
                            for (InetAddress inetAddress :
                                    Collections.list(networkInterface.getInetAddresses())) {
                                if (!inetAddress.isLoopbackAddress()
                                        && inetAddress.getAddress().length == 4) {
                                    return inetAddress.getHostAddress();
                                }
                            }
                        }
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private void sendPlayerAndOpponentStats(
            ClientHandler client,
            int playerTurnNumber,
            Player player,
            int opponentTurnNumber,
            Player opponent) {
        Message playerStatsMsg = createPlayerStatsMessage(playerTurnNumber, player);
        Message opponentStatsMsg = createPlayerStatsMessage(opponentTurnNumber, opponent);

        client.sendMessage(playerStatsMsg);
        client.sendMessage(opponentStatsMsg);
    }

    private void sendFullDeck(ClientHandler client) {
        Message fullDeckMsg = new Message(MessageType.FULL_CARD_DECK);
        // I truly do not understand why making a copy is scenery, but for the rematch, it is.
        Deck<Card> copyOfAllCards = new Deck<>();
        copyOfAllCards.addAll(Card.getFullCardCollection());
        fullDeckMsg.setData(DataKey.COLLECTION, copyOfAllCards);
        client.sendMessage(fullDeckMsg);
    }

    private void dealCardsToPlayerAndOpponent(
            ClientHandler client,
            int playerTurnNumber,
            Player player,
            int opponentTurnNumber,
            Player opponent) {
        dealHandCards(client, playerTurnNumber, player);
        dealHandCards(client, opponentTurnNumber, opponent);
        dealDeckCards(client, playerTurnNumber, player);
        dealDeckCards(client, opponentTurnNumber, opponent);
    }

    public void dealDeckCards(ClientHandler client, int targetPlayerTurnNumber, Player player) {
        for (Card card : player.getPlayArea().getPlayerCards().getDeckCards()) {
            Message removeCardMsg =
                    createRemoveCardMessage(targetPlayerTurnNumber, DeckType.DISCARD, card.getId());
            Message addCardMsg =
                    createAddCardMessage(targetPlayerTurnNumber, DeckType.DECK, card.getId());

            client.sendMessage(removeCardMsg);
            client.sendMessage(addCardMsg);
        }
    }

    public void dealHandCards(ClientHandler client, int targetPlayerTurnNumber, Player player) {
        for (Card card : player.getPlayArea().getPlayerCards().getHandCards()) {
            Message removeCardMsg =
                    createRemoveCardMessage(targetPlayerTurnNumber, DeckType.DECK, card.getId());
            Message addCardMsg =
                    createAddCardMessage(targetPlayerTurnNumber, DeckType.HAND, card.getId());

            client.sendMessage(removeCardMsg);
            client.sendMessage(addCardMsg);
        }
    }

    public void dealHandCardsBasedOnTurnNumber(int targetPlayerTurnNumber, Player player) {
        for (Card card : player.getPlayArea().getPlayerCards().getHandCards()) {
            Message removeCardMsg =
                    createRemoveCardMessage(targetPlayerTurnNumber, DeckType.DECK, card.getId());
            Message addCardMsg =
                    createAddCardMessage(targetPlayerTurnNumber, DeckType.HAND, card.getId());

            try {
                sendMessageToAllClients(removeCardMsg);
                sendMessageToAllClients(addCardMsg);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void discardCardsAfterTurn(int targetPlayerTurnNumber, Player player) {
        for (Card card : player.getPlayArea().getPlayerCards().getHandCards()) {
            Message removeCardMsg =
                    createRemoveCardMessage(targetPlayerTurnNumber, DeckType.HAND, card.getId());
            Message addCardMsg =
                    createAddCardMessage(targetPlayerTurnNumber, DeckType.DISCARD, card.getId());

            try {
                sendMessageToAllClients(removeCardMsg);
                sendMessageToAllClients(addCardMsg);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        for (Card card : player.getPlayArea().getPlayedCards()) {
            Message removeCardMsg =
                    createRemoveCardMessage(targetPlayerTurnNumber, DeckType.PLAYED, card.getId());
            Message addCardMsg =
                    createAddCardMessage(targetPlayerTurnNumber, DeckType.DISCARD, card.getId());

            try {
                sendMessageToAllClients(removeCardMsg);
                sendMessageToAllClients(addCardMsg);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void resetChampionsAfterTurn(int targetPlayerTurnNumber, Player player) {
        for (Card card : player.getPlayArea().getPlayedChampions()) {
            if (((Champion) card).isExpended()) {
                Message resetChampionMsg =
                        createResetChampionMessage(targetPlayerTurnNumber, card.getId());
                try {
                    sendMessageToAllClients(resetChampionMsg);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void sendRestockDeckFromDiscard(
            int targetPlayerTurnNumber, Deck<Card> restockedFromDiscarded) {
        for (Card card : restockedFromDiscarded) {
            Message removeCardMsg =
                    createRemoveCardMessage(targetPlayerTurnNumber, DeckType.DISCARD, card.getId());
            Message addCardMsg =
                    createAddCardMessage(targetPlayerTurnNumber, DeckType.DECK, card.getId());

            try {
                sendMessageToAllClients(removeCardMsg);
                sendMessageToAllClients(addCardMsg);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void sendTurnNotificationToAllClients(int playerTurnNumber) {
        Message turnNotificationMsg = createTurnNotificationMessage(playerTurnNumber);
        try {
            sendMessageToAllClients(turnNotificationMsg);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void dealMarketCardsToPurchaseArea(ClientHandler client) {
        for (Card card : Market.getInstance().getForPurchase()) {
            Message removeCardMsg = createRemoveMarketCardMessage(DeckType.MARKET, card.getId());
            Message addCardMsg = createAddMarketCardMessage(DeckType.FOR_PURCHASE, card.getId());

            client.sendMessage(removeCardMsg);
            client.sendMessage(addCardMsg);
        }
    }

    public void dealMarketCardsToPurchaseAreaToAll() {
        for (Card card : Market.getInstance().getNewToPurchase()) {
            Message removeCardMsg = createRemoveMarketCardMessage(DeckType.MARKET, card.getId());
            Message addCardMsg = createAddMarketCardMessage(DeckType.FOR_PURCHASE, card.getId());

            try {
                sendMessageToAllClients(removeCardMsg);
                sendMessageToAllClients(addCardMsg);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        Market.getInstance().setNewToPurchase(new Deck<>());
    }

    public static ServerThread getInstance() {
        if (instance == null) {
            throw new IllegalStateException("ServerThread not initialized!");
        }
        return instance;
    }

    public void sendCheatStatusToAll(boolean isCheating) {
        Message cheatStatusMsg = createCheatStatusMessage(isCheating);
        try {
            sendMessageToAllClients(cheatStatusMsg);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private int numOfRematchRequests;

    public void sendRematchToAll() throws IOException {
        numOfRematchRequests++;
        Log.d(TAG, "Processing REMATCH_REQUEST, numOfRequests= " + numOfRematchRequests);
        if (numOfRematchRequests == connections.size()) {
            Log.d(TAG, "Requirements for rematch met, sending command.");
            numOfRematchRequests = 0;
            Card.resetIdsAndCollection();
            Message rematch = new Message(MessageType.REMATCH);
            createGame();
            sendMessageToAllClients(rematch);
        }
    }

    public GameSession getGameSession() {
        return gameSession;
    }

    public void sendNameChangeToAll(String name, int targetPlayer) throws IOException {
        gameSession.getPlayers().get(targetPlayer).setPlayerName(name);
        Message updatedName =
                Communication.createPlayerStatsMessage(
                        targetPlayer, gameSession.getPlayers().get(targetPlayer));
        sendMessageToAllClients(updatedName);
    }
}
