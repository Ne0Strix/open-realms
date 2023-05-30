/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.network.client;

import android.util.Log;
import at.vunfer.openrealms.UIUpdateListener;
import at.vunfer.openrealms.network.Communication;
import at.vunfer.openrealms.network.Message;
import at.vunfer.openrealms.network.MessageType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ClientConnector extends Thread {
    private static final String TAG = "ClientConnector";
    private final UIUpdateListener uiUpdater;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private MessageHandler messageHandler;
    private InetSocketAddress targetServer;
    private Communication comm;

    private Socket socket = new Socket();

    public ClientConnector(UIUpdateListener uiUpdater) {
        this.uiUpdater = uiUpdater;
    }

    public void connectAndSendBuyCardMessage(Message message) {
        try {
            // Start listening for messages
            new Thread(this::listenForMessages).start();

            // send buyCard message
            outputStream.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
            // Error handling for send errors
        }
    }

    public void setMessageHandler(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    private void listenForMessages() {
        try {
            while (!socket.isClosed()) {
                Message msg = (Message) inputStream.readObject();
                messageHandler.handleMessage(msg);
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) inputStream.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            try {
                if (outputStream != null) outputStream.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            try {
                if (socket != null) socket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        try {
            // Set the socket connection target
            socket.connect(targetServer);
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
            Log.i("Info", "Client was connected to " + targetServer.toString());

            comm =
                    new Communication(
                            inputStream, outputStream, new ClientMessageHandler(uiUpdater));
        } catch (IOException e) {
            throw new RuntimeException("Unable to create client connection.", e);
        }
    }

    public void setConnectionTarget(String ipAddr, int port) {
        targetServer = new InetSocketAddress(ipAddr, port);
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

    public void sendCheatMessage() {
        if (comm != null) {
            Message cheatMessage = new Message(MessageType.CHEAT);
            comm.sendMessage(cheatMessage);
        }
    }
}
