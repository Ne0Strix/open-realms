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
    public void handleSpecificMessage(Message message) {
        switch (message.getType()) {
            case BUY_CARD:
                // Logic for handling the buyCard message
                break;
            // More message types can be processed here
            default:
                // Error handling for unknown message types
                System.out.println("Received unknown message type: " + message.getType());
                break;
        }
    }
}
