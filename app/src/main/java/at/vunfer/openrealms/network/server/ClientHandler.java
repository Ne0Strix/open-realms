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

    public ClientHandler() {
        // TODO make connection to client

        new Thread(this::listenForMessages).start();
    }

    private void listenForMessages() {
        try {
            while (true) {
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
}
