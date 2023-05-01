/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import at.vunfer.openrealms.R;
import at.vunfer.openrealms.model.Card;
import at.vunfer.openrealms.model.Effect;
import at.vunfer.openrealms.model.effects.CoinEffect;
import at.vunfer.openrealms.model.effects.DamageEffect;
import at.vunfer.openrealms.model.effects.HealingEffect;
import at.vunfer.openrealms.view.effects.BasicEffectView;
import java.util.ArrayList;
import java.util.List;

/** This class is used to represent a CardImageView. */
public class CardView extends ConstraintLayout {
    private Card card;
    private boolean isBeingHeld = false;
    // The time in mils a click has to be held to be considered holding vs clicking
    private final long HOLD_TIME = 250L;
    private final String LOG_TAG = "CardView";

    public CardView(Context context) {
        super(context);
        init();
    }

    public CardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        inflate(getContext(), R.layout.card_view, this);
        setUpListeners();
    }

    /**
     * Sets up the OnTouchListener to check if a card was clicked. If the card was clicked and
     * released within HOLD_TIME, a click is registered. If the card was clicked and released after
     * HOLD_TIME, a hold is registered.
     *
     * <p>On click a message is sent to the server containing the Card and the Position of the Card.
     * On hold a FullScreenPreview is shown to see more detail in the card.
     */
    @SuppressLint("ClickableViewAccessibility")
    public void setUpListeners() {
        ImageView cardBackground = findViewById(R.id.card_view_background);
        cardBackground.setOnTouchListener(
                (view, motionEvent) -> {
                    // Log.i(LOG_TAG, motionEvent.toString() + " " + card);
                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_UP:
                            if (motionEvent.getEventTime() - motionEvent.getDownTime()
                                    <= HOLD_TIME) {
                                String position = getParent().toString().split("id/")[1];
                                Log.i(LOG_TAG, "Sending: " + card + " " + position);
                            } else {
                                resetFullscreen();
                            }
                            isBeingHeld = false;
                            break;
                        case MotionEvent.ACTION_DOWN:
                            isBeingHeld = true;
                            final Handler handler = new Handler(Looper.getMainLooper());
                            handler.postDelayed(
                                    () -> {
                                        if (isBeingHeld) {
                                            setFullscreen();
                                        }
                                    },
                                    HOLD_TIME);
                            break;
                    }
                    return false;
                });
    }

    /** Enables the FullscreenPreview */
    private void setFullscreen() {
        // Get the view for the Fullscreen_Card Object from RootView
        CardView fullScreenCard = getRootView().findViewById(R.id.fullscreen_card);
        fullScreenCard.setCard(card);
        fullScreenCard.setVisibility(VISIBLE);
        // Make sure that the the Fullscreen_Card Object is drawn in front of everything
        fullScreenCard.getParent().bringChildToFront(fullScreenCard);
    }

    /** Disables the FullscreenPreview */
    private void resetFullscreen() {
        CardView fullScreenCard = getRootView().findViewById(R.id.fullscreen_card);
        fullScreenCard.setVisibility(INVISIBLE);
    }

    /** Applies the details of the current card to the View. */
    private void applyCardDetail() {
        TextView name = findViewById(R.id.card_view_name);
        name.setText(card.getName());

        TextView cost = findViewById(R.id.card_view_cost);
        cost.setText(card.getCost() + "");

        LinearLayout effectArea = findViewById(R.id.card_view_effectArea);
        effectArea.removeAllViews();

        // Default effects
        LinearLayout defaultEffects = new LinearLayout(getContext());
        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1f);
        defaultEffects.setOrientation(LinearLayout.HORIZONTAL);
        defaultEffects.setLayoutParams(params);
        for (Effect e : card.getEffects()) {
            if (e instanceof DamageEffect
                    || e instanceof HealingEffect
                    || e instanceof CoinEffect) {
                BasicEffectView effectView = new BasicEffectView(getContext(), e);
                effectView.setLayoutParams(params);
                defaultEffects.addView(effectView);
            }
        }
        effectArea.addView(defaultEffects);

        // sacrifice effects

        // synergy effects

        // expand effects
    }

    public CardView(Context context, Card card) {
        super(context);
        init();
        setCard(card);
    }

    public static List<CardView> getViewFromCards(Context context, List<Card> cards) {
        List<CardView> views = new ArrayList<>();
        for (Card c : cards) {
            views.add(new CardView(context, c));
        }
        return views;
    }

    /**
     * Sets the card for this CardImageView and applies it's details to the View.
     *
     * @param card The card to set.
     */
    public void setCard(Card card) {
        this.card = card;
        applyCardDetail();
    }

    /**
     * Gets the card for this CardImageView.
     *
     * @return The card for this CardImageView.
     */
    public Card getCard() {
        return card;
    }
}
