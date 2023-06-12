/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
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
import at.vunfer.openrealms.model.Champion;
import at.vunfer.openrealms.model.Effect;
import at.vunfer.openrealms.model.effects.CoinEffect;
import at.vunfer.openrealms.model.effects.DamageEffect;
import at.vunfer.openrealms.model.effects.DamagePerChampionInPlayEffect;
import at.vunfer.openrealms.model.effects.DamagePerGuardInPlayEffect;
import at.vunfer.openrealms.model.effects.DrawEffect;
import at.vunfer.openrealms.model.effects.HealingEffect;
import at.vunfer.openrealms.model.effects.HealingPerChampionInPlayEffect;
import at.vunfer.openrealms.view.effects.BasicEffectView;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** This class is used to represent a CardImageView. */
public class CardView extends ConstraintLayout {

    private static final int TEXT_SIZE = 8;
    private static final int MAX_WIDTH = 10;
    private static final int MAX_HEIGHT = 100;

    private Card card;
    private boolean isFaceUp = true;
    private boolean isExpended = false;
    public boolean isBeingHeld = false;
    // The time in mils a click has to be held to be considered holding vs clicking
    private static final long holdTime = 250L;
    private static final String logTag = "CardView";
    TextView health;
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
                if (motionEvent.getEventTime() - motionEvent.getDownTime() <= holdTime
                        && (!isExpended)) {
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
        fullScreenCard.setHealthSize(35);
    }

    /** Disables the FullscreenPreview */
    private void resetFullscreen() {
        CardView fullScreenCard = getRootView().findViewById(R.id.fullscreen_card);
        if (fullScreenCard != null) {
            fullScreenCard.setVisibility(INVISIBLE);
        }
    }

    /** Applies the details of the current card to the View. */
    @SuppressLint("SetTextI18n")
    private void applyCardDetail() {
        TextView name = findViewById(R.id.card_view_name);
        TextView cost = findViewById(R.id.card_view_cost);
        LinearLayout effectArea = findViewById(R.id.card_view_effectArea);
        ImageView typeIcon = findViewById(R.id.card_view_type_icon);
        ImageView background = findViewById(R.id.card_view_background);
        int typeIconResource = setCardTypeAndBackground(background);
        LinearLayout.LayoutParams paramsMatchParentHighWeight =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        0.5f);

