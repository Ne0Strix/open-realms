/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.view.view_interfaces;

import android.view.View;

public interface OverlayViewInterface {
    void setPlayerName(String playerName);

    String getPlayerName();

    void setPlayerHealth(int playerHealth);

    String getPlayerHealth();

    void setOpponentName(String opponentName);

    String getOpponentName();

    void setOpponentHealth(int opponentHealth);

    String getOpponentHealth();

    void setTurnDamage(int turnDamage);

    String getTurnDamage();

    void setTurnHealing(int turnHealing);

    String getTurnHealing();

    void setTurnCoin(int turnCoin);

    String getTurnCoin();

    View getOverlayView();
}
