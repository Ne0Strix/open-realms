/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.network.server;

import at.vunfer.openrealms.network.Message;

public class MessageHandler {
    public boolean handleMessage(Message message) {
        switch (message.getType()) {
            case TOUCHED:
                // TODO instructions for backend
            case CHOICE:
                // TODO instructions for backend
            case END_TURN:
                // TODO instructions for backend
            default:
                return false;
        }
    }
}
