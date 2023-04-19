/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import at.vunfer.openrealms.R;

public class TurnValuesView {
    private View view;
    private TextView turnValues;
    private TextView currentPlayerName;
    private TextView currentPlayerHealth;
    private TextView opponentName;
    private TextView opponentHealth;

    public TurnValuesView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.turn_values_view, null);
        turnValues = view.findViewById(R.id.turn_values);
        currentPlayerName = view.findViewById(R.id.current_player_name);
        currentPlayerHealth = view.findViewById(R.id.current_player_health);
        opponentName = view.findViewById(R.id.opponent_name);
        opponentHealth = view.findViewById(R.id.opponent_health);
    }

    public View getTurnValuesView() {
        return view;
    }

    public void setCurrentPlayerName(String name) {
        currentPlayerName.setText(name);
    }

    public void setCurrentPlayerHealth(String health) {
        currentPlayerHealth.setText(health);
    }

    public void setOpponentName(String name) {
        opponentName.setText(name);
    }

    public void setOpponentHealth(String health) {
        opponentHealth.setText(health);
    }

    public void setTurnValues(String turnCoin) {
        turnValues.setText(turnCoin);
    }
}
