/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.network.client;

import android.util.Log;
import at.vunfer.openrealms.UIUpdateListener;
import at.vunfer.openrealms.network.IHandleMessage;
import at.vunfer.openrealms.network.Message;

public class MessageHandler implements IHandleMessage {
    private static final String TAG = "ClientMessageHandler";
    private final UIUpdateListener uiUpdater;

    public MessageHandler(UIUpdateListener uiUpdater) {
        this.uiUpdater = uiUpdater;
    }

    public void handleMessage(Message message) {
        switch (message.getType()) {
            case ADD_CARD:
                // TODO instructions for UI
            case REMOVE_CARD:
                // TODO instructions for UI
            case CHOOSE_OPTION:
                // TODO instructions for UI
            case UPDATE_PLAYER_STATS:
                // TODO instructions for UI
                Log.i(TAG, "Handled message of type " + message.getType());
                uiUpdater.updateUI(message);
                break;
            default:
                Log.i(TAG, "Received message of unknown type.");
        }
    }
}
