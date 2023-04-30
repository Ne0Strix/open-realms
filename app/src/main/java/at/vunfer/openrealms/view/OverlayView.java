/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import at.vunfer.openrealms.R;
import at.vunfer.openrealms.view.view_interfaces.OverlayViewInterface;

public class OverlayView implements OverlayViewInterface {
    private ConstraintLayout overlayViewLayout;
    private TextView playerName;
    private TextView playerHealth;
    private TextView opponentName;
    private TextView opponentHealth;
    private TextView turnDamage;
    private TextView turnHealing;
    private TextView turnCoin;

    @SuppressLint("InflateParams")
    public OverlayView(Context context) {
        overlayViewLayout =
                (ConstraintLayout)
                        LayoutInflater.from(context).inflate(R.layout.overlay_view, null);

        ConstraintLayout.LayoutParams layoutParams =
                new ConstraintLayout.LayoutParams(
                        android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                        android.view.ViewGroup.LayoutParams.MATCH_PARENT);
        overlayViewLayout.setLayoutParams(layoutParams);

        playerName = overlayViewLayout.findViewById(R.id.playerName);
        playerHealth = overlayViewLayout.findViewById(R.id.playerHealth);
        opponentName = overlayViewLayout.findViewById(R.id.opponentName);
        opponentHealth = overlayViewLayout.findViewById(R.id.opponentHealth);
        turnDamage = overlayViewLayout.findViewById(R.id.turnDamage);
        turnHealing = overlayViewLayout.findViewById(R.id.turnHealing);
        turnCoin = overlayViewLayout.findViewById(R.id.turnCoin);
    }

    @Override
    public void setPlayerName(String playerName) {
        this.playerName.setText(playerName);
    }

    @Override
    public String getPlayerName() {
        return playerName.getText().toString();
    }

    @Override
    public void setPlayerHealth(int playerHealth) {
        this.playerHealth.setText(String.valueOf(playerHealth));
    }

    @Override
    public String getPlayerHealth() {
        return playerHealth.getText().toString();
    }

    @Override
    public void setOpponentName(String opponentName) {
        this.opponentName.setText(opponentName);
    }

    @Override
    public String getOpponentName() {
        return opponentName.getText().toString();
    }

    @Override
    public void setOpponentHealth(int opponentHealth) {
        this.opponentHealth.setText(String.valueOf(opponentHealth));
    }

    @Override
    public String getOpponentHealth() {
        return this.opponentHealth.getText().toString();
    }

    @Override
    public void setTurnDamage(int turnDamage) {
        this.turnDamage.setText(String.valueOf(turnDamage));
    }

    @Override
    public String getTurnDamage() {
        return turnDamage.getText().toString();
    }

    @Override
    public void setTurnHealing(int turnHealing) {
        this.turnHealing.setText(String.valueOf(turnHealing));
    }

    @Override
    public String getTurnHealing() {
        return turnHealing.getText().toString();
    }

    @Override
    public void setTurnCoin(int turnCoin) {
        this.turnCoin.setText(String.valueOf(turnCoin));
    }

    @Override
    public String getTurnCoin() {
        return turnCoin.getText().toString();
    }

    @Override
    public ConstraintLayout getOverlayView() {
        return overlayViewLayout;
    }
}
