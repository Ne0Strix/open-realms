/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import at.vunfer.openrealms.R;
import at.vunfer.openrealms.view.view_interfaces.OverlayViewInterface;

public class OverlayView extends ConstraintLayout implements OverlayViewInterface {
    private TextView playerName;
    private TextView playerHealth;
    private TextView opponentName;
    private TextView opponentHealth;
    private TextView turnDamage;
    private TextView turnHealing;
    private TextView turnCoin;
    private ImageView cheatingRing;

    public OverlayView(@NonNull Context context) {
        this(context, null);
    }

    public OverlayView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OverlayView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.overlay_view, this);

        playerName = findViewById(R.id.playerName);
        playerHealth = findViewById(R.id.playerHealth);
        opponentName = findViewById(R.id.opponentName);
        opponentHealth = findViewById(R.id.opponentHealth);
        turnDamage = findViewById(R.id.turnDamage);
        turnHealing = findViewById(R.id.turnHealing);
        turnCoin = findViewById(R.id.turnCoin);
        cheatingRing = findViewById(R.id.turnCoinIcon);
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
        return this;
    }

    @Override
    public void setCheatingEnabled(boolean enabled) {
        if (enabled) {
            cheatingRing.setImageResource(R.drawable.circle_outline_white_48);
        } else {
            cheatingRing.setImageResource(R.drawable.circle_outline_gold_48);
        }
    }
}
