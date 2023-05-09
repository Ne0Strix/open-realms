/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import at.vunfer.openrealms.MainActivity;
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
    private static final long holdTime = 250L;
    private static final String logTag = "CardView";

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
                    // Log.v(LOG_TAG, motionEvent.toString() + " " + card);
                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_UP:
                            if (motionEvent.getEventTime() - motionEvent.getDownTime()
                                    <= holdTime) {
                                int parentId = ((View) getParent()).getId();
                                Log.i(
                                        logTag,
                                        "Sending: "
                                                + card
                                                + " from int id: "
                                                + parentId
                                                + " or String id "
                                                + getResources().getResourceName(parentId));
                                // TODO: implement message sending
                                // TODO: remove temporary cardRemoval
                                if (parentId == R.id.player_hand_view) {
                                    MainActivity.playerHandPresenter.removeCardFromView(this);
                                }
                                if (parentId == R.id.opponent_hand_view) {
                                    MainActivity.opponentHandPresenter.removeCardFromView(this);
                                }
                                //   MainActivity.playAreaPresenter.removeCardFromView(this);
                                else if (parentId == R.id.market_view) {
                                    //   MainActivity.marketPresenter.removeCardFromView(this);
                                }
                            } else {
                                resetFullscreen();
                            }
                            isBeingHeld = false;
                            break;
                        case MotionEvent.ACTION_CANCEL:
                            resetFullscreen();
                            break;
                        case MotionEvent.ACTION_DOWN:
                            isBeingHeld = true;
                            final Handler handler = new Handler(Looper.getMainLooper());
                            // TODO: don't show fullscreen while scrolling in playArea
                            handler.postDelayed(
                                    () -> {
                                        if (isBeingHeld) {
                                            setFullscreen();
                                        }
                                    },
                                    holdTime);
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
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        1f);
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
