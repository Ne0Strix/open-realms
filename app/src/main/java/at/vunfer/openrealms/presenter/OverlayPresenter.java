/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.presenter;

import at.vunfer.openrealms.model.GameSession;
import at.vunfer.openrealms.presenter.presenterInterfaces.OverlayPresenterInterface;
import at.vunfer.openrealms.view.viewInterfaces.OverlayViewInterface;

public class OverlayPresenter implements OverlayPresenterInterface {
    private OverlayViewInterface overlayView;
    private GameSession gameSession;

    public OverlayPresenter(OverlayViewInterface overlayView, GameSession gameSession) {
        this.overlayView = overlayView;
        this.gameSession = gameSession;
    }

    @Override
    public void updatePlayerName(String playerName) {
        overlayView.setPlayerName(playerName);
    }

    @Override
    public void updatePlayerHealth(int playerHealth) {
        overlayView.setPlayerHealth(playerHealth);
    }

    @Override
    public void updateOpponentName(String opponentName) {
        overlayView.setOpponentName(opponentName);
    }

    @Override
    public void updateOpponentHealth(int opponentHealth) {
        overlayView.setOpponentHealth(opponentHealth);
    }

    @Override
    public void updateTurnDamage(int turnDamage) {
        overlayView.setTurnDamage(turnDamage);
    }

    @Override
    public void updateTurnHealing(int turnHealing) {
        overlayView.setTurnHealing(turnHealing);
    }

    @Override
    public void updateTurnCoin(int turnCoin) {
        overlayView.setTurnCoin(turnCoin);
    }
}
