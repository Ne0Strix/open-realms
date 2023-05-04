/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.presenter;

import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.constraintlayout.widget.ConstraintLayout;
import at.vunfer.openrealms.view.CardView;
import at.vunfer.openrealms.view.HandView;
import java.util.ArrayList;
import java.util.List;

public class HandPresenter {
    private static final String ERROR_MSG = "Error in HandView: ";
    private static final int MAX_HANDS = 5;

    private final List<CardView> cards = new ArrayList<>();
    private final LinearLayout handView;

    public HandPresenter(HandView handView) {
        this.handView = handView;
    }

    public List<CardView> getCards() {
        return this.cards;
    }

    public void createFirstHand() {
        for (int iterator = 0; iterator < MAX_HANDS; iterator++) {
            //    Card card = new Card(handView.getContext());
        }
        this.setCards();
    }

    /** Sets the list of cards to be displayed in the view. */
    private void setCards() {
        int numCards = cards.size();

        for (int i = 0; i < numCards; i++) {
            CardView card = cards.get(i);

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
                params.rightMargin = i == numCards / 2 ? 8 : 0;
                params.leftMargin = i == numCards / 2 ? 8 : 0;
            } else {
                params.rightMargin = i == numCards / 2 - 1 || i == numCards / 2 ? 8 : 0;
                params.leftMargin = i == numCards / 2 - 1 || i == numCards / 2 ? 8 : 0;
            }
            card.setLayoutParams(new ViewGroup.LayoutParams(180, 250));
            // Position the cards along an arc
            positionCards();

            addCard(card);
        }
    }

    private void positionCards() {
        int numCards = cards.size();

        // Calculate the angle between cards
        float cardAngle = 60.0f / (numCards - 1);

        // Calculate the radius of the arc
        float radius =
                (float) ((handView.getHeight() / 2.0) / Math.sin(Math.toRadians(cardAngle / 2.0)));

        // Calculate the center point of the arc
        float centerX = handView.getWidth() / 2.0f;
        float centerY = handView.getHeight();

        // Position the cards along the arc
        for (int i = 0; i < numCards; i++) {
            CardView card = cards.get(i);

            // Calculate the angle of the current card
            float angle = -30.0f + (i * cardAngle);

            // Calculate the position of the card
            float x = centerX + (float) Math.sin(Math.toRadians(angle)) * radius;
            float y = centerY - (float) Math.cos(Math.toRadians(angle)) * radius;

            // Set the position and rotation of the card
            card.setX(x);
            card.setY(y);
            card.setRotation(angle);
        }
    }

    private void addCard(CardView card) {
        this.handView.addView(card);
    }
}
