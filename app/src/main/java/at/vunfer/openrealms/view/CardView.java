/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;
import at.vunfer.openrealms.R;
import at.vunfer.openrealms.model.Card;

/** This class is used to represent a CardImageView. */
public class CardView extends RelativeLayout {
    private Card card;
    private final int DESIRED_WIDTH = 90; // dp
    private final int DESIRED_HEIGHT = 125; // dp

    private void scaleCard() {
        int originalWidth = 350; // dp
        int originalHeight = 500; // dp

        float pixelScale = getContext().getResources().getDisplayMetrics().density;

        int scaledWidth = (int) (originalWidth * pixelScale);
        int scaledHeight = (int) (originalHeight * pixelScale);

        setLayoutParams(new LayoutParams(scaledWidth, scaledHeight));

        float scaledTargetWidth = DESIRED_WIDTH * pixelScale;
        float scaledTargetHeight = DESIRED_HEIGHT * pixelScale;

        setScaleX(scaledTargetWidth / scaledWidth);
        setScaleY(scaledTargetHeight / scaledHeight);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int originalWidth = getMeasuredWidth();
        int originalHeight = getMeasuredHeight();

        int newWidth = (int) (originalWidth * getScaleX());
        int newHeight = (int) (originalHeight * getScaleY());

        setMeasuredDimension(newWidth, newHeight);
    }

    /**
     * Constructs a new CardImageView with the given context.
     *
     * @param context The context to use.
     */
    public CardView(Context context) {
        super(context);
        init();
    }

    /**
     * Constructs a new CardImageView with the given context and attributes.
     *
     * @param context The context to use.
     * @param attrs The attributes to use.
     */
    public CardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * Constructs a new CardImageView with the given context, attributes, and style.
     *
     * @param context The context to use.
     * @param attrs The attributes to use.
     * @param defStyleAttr The style to use.
     */
    public CardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        scaleCard();
        inflate(getContext(), R.layout.card_view, this);

        if (card != null) setCardDetail();
    }

    public void setCardDetail() {
        TextView name = findViewById(R.id.card_name);
        name.setText(card.getName());

        //   TextView cost = findViewById(R.id.card_cost);
        //  cost.setText(card.getCost());
    }

    public CardView(Context context, Card card) {
        super(context);
        setCard(card);
        init();
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
