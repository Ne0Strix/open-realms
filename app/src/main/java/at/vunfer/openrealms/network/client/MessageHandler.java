/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.network.client;

import at.vunfer.openrealms.UIUpdateListener;
import at.vunfer.openrealms.model.Card;
import at.vunfer.openrealms.network.DataKey;
import at.vunfer.openrealms.network.Message;
import java.util.List;

public class MessageHandler {
    public UIUpdateListener uiUpdateListener;

    public boolean handleMessage(Message message) {
        switch (message.getType()) {
            case ADD_CARD:
                handleAddCardMessage(message);
                break;
            case REMOVE_CARD:
                handleRemoveCardMessage(message);
                break;
            case CHOOSE_OPTION:
                handleChooseOptionMessage(message);
                break;
            case UPDATE_PLAYER_STATS:
                handleUpdatePlayerStatsMessage(message);
                break;
            default:
                return false;
        }
        return true;
    }

    private void handleAddCardMessage(Message message) {
        Card card = (Card) message.getData(DataKey.CARD_ID);
        uiUpdateListener.addCardToPlayArea(card);
    }

    private void handleRemoveCardMessage(Message message) {
        Card card = (Card) message.getData(DataKey.CARD_ID);
        uiUpdateListener.removeCardFromPlayArea(card);
    }

    private void handleChooseOptionMessage(Message message) {
        List<String> options = (List<String>) message.getData(DataKey.OPTIONS);
        String selectedOption = uiUpdateListener.displayOptions(options);
    }

    private void handleUpdatePlayerStatsMessage(Message message) {
        // PlayerStats stats = (PlayerStats) message.getData();
        // uiUpdateListener.updatePlayerStats(stats); - not implemented
    }

    public void setListener(UIUpdateListener listener) {
        this.uiUpdateListener = listener;
    }
}
