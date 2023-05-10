/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import at.vunfer.openrealms.view.view_interfaces.CardPileView;
import java.util.List;

/** View class for the Market. */
public class MarketView extends LinearLayout implements CardPileView {

    public MarketView(Context context) {
        this(context, null);
    }

    public MarketView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MarketView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /** Constructor for the MarketView class. */
    public void init() {}

    /** Show the market */
    @Override
    public void updateView(List<CardView> cards) {
        removeAllViews();
        for (CardView card : cards) {
            if (card.getParent() != null)
                ((ViewGroup) card.getParent()).removeView(card);
            addView(card);
        }
    }
}
