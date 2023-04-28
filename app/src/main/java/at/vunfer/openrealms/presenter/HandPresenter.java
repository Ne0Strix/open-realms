package at.vunfer.openrealms.presenter;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;
import java.util.List;

import at.vunfer.openrealms.R;
import at.vunfer.openrealms.model.Card;
import at.vunfer.openrealms.view.HandView;

public class HandPresenter {
    private static final String ERROR_MSG = "Error in HandView: ";
    private static final int MAX_HANDS = 5;

    private List<Card> cards = new ArrayList<>();
    private LinearLayout handView;
    private HandPresenter.OnCardSelectedListener onCardSelectedListener;

    public HandPresenter(HandView handView) {
        this.handView = handView;
    }

    public List<Card> getCards() {
        return this.cards;
    }
    public interface OnCardSelectedListener {
        void onCardSelected(Card card);

        void onCardDropped(Card card);
    }

    public void createFirstHand() {
        for (int iterator = 0; iterator < MAX_HANDS; iterator++) {
            Card card = new Card(handView.getContext());
            card.getCardImage().setLongClickable(true);
            card.getCardImage()
                    .setOnClickListener(
                            new android.view.View.OnClickListener() {
                                @Override
                                public void onClick(android.view.View v) {
                                    if (onCardSelectedListener != null) {
                                        onCardSelectedListener.onCardSelected(card);
                                    }
                                }
                            });
            this.cards.add(card);
        }
        this.setCards();
    }

    /**
     * Sets the list of cards to be displayed in the view.
     */
    private void setCards() {
        if (cards == null) {
            throw new IllegalArgumentException(ERROR_MSG + "Cards list is null");
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
            Card card = cards.get(i);

            // Calculate the angle of the current card
            float angle = -30.0f + (i * cardAngle);

            // Calculate the position of the card
            float x = centerX + (float) Math.sin(Math.toRadians(angle)) * radius;
            float y = centerY - (float) Math.cos(Math.toRadians(angle)) * radius;

            // Set the position and rotation of the card
            card.getCardImage().setX(x);
            card.getCardImage().setY(y);
            card.getCardImage().setRotation(angle);
        }
    }

    private void addCard(Card card) {
        this.handView.addView(card.getCardImage());
    }
}
