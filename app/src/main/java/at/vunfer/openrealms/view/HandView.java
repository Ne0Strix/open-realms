/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.view;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.constraintlayout.widget.ConstraintLayout;
import at.vunfer.openrealms.R;
import at.vunfer.openrealms.model.Card;
import at.vunfer.openrealms.model.Deck;
import at.vunfer.openrealms.model.GameSession;
import java.util.ArrayList;
import java.util.List;

public class HandView extends LinearLayout {
    private static final String ERROR_MSG = "Error in HandView: ";
    private static final int MAX_HANDS = 10;

    private Context context;
    private List<Card> cards = new ArrayList<>();
    private LinearLayout handView;
    private OnCardSelectedListenerInterface onCardSelectedListener;
    private GameSession gameSession;

    public HandView(Context context, GameSession gameSession) {
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
        this.gameSession = gameSession;
    }

    /*  public void createFirstHand() {
            Deck<Card> deck =
                    gameSession.getCurrentPlayer().getPlayArea().getPlayerCards().getHandCards();
            for (Card card : deck) {
                card.getCardImage().setLongClickable(true);
                card.getCardImage()
                        .setOnClickListener(
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (onCardSelectedListener != null && gameSession != null) {
                                            onCardSelectedListener.onCardSelected(
                                                    card, gameSession.getCurrentPlayer().getPlayArea(),handView);
                                        } else {
                                            System.out.println("game session null");
                                            ;
                                        }
                                    }
                                });
                this.cards.add(card);
            }
            this.setCards();
        }
    */
    public void resetHand() {

        // Invalidate and request layout to force a redraw of the layout
        handView.invalidate();
        handView.requestLayout();
        // Remove all old card views from handView
        handView.removeAllViews();

        // Clear the cards list
        this.cards.clear();

        Deck<Card> deck =
                gameSession.getCurrentPlayer().getPlayArea().getPlayerCards().getHandCards();
        Log.v("RESET HAND", "Gotten Cards from session: " + deck.toString());

        for (Card card : deck) {
            card.getCardImage().setLongClickable(true);
            HandView handView = this;
            card.getCardImage()
                    .setOnClickListener(
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (onCardSelectedListener != null && gameSession != null) {
                                        onCardSelectedListener.onCardSelected(
                                                card,
                                                gameSession.getCurrentPlayer().getPlayArea(),
                                                handView);
                                    } else {
                                        System.out.println("game session null");
                                    }
                                }
                            });
            this.cards.add(card);
        }
        this.setCards();
    }

    /**
     * Sets the list of cards to be displayed in the view.
     *
     * @param //cards the list of cards to be displayed
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
            // positionCards();

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
        Log.v("TEST", "CenterOfHandView: " + centerX);
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
            Log.v(
                    "POSITOIN CARDS",
                    "Looking at CardNr. " + i + " x=" + x + " y=" + y + " angle=" + angle);
        }
    }

    private void addCard(Card card) {
        this.handView.addView(card.getCardImage());
    }

    public LinearLayout getHandView() {
        return handView;
    }

    public View getView() {
        return handView;
    }

    public void setOnCardSelectedListener(OnCardSelectedListenerInterface onCardSelectedListener) {
        this.onCardSelectedListener = onCardSelectedListener;
    }
}