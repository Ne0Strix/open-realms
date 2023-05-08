/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms;

import java.util.List;

import at.vunfer.openrealms.model.Card;
import at.vunfer.openrealms.network.Message;

public interface UIUpdateListener {
    void updateUI(Message message);
    void addCardToPlayArea(Card card);
    void removeCardFromPlayArea(Card card);
    String displayOptions(List<String> options);
    //void updatePlayerStats(PlayerStats stats);
}
