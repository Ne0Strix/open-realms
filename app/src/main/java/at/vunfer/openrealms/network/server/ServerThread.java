/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.network.server;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.Log;
import at.vunfer.openrealms.model.GameSession;
import at.vunfer.openrealms.model.Player;
import at.vunfer.openrealms.model.PlayerFactory;
import at.vunfer.openrealms.network.Message;

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
        Player player1 = PlayerFactory.createPlayer("Player 1");
        Player player2 = PlayerFactory.createPlayer("Player 2");
        List<Player> players = List.of(player1, player2);
        gameSession = new GameSession(players, player1);
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

    public void sendMsgToAll(Message msg) throws IOException {
        for (ClientHandler client : connections) {
            client.sendMessage(msg);
        }
    }
}
