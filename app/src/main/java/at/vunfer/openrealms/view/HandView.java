/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import java.util.ArrayList;
import java.util.List;

public class HandView extends LinearLayout {
    private static final String ERROR_MSG = "Error in HandView: ";
    private static final int MAX_HANDS = 10;

    private List<CardView> cards = new ArrayList<>();
    private LinearLayout handView;

    public HandView(Context context) {
        super(context);
        init();
    }

    public HandView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HandView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        // Creating the HandView layout parameters
        /* LinearLayout.LayoutParams params =
        new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);*/

        // Center the HandView layout parameters at the bottom of the screen.

        // Inflate the HandView layout with the created layout parameters.
        // handView = (LinearLayout) LayoutInflater.from(this.context).inflate(R.layout.hand_view,
        // this);
        // Log.i("EQ",v.equals(getChildAt(0))+" "+v.toString()+" "+getChildAt(0));
        /*       setOrientation(LinearLayout.HORIZONTAL);
        setMinimumHeight(1450);
        setLayoutParams(params);*/
        // handView.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        Log.d("AB", getChildCount() + "" + getChildAt(0));
    }

    /**
     * Sets the cards in the HandView.
     *
     * @param cards The cards to set.
     * @return True if cards were set, false otherwise.
     */
    private boolean setCards(List<CardView> cards) {
        if (cards == null) {
            return false;
        }
        int numCards = cards.size();

        for (int i = 0; i < numCards; i++) {
            CardView card = cards.get(i);

            Log.d("Card", card.getCard().toString());
            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f);
            /* params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
            params.setMargins(-20, 0, -20, 64);

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
            }*/
            // Position the cards along an arc
            positionCards();

            // card.setLayoutParams(new ViewGroup.LayoutParams(180, 250));
            card.setLayoutParams(params);
            addCard(card);
            Log.d("A", card.getX() + " " + card.getY() + " " + card.getRotation());
            Log.d("A", card.getParent().getParent() + "");
        }
        return true;
    }

    private void positionCards() {
        int numCards = cards.size();

        // Calculate the angle between cards
        float cardAngle = 60.0f / (numCards - 1);

        // Calculate the radius of the arc
        float radius = (float) ((getHeight() / 2.0) / Math.sin(Math.toRadians(cardAngle / 2.0)));

        // Calculate the center point of the arc
        float centerX = getWidth() / 2.0f;
        float centerY = getHeight();

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

    /**
     * Creates the first hand of cards for the player.
     *
     * @param playerStarterCards The cards to add to the hand.
     */
    public void createFirstHand(List<CardView> playerStarterCards) {
        setCards(playerStarterCards);
    }

    private void addCard(CardView card) {
        addView(card);
    }
}
