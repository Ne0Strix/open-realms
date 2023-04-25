/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.presenter.presenterInterfaces;

public interface OverlayPresenterInterface {
    void updatePlayerName(String playerName);

    void updatePlayerHealth(int playerHealth);

    void updateOpponentName(String opponentName);

    void updateOpponentHealth(int opponentHealth);

    void updateTurnDamage(int turnDamage);

    void updateTurnHealing(int turnHealing);

    void updateTurnCoin(int turnCoin);
}
