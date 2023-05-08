/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.network;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import at.vunfer.openrealms.model.Card;

public class Message implements Serializable {
    private MessageType type;
    private final Map<DataKey, Object> data;
    private Card card;
    private List<String> options;
    private String selectedOption;
    //private PlayerStats playerStats;

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
    public Card getCard() {
        return card;
    }
    public void setCard(Card card) {
        this.card = card;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public String getSelectedOption() {
        return selectedOption;
    }

    public void setSelectedOption(String selectedOption) {
        this.selectedOption = selectedOption;
    }

    /*public PlayerStats getPlayerStats() {
        return playerStats;
    }

    public void setPlayerStats(PlayerStats playerStats) {
        this.playerStats = playerStats;
    }*/
}
