/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.view.viewInterfaces;

import android.view.View;

public interface OverlayViewInterface {
    void setPlayerName(String playerName);

    void setPlayerHealth(int playerHealth);

    void setOpponentName(String opponentName);

    void setOpponentHealth(int opponentHealth);

    void setTurnDamage(int turnDamage);

    void setTurnHealing(int turnHealing);

    void setTurnCoin(int turnCoin);

    View getOverlayView();
}
