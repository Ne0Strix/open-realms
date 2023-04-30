/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.network.server;

import android.util.Log;
import at.vunfer.openrealms.network.IHandleMessage;
import at.vunfer.openrealms.network.Message;

public class MessageHandler implements IHandleMessage {
    private final String TAG = "ServerMessageHandler";

    public void handleMessage(Message message) {
        switch (message.getType()) {
            case TOUCHED:
                // TODO instructions for backend
            case CHOICE:
                // TODO instructions for backend
            case END_TURN:
                // TODO instructions for backend
                Log.i(TAG, "Handled message of type " + message.getType());
                break;
            default:
                Log.i(TAG, "Received message of unknown type.");
        }
    }
}
