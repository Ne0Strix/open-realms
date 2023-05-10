/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.network.server;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.Log;
import at.vunfer.openrealms.model.Card;
import at.vunfer.openrealms.model.Deck;
import at.vunfer.openrealms.model.DeckGenerator;
import at.vunfer.openrealms.model.GameSession;
import at.vunfer.openrealms.model.Market;
import at.vunfer.openrealms.model.Player;
import at.vunfer.openrealms.model.PlayerFactory;
import at.vunfer.openrealms.network.DataKey;
import at.vunfer.openrealms.network.DeckType;
import at.vunfer.openrealms.network.Message;
import at.vunfer.openrealms.network.MessageType;
import at.vunfer.openrealms.network.PlayerStats;
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
    private int port;
    private String ipAddr = "empty";
    private static ServerThread instance;
    private GameSession gameSession;

    private ServerSocket serverSocket;
    private ArrayList<ClientHandler> connections = new ArrayList<>();

    public ServerThread(Context context, int port) {
        this.context = context;
        this.port = port;
        instance = this;
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(this.port);
            ipAddr = getIPAddress();
            Log.i(TAG, "Server reachable under: " + ipAddr + ":" + serverSocket.getLocalPort());
            connections.add(new ClientHandler(serverSocket.accept()));
            Log.i(TAG, "Local client connected!");
            connections.add(new ClientHandler(serverSocket.accept()));
            Log.i(TAG, "Remote client connected!");
        } catch (IOException ex) {
            Log.e(TAG, "IO Exception on Server!");
            ex.printStackTrace();
        }
        createGame();
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
            sendTurnNotification(client, playerTurnNumber);
        }
    }

    private void createGame() {
        Player player1 = PlayerFactory.createPlayer("Vünfer");
        Player player2 = PlayerFactory.createPlayer("Hr.Macho");
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

        try {
            client.sendMessage(playerStatsMsg);
            client.sendMessage(opponentStatsMsg);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Message createPlayerStatsMessage(int targetPlayerTurnNumber, Player player) {
        Message playerStatsMsg = new Message(MessageType.UPDATE_PLAYER_STATS);
        playerStatsMsg.setData(DataKey.TARGET_PLAYER, targetPlayerTurnNumber);
        playerStatsMsg.setData(
                DataKey.PLAYER_STATS,
                new PlayerStats(
                        player.getPlayerName(),
                        player.getPlayArea().getHealth(),
                        player.getPlayArea().getTurnDamage(),
                        player.getPlayArea().getTurnHealing(),
                        player.getPlayArea().getTurnCoins()));
        return playerStatsMsg;
    }

    private void sendFullDeck(ClientHandler client) {
        Message fullDeckMsg = new Message(MessageType.FULL_CARD_DECK);
        fullDeckMsg.setData(DataKey.COLLECTION, Card.getFullCardCollection());
        try {
            client.sendMessage(fullDeckMsg);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

            try {
                client.sendMessage(removeCardMsg);
                client.sendMessage(addCardMsg);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void dealHandCards(ClientHandler client, int targetPlayerTurnNumber, Player player) {
        for (Card card : player.getPlayArea().getPlayerCards().getHandCards()) {
            Message removeCardMsg =
                    createRemoveCardMessage(targetPlayerTurnNumber, DeckType.DECK, card.getId());
            Message addCardMsg =
                    createAddCardMessage(targetPlayerTurnNumber, DeckType.HAND, card.getId());

            try {
                client.sendMessage(removeCardMsg);
                client.sendMessage(addCardMsg);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
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

    public void sendRestockDeckFromDiscard(
            int targetPlayerTurnNumber, Player player, Deck<Card> restockedFromDiscarded) {
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

    public Message createAddCardMessage(int targetPlayerTurnNumber, DeckType deckType, int cardId) {
        Message addCardMsg = new Message(MessageType.ADD_CARD);
        addCardMsg.setData(DataKey.TARGET_PLAYER, targetPlayerTurnNumber);
        addCardMsg.setData(DataKey.DECK, deckType);
        addCardMsg.setData(DataKey.CARD_ID, cardId);
        return addCardMsg;
    }

    public Message createRemoveCardMessage(
            int targetPlayerTurnNumber, DeckType deckType, int cardId) {
        Message removeCardMsg = new Message(MessageType.REMOVE_CARD);
        removeCardMsg.setData(DataKey.TARGET_PLAYER, targetPlayerTurnNumber);
        removeCardMsg.setData(DataKey.DECK, deckType);
        removeCardMsg.setData(DataKey.CARD_ID, cardId);
        return removeCardMsg;
    }

    public Message createTurnNotificationMessage(int targetPlayerTurnNumber) {
        Message turnNotificationMsg = new Message(MessageType.TURN_NOTIFICATION);
        turnNotificationMsg.setData(DataKey.TARGET_PLAYER, targetPlayerTurnNumber);
        return turnNotificationMsg;
    }

    private void dealMarketCardsToPurchaseArea(ClientHandler client) {
        for (Card card : Market.getInstance().getForPurchase()) {
            Message removeCardMsg = createRemoveMarketCardMessage(DeckType.MARKET, card.getId());
            Message addCardMsg = createAddMarketCardMessage(DeckType.FOR_PURCHASE, card.getId());

            try {
                client.sendMessage(removeCardMsg);
                client.sendMessage(addCardMsg);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
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
        Market.getInstance().setGetNewToPurchase(new Deck<>());
    }

    public Message createAddMarketCardMessage(DeckType deckType, int cardId) {
        Message addCardMsg = new Message(MessageType.ADD_CARD);
        addCardMsg.setData(DataKey.DECK, deckType);
        addCardMsg.setData(DataKey.CARD_ID, cardId);
        return addCardMsg;
    }

    public Message createRemoveMarketCardMessage(DeckType deckType, int cardId) {
        Message removeCardMsg = new Message(MessageType.REMOVE_CARD);
        removeCardMsg.setData(DataKey.DECK, deckType);
        removeCardMsg.setData(DataKey.CARD_ID, cardId);
        return removeCardMsg;
    }

    private void sendTurnNotification(ClientHandler client, int playerTurnNumber) {
        Message yourTurnMsg = new Message(MessageType.TURN_NOTIFICATION);
        if (playerTurnNumber == 0) {
            yourTurnMsg.setData(DataKey.YOUR_TURN, true);
        } else {
            yourTurnMsg.setData(DataKey.YOUR_TURN, false);
        }

        try {
            client.sendMessage(yourTurnMsg);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static ServerThread getInstance() {
        if (instance == null) {
            throw new IllegalStateException("ServerThread not initialized!");
        }
        return instance;
    }

    public GameSession getGameSession() {
        return gameSession;
    }
}
