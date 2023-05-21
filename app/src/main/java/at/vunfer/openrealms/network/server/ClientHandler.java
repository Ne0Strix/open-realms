/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.network.server;

import android.util.Log;
import at.vunfer.openrealms.network.Communication;
import at.vunfer.openrealms.network.DataKey;
import at.vunfer.openrealms.network.Message;
import at.vunfer.openrealms.network.MessageType;
import at.vunfer.openrealms.network.client.MessageHandler;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientHandler {

    private final String TAG = "ClientHandler";
    private Socket socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private MessageHandler messageHandler;
    private ClientHandler clientHandler;
    private Communication comm;

    public ClientHandler(Socket clientSocket) {
        this.socket = socket;
        this.messageHandler = messageHandler;
        try {
            socket = clientSocket;
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            inputStream = new ObjectInputStream(socket.getInputStream());
            comm = new Communication(inputStream, outputStream, new ServerMessageHandler());
            Log.i(TAG, "ClientHandler successfully attached to a new client.");
        } catch (IOException ex) {
            Log.e("Error", "IO Exception!");
        }
    }

    public void disconnectClient() throws IOException {
        socket.close();
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

    public void handleClientMessage(Message message) {
        if (message.getType() == MessageType.BUY_CARD) {
            try {
                buyCard(message);
                // Optional: Send confirmation message to the client
                Message confirmationMessage = new Message(MessageType.BUY_CARD_CONFIRMATION);
                // Set required data in the confirmation message
                // ...
                sendMessage(confirmationMessage);
            } catch (IllegalArgumentException e) {
                // Error handling for invalid card purchases
                e.printStackTrace();
                // Optional: Send error message to the client
                Message errorMessage = new Message(MessageType.BUY_CARD_ERROR);
                // Set error message in the message
                errorMessage.setData(DataKey.ERROR_MESSAGE, e.getMessage());
                sendMessage(errorMessage);
            }
        }
    }

    private void buyCard(Message message) throws IllegalArgumentException {
        // Logic for purchasing a card based on the message data

    }

    public void sendMessage(Message msg) {
        try {
            outputStream.writeObject(msg);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        comm.sendMessage(msg);
        Log.i(TAG, "Sent message: " + msg.getType());
    }
}
