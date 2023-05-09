/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.network.server;

import java.util.Map;

import at.vunfer.openrealms.network.DataKey;
import at.vunfer.openrealms.network.Message;

public class MessageHandler {
    public boolean handleMessage(Message message) {
        switch (message.getType()) {
            case TOUCHED:
                System.out.println("Received TOUCHED message.");
                return true;
            case CHOICE:
                Object rawData = message.getData(DataKey.OPTIONS);
                if (rawData instanceof Map) {
                    Map<String, Object> data = (Map<String, Object>) rawData;
                    System.out.println("Received CHOICE message with option: " + data.get("option"));
                }
                return true;
            case END_TURN:
                System.out.println("Received END_TURN message. Ending turn...");
                return true;
            default:
                System.out.println("Received unknown message type: " + message.getType());
                return false;
        }
    }
}