        setCardNameAndCost(name, cost);
        clearEffectArea(effectArea);
        setTypeIconVisibility(typeIcon, typeIconResource);
        setCardImage();
        handleDefaultEffects(effectArea, paramsMatchParentHighWeight);
        handleSynergyEffects(effectArea, typeIconResource, paramsMatchParentHighWeight);
        handleChampionDetails();
    }

    private void setCardNameAndCost(TextView name, TextView cost) {
        name.setText(card.getName());
        cost.setText(String.valueOf(card.getCost()));
    }

    private void clearEffectArea(LinearLayout effectArea) {
        effectArea.removeAllViews();
    }

    private int setCardTypeAndBackground(ImageView background) {
        int typeIconResource = -1;
        switch (card.getFaction()) {
            case GUILD:
                background.setImageResource(R.drawable.card_guild);
                typeIconResource = R.drawable.guild_icon;
                break;
            case IMPERIAL:
                background.setImageResource(R.drawable.card_imperial);
                typeIconResource = R.drawable.imperial_icon;
                break;
            case NECROS:
                background.setImageResource(R.drawable.card_necros);
                typeIconResource = R.drawable.necros_icon;
                break;
            case WILD:
                background.setImageResource(R.drawable.card_wild);
                typeIconResource = R.drawable.wild_icon;
                break;
            default:
                background.setImageResource(R.drawable.emptycards);
                break;
        }
        return typeIconResource;
    }

    private void setTypeIconVisibility(ImageView typeIcon, int typeIconResource) {
        if (typeIconResource != -1) {
            typeIcon.setImageResource(typeIconResource);
            typeIcon.setVisibility(VISIBLE);
        } else {
            typeIcon.setVisibility(INVISIBLE);
        }
    }

    private void setCardImage() {
        // Parse card name and retrieve an image-resource with a matching name
        String resourceName = "card_image_";
        // Only lowercase characters a-z, 0-9 and _ are allowed in the names of android resources
        String cardName = card.getName().toLowerCase();
        cardName = cardName.replaceAll("[ -]", "_");
        cardName = cardName.replaceAll("[,']", "");

        resourceName = resourceName + cardName;
        @SuppressLint("DiscouragedApi")
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
    }

    private boolean isBasicEffect(Effect e) {
        return e instanceof DamageEffect
                || e instanceof HealingEffect
                || e instanceof CoinEffect
                || e instanceof DrawEffect
                || e instanceof DamagePerChampionInPlayEffect
                || e instanceof DamagePerGuardInPlayEffect
                || e instanceof HealingPerChampionInPlayEffect;
    }

    private void handleDefaultEffects(
            LinearLayout effectArea, LinearLayout.LayoutParams paramsMatchParentHighWeight) {
        LinearLayout defaultEffects = new LinearLayout(getContext());
        defaultEffects.setOrientation(LinearLayout.HORIZONTAL);
        defaultEffects.setLayoutParams(paramsMatchParentHighWeight);

        if (card instanceof Champion) {
            ImageView expandIcon = new ImageView(getContext());
            expandIcon.setImageResource(R.drawable.expend_icon);
            expandIcon.setLayoutParams(paramsMatchParentHighWeight);
            expandIcon.setMaxWidth(MAX_WIDTH);
            expandIcon.setMaxHeight(MAX_HEIGHT);
            defaultEffects.addView(expandIcon);
        }

        addEffects(card.getEffects(), defaultEffects, paramsMatchParentHighWeight);

        effectArea.addView(defaultEffects);
    }

    private void handleSynergyEffects(
            LinearLayout effectArea,
            int typeIconResource,
            LinearLayout.LayoutParams paramsMatchParentHighWeight) {
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

            addEffects(card.getSynergyEffects(), synergyEffects, paramsMatchParentHighWeight);

            effectArea.addView(synergyEffects);
        }
    }

    private void addEffects(
            List<Effect> effects, LinearLayout effectsLayout, LinearLayout.LayoutParams params) {
        for (Effect e : effects) {
            if (isBasicEffect(e)) {
                BasicEffectView effectView = new BasicEffectView(getContext(), e);
                effectView.setLayoutParams(params);
                effectsLayout.addView(effectView);
            }
        }
    }

    private void handleChampionDetails() {
        ImageView blackShield = findViewById(R.id.card_view_black_shield_icon);
        ImageView whiteShield = findViewById(R.id.card_view_white_shield_icon);
        health = findViewById(R.id.card_view_health);

        if (!(card instanceof Champion)) {
            whiteShield.setVisibility(INVISIBLE);
            blackShield.setVisibility(INVISIBLE);
            health.setVisibility(INVISIBLE);
            return;
        }

        Log.d("CardView", "applyCardDetail: " + card.getName() + " is a champion");
        ConstraintLayout shieldArea = findViewById(R.id.card_view_shield_area);
        shieldArea.setVisibility(VISIBLE);
        health.setText(Integer.toString(((Champion) card).getHealth()));
        health.setTextSize(TEXT_SIZE);
        health.setVisibility(VISIBLE);

        if (((Champion) card).isGuard()) {
            Log.d("CardView", "applyCardDetail: " + card.getName() + " is a guard");
            blackShield.setVisibility(VISIBLE);
            whiteShield.setVisibility(INVISIBLE);
            health.setTextColor(Color.WHITE);
        } else {
            whiteShield.setVisibility(VISIBLE);
            blackShield.setVisibility(INVISIBLE);
            health.setTextColor(Color.BLACK);
        }
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

    public void setIsExpended() {
        isExpended = true;
        findViewById(R.id.card_view_expended).setVisibility(VISIBLE);
    }

    public void setIsNotExpended() {
        isExpended = false;
        findViewById(R.id.card_view_expended).setVisibility(INVISIBLE);
    }

    public void setHealthSize(int size) {
        health = findViewById(R.id.card_view_health);
        health.setTextSize(size);
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

    public boolean isExpended() {
        return isExpended;
    }
}
