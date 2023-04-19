/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.presenter;

import at.vunfer.openrealms.model.GameSession;
import at.vunfer.openrealms.model.Player;
import at.vunfer.openrealms.view.TurnValuesView;

public class TurnValuesPresenter {
    private TurnValuesView turnValuesView;
    private GameSession gameSession;

    public TurnValuesPresenter(TurnValuesView turnValuesView, GameSession gameSession) {
        this.turnValuesView = turnValuesView;
        this.gameSession = gameSession;
        updateTurnValuesView();
    }

    public void updateTurnValuesView() {
        Player currentPlayer = gameSession.getCurrentPlayer();
        Player nextPlayer = gameSession.getOpponent(gameSession.getCurrentPlayer());

        turnValuesView.setCurrentPlayerName("Current Player: " + currentPlayer.getPlayerName());
        turnValuesView.setOpponentName("Opponent: " + nextPlayer.getPlayerName());
        turnValuesView.setCurrentPlayerHealth("Health: " + currentPlayer.getPlayArea().getHealth());
        turnValuesView.setOpponentHealth("Health: " + nextPlayer.getPlayArea().getHealth());
        turnValuesView.setTurnValues("TurnCoin: " + gameSession.getCurrentPlayer().getPlayArea().getTurnCoins()
                                    + "\nTurnDamage: " + gameSession.getCurrentPlayer().getPlayArea().getTurnDamage()
                                    + "\nTurnHealing: " + gameSession.getCurrentPlayer().getPlayArea().getTurnHealing()  );
    }

}
