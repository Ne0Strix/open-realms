/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;
import at.vunfer.openrealms.R;
import at.vunfer.openrealms.view.view_interfaces.CardPileView;
import java.util.List;

public class HandView extends LinearLayout implements CardPileView {

    private static final float CARD_SCALE = 0.75f;
    private float screenDensity;
    private boolean isOpponent;

    public HandView(Context context) {
        this(context, null);
    }

    public HandView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HandView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        screenDensity = getResources().getDisplayMetrics().density;
        isOpponent = (getId() == R.id.opponent_hand_view);
    }

    /** Sets the cards in the HandView. */
    @Override
    public void updateView(List<CardView> cards) {
        removeAllViews();
        int numCards = cards.size();

        for (int i = 0; i < numCards; i++) {
            CardView card = cards.get(i);
            card.setFaceUpOrDown(isOpponent);
            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(
                            (int) (CARD_SCALE * screenDensity * 77),
                            (int) (CARD_SCALE * screenDensity * 106));
            card.setLayoutParams(params);
            addView(card);
        }
        positionCards(cards);
    }

    private void positionCards(List<CardView> cards) {
        int numCards = cards.size();
        float originalCardAngle = 12f * numCards;

        // Calculate the angle between cards
        float cardAngle = originalCardAngle / (numCards - 1);
        Log.d("Position Hand", "Setting card Angle: " + cardAngle);

        // Position the cards along the arc
        for (int i = 0; i < numCards; i++) {
            CardView card = cards.get(i);

            // Calculate the angle of the current card
            float angle = -originalCardAngle / 2 + (i * cardAngle);
            if (numCards == 1) angle = 0;

            // Set the position and rotation of the card
            card.setRotation(angle);
            LinearLayout.LayoutParams params = (LayoutParams) card.getLayoutParams();
            params.setMargins(-20, 0, -20, (int) (2 * (originalCardAngle / 2 - Math.abs(angle))));

            Log.d(
                    "HandView",
                    "Setting "
                            + card.getCard().getName()
                            + " X:"
                            + card.getX()
                            + " Y:"
                            + card.getY()
                            + " Rotation:"
                            + card.getRotation());
        }
    }
}
