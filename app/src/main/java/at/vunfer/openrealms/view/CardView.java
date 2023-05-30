/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** This class is used to represent a CardImageView. */
public class CardView extends ConstraintLayout {
    private Card card;
    private boolean isFaceUp = true;
    public boolean isBeingHeld = false;
    // The time in mils a click has to be held to be considered holding vs clicking
    private static final long holdTime = 250L;
    private static final String logTag = "CardView";
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable setFullscreen =
            new Runnable() {
                public void run() {
                    if (isBeingHeld) {
                        setFullscreen();
                    }
                }
            };

    public CardView(Context context) {
        this(context, (AttributeSet) null);
    }

    public CardView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public CardView(Context context, Card card) {
        super(context);
        init();
        setCard(card);
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
        cardBackground.setOnTouchListener((view, motionEvent) -> onClick(motionEvent));
    }

    public boolean onClick(MotionEvent motionEvent) {
        if (!isFaceUp) return false;
        // Log.v(LOG_TAG, motionEvent.toString() + " " + card);
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_UP:
                if (motionEvent.getEventTime() - motionEvent.getDownTime() <= holdTime) {
                    Log.i(logTag, "Sending: " + card);
                    sendTouchMessage();
                }
                isBeingHeld = false;
                resetFullscreen();
                handler.removeCallbacks(setFullscreen);
                break;
            case MotionEvent.ACTION_CANCEL:
                isBeingHeld = false;
                resetFullscreen();
                handler.removeCallbacks(setFullscreen);
                break;
            case MotionEvent.ACTION_DOWN:
                isBeingHeld = true;
                handler.postDelayed(setFullscreen, holdTime);
                break;
        }
        return false;
    }

    public void sendTouchMessage() {
        try {
            MainActivity.sendMessage(MainActivity.buildTouchMessage(card.getId()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
        if (fullScreenCard != null) {
            fullScreenCard.setVisibility(INVISIBLE);
        }
    }

    /** Applies the details of the current card to the View. */
    private void applyCardDetail() {
        TextView name = findViewById(R.id.card_view_name);
        name.setText(card.getName());

        TextView cost = findViewById(R.id.card_view_cost);
        cost.setText(card.getCost() + "");

        LinearLayout effectArea = findViewById(R.id.card_view_effectArea);
        effectArea.removeAllViews();

        ImageView typeIcon = findViewById(R.id.card_view_type_icon);
        ImageView background = findViewById(R.id.card_view_background);
        int typeIconResource = -1;
        switch (card.getType()) {
            case GUILD:
                background.setImageResource(R.drawable.card_guild);
                typeIconResource = R.drawable.guild_icon;
                typeIcon.setImageResource(typeIconResource);
                typeIcon.setVisibility(VISIBLE);
                break;
            case IMPERIAL:
                background.setImageResource(R.drawable.card_imperial);
                typeIconResource = R.drawable.imperial_icon;
                typeIcon.setImageResource(typeIconResource);
                typeIcon.setVisibility(VISIBLE);
                break;
            case NECROS:
                background.setImageResource(R.drawable.card_necros);
                typeIconResource = R.drawable.necros_icon;
                typeIcon.setImageResource(typeIconResource);
                typeIcon.setVisibility(VISIBLE);
                break;
            case WILD:
                background.setImageResource(R.drawable.card_wild);
                typeIconResource = R.drawable.wild_icon;
                typeIcon.setImageResource(typeIconResource);
                typeIcon.setVisibility(VISIBLE);
                break;
            default:
                background.setImageResource(R.drawable.emptycards);
                typeIcon.setVisibility(INVISIBLE);
                break;
        }

        // Parse card name and retrieve an image-resource with a matching name
        String resourceName = "card_image_";
        // Only lowercase characters a-z, 0-9 and _ are allowed in the names of android resources
        String cardName = card.getName().toLowerCase();
        cardName = cardName.replaceAll("[ -]", "_");
        cardName = cardName.replaceAll(",", "");

        resourceName = resourceName + cardName;
        int imageResourceId =
                getResources()
                        .getIdentifier(resourceName, "drawable", getContext().getPackageName());
        ImageView cardImage = findViewById(R.id.card_view_image);
        if (imageResourceId != 0) {
            cardImage.setImageResource(imageResourceId);
        } else {
            Log.e(logTag, "Image Resource \"R.drawable." + resourceName + "\" was not found.");
            cardImage.setImageResource(R.drawable.playarea);
        }
        // Default effects
        LinearLayout defaultEffects = new LinearLayout(getContext());
        LinearLayout.LayoutParams paramsMatchParentHighWeight =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        0.5f);
        defaultEffects.setOrientation(LinearLayout.HORIZONTAL);
        defaultEffects.setLayoutParams(paramsMatchParentHighWeight);
        for (Effect e : card.getEffects()) {
            if (e instanceof DamageEffect
                    || e instanceof HealingEffect
                    || e instanceof CoinEffect) {
                BasicEffectView effectView = new BasicEffectView(getContext(), e);
                effectView.setLayoutParams(paramsMatchParentHighWeight);
                defaultEffects.addView(effectView);
            }
        }
        effectArea.addView(defaultEffects);

        // sacrifice effects

        // synergy effects
        if (!card.getSynergyEffects().isEmpty()) {
            LinearLayout synergyEffects = new LinearLayout(getContext());
            synergyEffects.setOrientation(LinearLayout.HORIZONTAL);
            synergyEffects.setLayoutParams(paramsMatchParentHighWeight);

            LinearLayout.LayoutParams paramsMatchParentLowWeight =
                    new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            1);
            ImageView synergyIcon = new ImageView(getContext());
            synergyIcon.setImageResource(typeIconResource);
            synergyIcon.setLayoutParams(paramsMatchParentLowWeight);
            synergyEffects.addView(synergyIcon);

            for (Effect e : card.getSynergyEffects()) {
                if (e instanceof DamageEffect
                        || e instanceof HealingEffect
                        || e instanceof CoinEffect) {
                    BasicEffectView effectView = new BasicEffectView(getContext(), e);
                    effectView.setLayoutParams(paramsMatchParentHighWeight);
                    synergyEffects.addView(effectView);
                }
            }
            effectArea.addView(synergyEffects);
        }
        // expand effects
    }

    public static List<CardView> getViewFromCards(Context context, List<Card> cards) {
        List<CardView> views = new ArrayList<>();
        for (Card c : cards) {
            views.add(new CardView(context, c));
        }
        return views;
    }

    public void setFaceUpOrDown(boolean faceDown) {
        if (faceDown) setFaceDown();
        else setFaceUp();
    }

    public void setFaceDown() {
        isFaceUp = false;
        findViewById(R.id.card_view_back_of_card).setVisibility(VISIBLE);
    }

    public void setFaceUp() {
        isFaceUp = true;
        findViewById(R.id.card_view_back_of_card).setVisibility(INVISIBLE);
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

    public int getCardId() {
        return card.getId();
    }
}
