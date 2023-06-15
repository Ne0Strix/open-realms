/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.network.server.card_actions;

import at.vunfer.openrealms.model.GameSession;
import at.vunfer.openrealms.model.Player;
import at.vunfer.openrealms.network.DeckType;
import at.vunfer.openrealms.network.server.ServerMessageHandler;
import java.io.IOException;

public class BuyCard implements CardAction {
    private final ServerMessageHandler serverMessageHandler;

    public BuyCard(ServerMessageHandler serverMessageHandler) {
        this.serverMessageHandler = serverMessageHandler;
    }

    @Override
    public boolean handleAction(int cardId, GameSession gameSession, Player currentPlayer)
            throws IOException {
        if (currentPlayer.getPlayArea().buyCardById(cardId)) {
            serverMessageHandler.sendCardMovementToAllClients(
                    gameSession, currentPlayer, DeckType.FOR_PURCHASE, DeckType.DISCARD, cardId);
            return true;
        }
        return false;
    }
}
