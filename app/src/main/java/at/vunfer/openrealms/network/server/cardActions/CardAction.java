/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.network.server.cardActions;

import at.vunfer.openrealms.model.GameSession;
import at.vunfer.openrealms.model.Player;
import java.io.IOException;

public interface CardAction {
    boolean handleAction(int cardId, GameSession gameSession, Player currentPlayer)
            throws IOException;
}
