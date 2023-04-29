/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import at.vunfer.openrealms.R;
import at.vunfer.openrealms.view.viewInterfaces.OverlayViewInterface;

public class OverlayView implements OverlayViewInterface {
    private ConstraintLayout overlayView;
    private TextView playerName;
    private TextView playerHealth;
    private TextView opponentName;
    private TextView opponentHealth;
    private TextView turnDamage;
    private TextView turnHealing;
    private TextView turnCoin;

    @SuppressLint("InflateParams")
    public OverlayView(Context context) {
        overlayView =
                (ConstraintLayout)
                        LayoutInflater.from(context).inflate(R.layout.overlay_view, null);

        ConstraintLayout.LayoutParams layoutParams =
                new ConstraintLayout.LayoutParams(
                        ConstraintLayout.LayoutParams.MATCH_PARENT,
                        ConstraintLayout.LayoutParams.MATCH_PARENT);
        overlayView.setLayoutParams(layoutParams);

        playerName = overlayView.findViewById(R.id.playerName);
        playerHealth = overlayView.findViewById(R.id.playerHealth);
        opponentName = overlayView.findViewById(R.id.opponentName);
        opponentHealth = overlayView.findViewById(R.id.opponentHealth);
        turnDamage = overlayView.findViewById(R.id.turnDamage);
        turnHealing = overlayView.findViewById(R.id.turnHealing);
        turnCoin = overlayView.findViewById(R.id.turnCoin);
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
        return overlayView;
    }
}
