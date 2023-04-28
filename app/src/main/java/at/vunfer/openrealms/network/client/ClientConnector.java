/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.network.client;

import android.util.Log;
import at.vunfer.openrealms.network.Message;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientConnector extends Thread {
    private int port;
    private String hostname;
    private Socket socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private MessageHandler messageHandler;

    public ClientConnector(int port, String hostname) {
        // TODO make connection to server
        this.port = port;
        this.hostname = hostname;
    }

    public void run() {
        try {
            socket = new Socket(hostname, port);
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            inputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException ex) {
            Log.e("Error", "IO Exception!");
        }

        // starting the thread that is waiting for the messages from server
        new Thread(this::listenForMessages).start();
    }

    private void listenForMessages() {
        while (true) {
            try {
                Message msg = (Message) inputStream.readObject();
                messageHandler.handleMessage(msg);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    public void sendStringToServer() {
        SenderThread sender = new SenderThread(outputStream);
        sender.start();
    }
}
