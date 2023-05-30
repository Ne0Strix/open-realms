/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.network.client;

import at.vunfer.openrealms.network.Message;

public class MessageHandler {

    public boolean handleMessage(Message message) {
        switch (message.getType()) {
            case ADD_CARD:
                // TODO instructions for UI
            case REMOVE_CARD:
                // TODO instructions for UI
            case CHOOSE_OPTION:
                // TODO instructions for UI
            case UPDATE_PLAYER_STATS:
                // TODO instructions for UI
            case BUY_CARD:
                // TODO: instructions for UI
            case CHEAT:
                return true;
            default:
                return false;
        }
    }
}
