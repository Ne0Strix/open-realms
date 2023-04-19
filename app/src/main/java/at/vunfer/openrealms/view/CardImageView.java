/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.view;

import android.content.Context;
import android.util.AttributeSet;
import at.vunfer.openrealms.model.Card;

/** This class is used to represent a CardImageView. */
public class CardImageView extends androidx.appcompat.widget.AppCompatImageView {
    private Card card;

    /**
     * Constructs a new CardImageView with the given context.
     *
     * @param context The context to use.
     */
    public CardImageView(Context context) {
        super(context);
    }

    /**
     * Constructs a new CardImageView with the given context and attributes.
     *
     * @param context The context to use.
     * @param attrs The attributes to use.
     */
    public CardImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Constructs a new CardImageView with the given context, attributes, and style.
     *
     * @param context The context to use.
     * @param attrs The attributes to use.
     * @param defStyleAttr The style to use.
     */
    public CardImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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
