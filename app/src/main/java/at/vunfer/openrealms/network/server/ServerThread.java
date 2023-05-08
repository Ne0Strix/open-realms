/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.network.server;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.Log;
import at.vunfer.openrealms.model.Card;
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
        setupClients();
    }

    private void setupClients() {
        for (ClientHandler client : connections) {
            int playerTurnNumber = getTurnNumber(client);
            Player player = gameSession.getPlayers().get(playerTurnNumber);

            Player opponent = gameSession.getOpponent(player);
            int opponentTurnNumber = gameSession.getPlayers().indexOf(opponent);

            // Message with player-stats
            Message playerStatsMsg = new Message(MessageType.UPDATE_PLAYER_STATS);
            playerStatsMsg.setData(DataKey.TARGET_PLAYER, playerTurnNumber);
            playerStatsMsg.setData(
                    DataKey.PLAYER_STATS,
                    new PlayerStats(
                            player.getPlayerName(),
                            player.getPlayArea().getHealth(),
                            player.getPlayArea().getTurnDamage(),
                            player.getPlayArea().getTurnHealing(),
                            player.getPlayArea().getTurnCoins()));

            // Message with opponent-stats
            Message opponentStatsMsg = new Message(MessageType.UPDATE_PLAYER_STATS);
            opponentStatsMsg.setData(DataKey.TARGET_PLAYER, opponentTurnNumber);
            opponentStatsMsg.setData(
                    DataKey.PLAYER_STATS,
                    new PlayerStats(
                            opponent.getPlayerName(),
                            opponent.getPlayArea().getHealth(),
                            opponent.getPlayArea().getTurnDamage(),
                            opponent.getPlayArea().getTurnHealing(),
                            opponent.getPlayArea().getTurnCoins()));

            // Message with fullCardCollection
            Message fullDeckMsg = new Message(MessageType.FULL_CARD_DECK);
            fullDeckMsg.setData(DataKey.DECK, Card.getFullCardCollection());

            // send the messages
            try {
                client.sendMessage(playerStatsMsg);
                client.sendMessage(opponentStatsMsg);
                client.sendMessage(fullDeckMsg);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // deal hand cards to player
            for (Card card : player.getPlayArea().getPlayerCards().getHandCards()) {
                // add card to player-hand
                Message addCardMsg = new Message(MessageType.ADD_CARD);
                addCardMsg.setData(DataKey.TARGET_PLAYER, playerTurnNumber);
                addCardMsg.setData(DataKey.DECK, DeckType.HAND);
                addCardMsg.setData(DataKey.CARD_ID, card.getId());

                // remove card from player-deck
                Message removeCardMsg = new Message(MessageType.REMOVE_CARD);
                removeCardMsg.setData(DataKey.TARGET_PLAYER, playerTurnNumber);
                removeCardMsg.setData(DataKey.DECK, DeckType.DECK);
                removeCardMsg.setData(DataKey.CARD_ID, card.getId());
                try {
                    client.sendMessage(addCardMsg);
                    client.sendMessage(removeCardMsg);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            // deal hand cards to opponent
            for (Card card : opponent.getPlayArea().getPlayerCards().getHandCards()) {
                // add card to opponent-hand
                Message addCardMsg = new Message(MessageType.ADD_CARD);
                addCardMsg.setData(DataKey.TARGET_PLAYER, opponentTurnNumber);
                addCardMsg.setData(DataKey.DECK, DeckType.HAND);
                addCardMsg.setData(DataKey.CARD_ID, card.getId());

                // remove card from opponent-deck
                Message removeCardMsg = new Message(MessageType.REMOVE_CARD);
                removeCardMsg.setData(DataKey.TARGET_PLAYER, opponentTurnNumber);
                removeCardMsg.setData(DataKey.DECK, DeckType.DECK);
                removeCardMsg.setData(DataKey.CARD_ID, card.getId());
                try {
                    client.sendMessage(addCardMsg);
                    client.sendMessage(removeCardMsg);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            // deal market cards to purchaseArea
            for (Card card : Market.getInstance().getForPurchase()) {
                // add card to purchaseArea
                Message addCardMsg = new Message(MessageType.ADD_CARD);
                addCardMsg.setData(DataKey.DECK, DeckType.FOR_PURCHASE);
                addCardMsg.setData(DataKey.CARD_ID, card.getId());

                // remove card from marketDeck
                Message removeCardMsg = new Message(MessageType.REMOVE_CARD);
                removeCardMsg.setData(DataKey.DECK, DeckType.MARKET);
                removeCardMsg.setData(DataKey.CARD_ID, card.getId());

                try {
                    client.sendMessage(addCardMsg);
                    client.sendMessage(removeCardMsg);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
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
