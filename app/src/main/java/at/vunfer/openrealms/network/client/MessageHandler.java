/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.network.client;

import at.vunfer.openrealms.network.Message;

public class MessageHandler {

    public boolean handleMessage(Message message) {
        switch (message.getType()) {
            case ADD_CARD:
                handleAddCardMessage(message);
            case REMOVE_CARD:
                handleRemoveCardMessage(message);
            case CHOOSE_OPTION:
                handleChooseOptionMessage(message);            case UPDATE_PLAYER_STATS:
                // TODO instructions for UI
            default:
                return false;
        }
    }
    private void handleAddCardMessage(Message message) {
        // TODO instructions for UI
    }
    private void handleRemoveCardMessage(Message message) {
        // TODO instructions for UI
    }
    private void handleChooseOptionMessage(Message message) {
        // TODO instructions for UI
    }
}
