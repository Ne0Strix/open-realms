/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.network.server.card_actions;

import at.vunfer.openrealms.model.GameSession;
import at.vunfer.openrealms.model.Player;
import at.vunfer.openrealms.network.DeckType;
import at.vunfer.openrealms.network.server.ServerMessageHandler;
import java.io.IOException;

public class PlayCard implements CardAction {
    private final ServerMessageHandler serverMessageHandler;

    public PlayCard(ServerMessageHandler serverMessageHandler) {
        this.serverMessageHandler = serverMessageHandler;
    }

    @Override
    public boolean handleAction(int cardId, GameSession gameSession, Player currentPlayer)
            throws IOException {
        int cardType = currentPlayer.getPlayArea().playCardById(cardId);
        if (cardType == 1 || cardType == 2) {
            serverMessageHandler.sendCardMovementToAllClients(
                    gameSession,
                    currentPlayer,
                    DeckType.HAND,
                    cardType == 1 ? DeckType.PLAYED : DeckType.CHAMPIONS,
                    cardId);
            serverMessageHandler.checkDrawnCard(gameSession, currentPlayer);
            return true;
        }
        return false;
    }
}
