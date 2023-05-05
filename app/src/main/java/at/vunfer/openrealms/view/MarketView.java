/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.view;

import android.content.Context;
import android.util.AttributeSet;
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
    public void init() {
        // Creating MarketView layout parameters
        /*
                ConstraintLayout.LayoutParams params =
                        new ConstraintLayout.LayoutParams(
                                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                                ConstraintLayout.LayoutParams.WRAP_CONTENT);

                // Centering the MarketView layout parameters in the middle of the game field
                params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
                params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
                params.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
                params.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
        */

        // Inflating the MarketView layout with the created layout parameters
        /*this.marketView =
                (LinearLayout)
                        LayoutInflater.from(getContext()).inflate(R.layout.market_view, null);
        this.marketView.setMinimumHeight(90);
        this.marketView.setLayoutParams(params);*/
    }

    /** Show the market */
    @Override
    public void updateView(List<CardView> cards) {
        removeAllViews();
        for (CardView card : cards) {
            addView(card);
        }
    }
}
