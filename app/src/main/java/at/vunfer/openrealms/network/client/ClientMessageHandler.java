/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.network.client;

import android.util.Log;

import java.util.List;

import at.vunfer.openrealms.UIUpdateListener;
import at.vunfer.openrealms.network.IHandleMessage;
import at.vunfer.openrealms.network.Message;

public class ClientMessageHandler implements IHandleMessage {
    private static final String TAG = "ClientMessageHandler";
    private final UIUpdateListener uiUpdater;

    public ClientMessageHandler(UIUpdateListener uiUpdater) {
        this.uiUpdater = uiUpdater;
    }

    public void handleMessage(Message message) {
        switch (message.getType()) {
            case ADD_CARD:
                uiUpdater.addCardToPlayArea(message.getCard());
                Log.i(TAG, "Added card to hand or play area");
                uiUpdater.updateUI(message);
                break;
            case REMOVE_CARD:
                uiUpdater.removeCardFromPlayArea(message.getCard());
                Log.i(TAG, "Removed card from hand or play area");
                uiUpdater.updateUI(message);
                break;
            case CHOOSE_OPTION:
                List<String> options = message.getOptions();
                String selectedOption = uiUpdater.displayOptions(options);
                message.setSelectedOption(selectedOption);
                Log.i(TAG, "Displayed options and waited for player's choice");
                uiUpdater.updateUI(message);
                break;
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
