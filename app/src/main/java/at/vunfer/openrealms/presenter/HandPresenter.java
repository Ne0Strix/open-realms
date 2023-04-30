package at.vunfer.openrealms.presenter;


import android.view.View;

import java.util.ArrayList;
import java.util.List;

import at.vunfer.openrealms.model.Card;
import at.vunfer.openrealms.view.HandView;

public class HandPresenter {
    private static final String ERROR_MSG = "Error in HandView: ";
    private static final int MAX_HANDS = 5;

    private List<Card> cards = new ArrayList<>();
    private HandPresenter.OnCardSelectedListener onCardSelectedListener;
    private final HandView handView;

    public HandPresenter(HandView handView) {
        this.handView = handView;
    }

    public interface OnCardSelectedListener {
        void onCardSelected(Card card);

        void onCardDropped(Card card);
    }

    public void setOnCardSelectedListener(HandPresenter.OnCardSelectedListener listener) {
        this.onCardSelectedListener = listener;
    }

    public void createFirstHand() {
        for (int iterator = 0; iterator < MAX_HANDS; iterator++) {
            Card card = new Card(handView.getContext());
            card.getCardImage().setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    v.startDragAndDrop(null, new View.DragShadowBuilder(v), null, 0);
                    return true;
                }
            });
            card.getCardImage()
                    .setOnClickListener(
                            new View.OnClickListener() {
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

    private void setCards() {
        if (cards == null) {
            throw new IllegalArgumentException(ERROR_MSG + "Cards list is null");
        }
        int numCards = cards.size();

        for (int i = 0; i < numCards; i++) {
            Card card = cards.get(i);

            if (numCards % 2 != 0) {
                // even number of cards, center them
                int half = numCards / 2;
                if (i < half) {
                    card.setPosition(i);
                } else if (i > half) {
                    card.setPosition(i - 1);
                } else {
                    // middle card
                    card.setPosition(half);
                }
            } else {
                // odd number of cards, center middle card
                if (i == numCards / 2) {
                    card.setPosition(i);
                } else if (i < numCards / 2) {
                    card.setPosition(i);
                } else {
                    card.setPosition(i - 1);
                }
            }
            // Position the cards along an arc
            positionCards();
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
            card.setX(x);
            card.setY(y);
            card.setRotation(angle);
        }
    }
}
