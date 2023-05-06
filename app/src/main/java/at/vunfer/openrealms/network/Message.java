/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.network;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Message implements Serializable {
    private MessageType type;
    private Map<DataKey, Object> data;

    public Message(MessageType type) {
        this.type = type;
        this.data = new HashMap<>();
    }

    public MessageType getType() {
        return type;
    }

    public void setData(DataKey key, String value) throws IllegalArgumentException {
        if (validateData(key, value)) {
            data.put(key, value);
        } else {
            throw new IllegalArgumentException("Invalid data type for key " + key);
        }
    }

    public void getData(String key) {
        data.get(key);
    }

    private boolean validateData(DataKey key, Object value) {
        switch (key) {
            case CARD_ID:
                return value instanceof Integer;
            case DECK:
            case CHOICE:
            case PLAYER_STATS:
            case OPTIONS:
                return value instanceof String;
            default:
                return false;
        }
    }
}
