/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.view;

import android.content.Context;
import android.util.AttributeSet;
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

        if (card != null) setCardDetail();
    }

    public void setCardDetail() {
        TextView name = findViewById(R.id.card_name);
        name.setText(card.getName());

        TextView cost = findViewById(R.id.card_cost);
        cost.setText(card.getCost() + "");

        LinearLayout effectArea = findViewById(R.id.effectArea);

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
        setCard(card);
        init();
    }

    public static List<CardView> getViewFromCards(Context context, List<Card> cards) {
        List<CardView> views = new ArrayList<>();
        for (Card c : cards) {
            views.add(new CardView(context, c));
        }
        return views;
    }

    /**
     * Sets the card for this CardImageView.
     *
     * @param card The card to set.
     */
    public void setCard(Card card) {
        this.card = card;
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
