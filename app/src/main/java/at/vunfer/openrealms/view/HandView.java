/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.view;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.constraintlayout.widget.ConstraintLayout;
import at.vunfer.openrealms.R;
import at.vunfer.openrealms.model.Card;
import at.vunfer.openrealms.model.Deck;

public class HandView extends LinearLayout {
    // private static final String ERROR_MSG = "Error in HandView: ";

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

    /**
     * Sets the cards in the HandView.
     *
     * @param cards The cards to set.
     * @return True if cards were set, false otherwise.
     */
    private boolean setCards(Deck<Card> cards) {
        if (cards == null) {
            return false;
        }
        int numCards = cards.size();

        for (int i = 0; i < numCards; i++) {
            Card card = cards.get(i);
            card.getCardImage().setImageResource(R.drawable.emptycards);

            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f);
            params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
            params.setMargins(-20, 0, -20, 64);

            this.handView.setLayoutParams(
                    new ConstraintLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));

            this.handView.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);

            if (numCards % 2 != 0) {
                // even number of cards, center them
                int half = numCards / 2;
                if (i < half) {
                    params.rightMargin = 8;
                } else if (i > half) {
                    params.leftMargin = 8;
                } else {
                    // middle card
                    params.leftMargin = 8;
                    params.rightMargin = 8;
                }
            } else {
                // odd number of cards, center middle card
                if (i == numCards / 2) {
                    params.leftMargin = 8;
                    params.rightMargin = 8;
                }
            }
            card.getCardImage().setLayoutParams(new ViewGroup.LayoutParams(180, 250));
            card.getCardImage().setLayoutParams(params);
            addCard(card);
        }
        return true;
    }

    public LinearLayout getHandView() {
        return handView;
    }

    public View getView() {
        return handView;
    }

    /**
     * Creates the first hand of cards for the player.
     *
     * @param playerStarterCards The cards to add to the hand.
     */
    public void createFirstHand(Deck<Card> playerStarterCards) {}

    private void addCard(Card card) {
        this.handView.addView(card.getCardImage());
    }
}
