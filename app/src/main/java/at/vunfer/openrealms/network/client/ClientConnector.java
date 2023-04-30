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
    private final String TAG = "ClientConnector";
    private final UIUpdateListener uiUpdater;
    private Socket socket;
    private InetSocketAddress targetServer;
    private Communication comm;

    public ClientConnector(UIUpdateListener uiUpdater) {
        this.socket = new Socket();
        this.uiUpdater = uiUpdater;
    }

    @Override
    public void run() {
        try {
            socket.connect(targetServer);
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
            Log.i("Info", "Client was connected to " + targetServer.toString());

            comm = new Communication(inputStream, outputStream, new MessageHandler(uiUpdater));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setConnectionTarget(String ipAddr, int port) {
        targetServer = new InetSocketAddress(ipAddr, port);
    }

    public void sendMessage(Message msg) throws IOException {
        comm.sendMessage(msg);
        Log.i(TAG, "Sent message: " + msg.getType());
    }
}
