/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.network.server;

import at.vunfer.openrealms.network.Message;
import at.vunfer.openrealms.network.client.MessageHandler;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientHandler {

    private Socket socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private MessageHandler messageHandler;
    private ClientHandler clientHandler;

    public ClientHandler() {
        this.socket = socket;
        this.messageHandler = messageHandler;

        try {
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            inputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        new Thread(this::listenForMessages).start();
    }

    private void listenForMessages() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Message msg = (Message) inputStream.readObject();
                messageHandler.handleMessage(msg);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                break;
            }
        }
    }
}
