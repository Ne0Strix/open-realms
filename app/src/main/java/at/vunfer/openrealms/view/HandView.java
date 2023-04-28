/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.view;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import at.vunfer.openrealms.R;
import at.vunfer.openrealms.model.Card;
import at.vunfer.openrealms.model.Deck;

public class HandView extends LinearLayout {

    private Context context;
    private LinearLayout handView;

    public HandView(Context context) {
        super(context);
        this.context = context;

        // Creating the HandView layout parameters
        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);

        // Center the HandView layout parameters at the bottom of the screen.
        params.gravity = Gravity.BOTTOM;

        // Inflate the HandView layout with the created layout parameters.
        this.handView =
                (LinearLayout) LayoutInflater.from(this.context).inflate(R.layout.hand_view, null);
        this.handView.setOrientation(LinearLayout.HORIZONTAL);
        this.handView.setMinimumHeight(1450);
        this.handView.setLayoutParams(params);
    }

    public LinearLayout getHandView() {
        return handView;
    }

    public View getView() {
        return handView;
    }
    public void createFirstHand(Deck<Card> playerStarterCards) {
    }
}
