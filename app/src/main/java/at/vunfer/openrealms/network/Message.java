/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.network;

import at.vunfer.openrealms.model.Deck;
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

    public void setData(DataKey key, Object value) throws IllegalArgumentException {
        if (validateData(key, value)) {
            data.put(key, value);
        } else {
            throw new IllegalArgumentException("Invalid data type for key " + key);
        }
    }

    public Object getData(DataKey key) {
        return data.get(key);
    }


    /**
     * Validates data entered into the message's key-value pairs.
     * @param key The type of data in the field.
     * @param value The value of the field.
     * @return True if a given value is valid for provided DataKey.
     */
    private boolean validateData(DataKey key, Object value) {
        switch (key) {
            case TARGET_PLAYER:
            case CARD_ID:
                return value instanceof Integer;
            case DECK:
                return value instanceof DeckType;
            case CHOICE:
            case OPTIONS:
                return value instanceof String;
            case PLAYER_STATS:
                return value instanceof PlayerStats;
            case YOUR_TURN:
                return value instanceof Boolean;
            case COLLECTION:
                return value instanceof Deck;
            default:
                return false;
        }
    }
}
