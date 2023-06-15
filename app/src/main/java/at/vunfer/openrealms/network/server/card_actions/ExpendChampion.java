/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.network.server.card_actions;

import at.vunfer.openrealms.model.GameSession;
import at.vunfer.openrealms.model.Player;
import at.vunfer.openrealms.network.server.ServerMessageHandler;
import java.io.IOException;

public class ExpendChampion implements CardAction {
    private final ServerMessageHandler serverMessageHandler;

    public ExpendChampion(ServerMessageHandler serverMessageHandler) {
        this.serverMessageHandler = serverMessageHandler;
    }

    @Override
    public boolean handleAction(int cardId, GameSession gameSession, Player currentPlayer)
            throws IOException {
        if (currentPlayer.getPlayArea().expendChampionById(cardId)) {
            serverMessageHandler.sendChampionExpendedToAllClients(
                    gameSession, currentPlayer, cardId);
            serverMessageHandler.checkDrawnCard(gameSession, currentPlayer);
            return true;
        }
        return false;
    }
}
