/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.network.client;

import android.util.Log;
import at.vunfer.openrealms.UIUpdateListener;
import at.vunfer.openrealms.network.Communication;
import at.vunfer.openrealms.network.Message;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ClientConnector extends Thread {
    private static final String TAG = "ClientConnector";
    private final UIUpdateListener uiUpdater;
    private Socket socket;
    private InetSocketAddress targetServer;
    private Communication comm;
    private String ipAddress;
    private int port;

    public ClientConnector(UIUpdateListener uiUpdater) {
        this.socket = new Socket();
        this.uiUpdater = uiUpdater;
    }

    private ObjectInputStream relayIn;
    private ObjectOutputStream relayOut;

    public ClientConnector(
            UIUpdateListener uiUpdater,
            Socket socket,
            ObjectInputStream inStream,
            ObjectOutputStream outStream) {
        this.socket = socket;
        this.uiUpdater = uiUpdater;
        this.relayIn = inStream;
        this.relayOut = outStream;
    }

    @Override
    public void run() {
        if (!socket.isConnected()) {
            // Local connection
            try {
                targetServer = new InetSocketAddress(ipAddress, port);
                socket.connect(targetServer);
                ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
                Log.i("Info", "Client was connected to " + targetServer.toString());
                uiUpdater.clientCallback(true);

                comm =
                        new Communication(
                                inputStream, outputStream, new ClientMessageHandler(uiUpdater));

            } catch (IOException e) {
                uiUpdater.clientCallback(false);
            }
        } else {
            // Relay Connection
            comm = new Communication(relayIn, relayOut, new ClientMessageHandler(uiUpdater));
            Log.i("Info", "Client was started with relay ");
        }
    }

    public void setConnectionTarget(String ipAddr, int port) {
        this.ipAddress = ipAddr;
        this.port = port;
    }

    public void sendMessage(Message msg) throws IOException {
        comm.sendMessage(msg);
        Log.i(TAG, "Sent message: " + msg.getType());
    }

    public InetSocketAddress getConnectionTarget() {
        return targetServer;
    }

    public Communication getCommunication() {
        return comm;
    }

    public void setSocket(Socket socket) {
        // This was added to enable easier Mocking of private fields
        this.socket = socket;
    }

    public void close() {
        try {
            comm.closeCommunication();
        } catch (IOException e) {
            Log.e(TAG, e.getLocalizedMessage());
        }
    }
}
