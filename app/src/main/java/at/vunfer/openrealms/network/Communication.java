/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.network;

import android.util.Log;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Communication {

    private final String TAG = "Communication";
    private final ObjectInputStream input;
    private final ObjectOutputStream output;
    ExecutorService executor = Executors.newFixedThreadPool(2);
    IHandleMessage messageHandler;

    public Communication(
            ObjectInputStream input, ObjectOutputStream output, IHandleMessage messageHandler) {
        this.input = input;
        this.output = output;
        this.messageHandler = messageHandler;
        executor.submit(this::listenForMessages);
    }

    public void sendMessage(Message msg) throws IOException {
        executor.submit(
                () -> {
                    try {
                        output.writeObject(msg);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
        Log.i(TAG, "Sent: " + msg.getType());
    }

    private void listenForMessages() {
        while (true) {
            try {
                Message msg = (Message) input.readObject();
                Log.i(TAG, "Received: " + msg.getType());
                messageHandler.handleMessage(msg);
                if (msg.getType() == MessageType.TOUCHED) {
                    sendMessage(new Message(MessageType.ADD_CARD));
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                break;
            }
        }
    }
}
