/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.network.server;

import android.util.Log;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class ServerThread extends Thread {
    private int port;

    private ServerSocket serverSocket;
    private ArrayList<ClientHandler> connections = new ArrayList<>();

    public ServerThread(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(this.port); // Create an unbound server socket
            Log.i("Info", "The server was started! Port number: " + port);
            connections.add(new ClientHandler(serverSocket.accept()));
        } catch (IOException ex) {
            System.out.println("IO Exception on Server!");
            ex.printStackTrace();
        }
    }

    public void stopClient() throws IOException {
        serverSocket.close();
    }

    public ClientHandler getHandler() {
        return connections.get(0);
    }
}
