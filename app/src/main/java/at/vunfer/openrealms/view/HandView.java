/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;
import at.vunfer.openrealms.view.view_interfaces.CardPileView;
import java.util.List;

public class HandView extends LinearLayout implements CardPileView {

    private static final float CARD_SCALE = 0.75f;

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
    }

    /** Sets the cards in the HandView. */
    @Override
    public void updateView(List<CardView> cards) {
        removeAllViews();
        int numCards = cards.size();
        float density = getResources().getDisplayMetrics().density;

        for (int i = 0; i < numCards; i++) {
            CardView card = cards.get(i);

            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(
                            (int) (CARD_SCALE * density * 77), (int) (CARD_SCALE * density * 106));
            /*
            params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
            params.setMargins(-20, 0, -20, 64);

            if (numCards % 2 != 0) {
                // even number of cards, center them
                params.rightMargin = i == numCards / 2 ? 8 : 0;
                params.leftMargin = i == numCards / 2 ? 8 : 0;
            } else {
                params.rightMargin = i == numCards / 2 - 1 || i == numCards / 2 ? 8 : 0;
                params.leftMargin = i == numCards / 2 - 1 || i == numCards / 2 ? 8 : 0;
            }
            */
            // Position the cards along an arc

            // card.setLayoutParams(new ViewGroup.LayoutParams(180, 250));
            card.setLayoutParams(params);
            addView(card);
        }
        positionCards(cards);
    }

    // TODO: Broken, leaves cards outside of View, in the center of the screen
    private void positionCards(List<CardView> cards) {
        int numCards = cards.size();
        float originalCardAngle = 12 * numCards;

        // Calculate the angle between cards
        float cardAngle = originalCardAngle / (numCards - 1);
        Log.d("Position Hand", "Setting card Angle: " + cardAngle);
        // Calculate the radius of the arc
        float radius = (float) ((getHeight() / 2.0) / Math.sin(Math.toRadians(cardAngle / 2.0)));
        Log.d("Position Hand", "Setting radius: " + radius);

        // Calculate the center point of the arc
        float centerX = getWidth() / 2.0f;
        float centerY = getHeight();
        Log.d("Position Hand", "Setting centerX: " + centerX);
        Log.d("Position Hand", "Setting centerY: " + centerY);

        // Position the cards along the arc
        for (int i = 0; i < numCards; i++) {
            CardView card = cards.get(i);

            // Calculate the angle of the current card
            float angle = -originalCardAngle / 2 + (i * cardAngle);
            if (numCards == 1) angle = 0;

            // Calculate the position of the card
            float x = centerX + (float) Math.sin(Math.toRadians(angle)) * radius;
            float y = centerY - (float) Math.cos(Math.toRadians(angle)) * radius;

            // Set the position and rotation of the card
            card.setX(x);
            card.setY(y);
            card.setRotation(angle);
            LinearLayout.LayoutParams params = (LayoutParams) card.getLayoutParams();
            params.setMargins(-20, 0, -20, (int) (2 * (originalCardAngle / 2 - Math.abs(angle))));
            // card.setY(0 - 2*(originalCardAngle / 2 - Math.abs(angle)));
            Log.d(
                    "Position Hand",
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
