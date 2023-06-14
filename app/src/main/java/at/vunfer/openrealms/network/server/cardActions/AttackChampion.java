/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.network.server.cardActions;

import android.util.Log;
import at.vunfer.openrealms.model.GameSession;
import at.vunfer.openrealms.model.Player;
import at.vunfer.openrealms.network.server.ServerMessageHandler;
import java.io.IOException;

public class AttackChampion implements CardAction {
    private final ServerMessageHandler serverMessageHandler;

    public AttackChampion(ServerMessageHandler serverMessageHandler) {
        this.serverMessageHandler = serverMessageHandler;
    }

    @Override
    public boolean handleAction(int cardId, GameSession gameSession, Player currentPlayer)
            throws IOException {
        if (currentPlayer
                .getPlayArea()
                .attackChampionById(cardId, gameSession.getOpponent(currentPlayer).getPlayArea())) {
            Log.i(ServerMessageHandler.TAG, "Champion " + cardId + " attacked successfully.");
            serverMessageHandler.sendChampionKilledToAllClients(gameSession, currentPlayer, cardId);
            return true;
        }
        return false;
    }
}
