/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import at.vunfer.openrealms.MainActivity;
import at.vunfer.openrealms.R;
import at.vunfer.openrealms.model.Card;
import java.util.List;
import java.util.logging.Logger;

/** View class for the Market. */
public class MarketView implements at.vunfer.openrealms.presenter.MarketPresenter.View {
    private static final Logger LOGGER = Logger.getLogger(MarketView.class.getName());
    private static final String ERROR_MESSAGE = "Error displaying market";
    private static final String TAG = "MarketView";
    private final Runnable measureAndLayout =
            new Runnable() {
                @Override
                public void run() {
                    /*measure(
                            MeasureSpec.makeMeasureSpec(getWidth(), MeasureSpec.EXACTLY),
                            MeasureSpec.makeMeasureSpec(getHeight(), MeasureSpec.EXACTLY));
                    layout(getLeft(), getTop(), getRight(), getBottom());*/
                }
            };
    private Card selectedCard;

    private Context context;
    private LinearLayout marketView;

    public MarketView(Context context) {
        this.context = context;
        // Creating MarketView layout parameters
        ConstraintLayout.LayoutParams params =
                new ConstraintLayout.LayoutParams(
                        ConstraintLayout.LayoutParams.WRAP_CONTENT,
                        ConstraintLayout.LayoutParams.WRAP_CONTENT);

        // Centering the MarketView layout parameters in the middle of the game field
        params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
        params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        params.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
        params.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;

        // Inflating the MarketView layout with the created layout parameters
        this.marketView =
                (LinearLayout)
                        LayoutInflater.from(this.context).inflate(R.layout.market_view, null);
        this.marketView.setMinimumHeight(90);
        this.marketView.setLayoutParams(params);
    }

    /**
     * Show the market
     *
     * @param market the market to be displayed
     */
    public void showMarket(List<Card> market) {
        this.marketView.removeAllViews();
        for (Card card : market) {
            View marketCardView =
                    LayoutInflater.from(this.context).inflate(R.layout.card_view, null);

            ImageView cardImage = marketCardView.findViewById(R.id.card_image);
            TextView cardName = marketCardView.findViewById(R.id.card_description);
            TextView cardCost = marketCardView.findViewById(R.id.card_cost);

            // Set the image resource directly on the ImageView
            cardImage.setImageResource(card.getImageResource());

            cardName.setText(card.getName());
            cardCost.setText(String.valueOf(card.getCost()));

            // Add an OnClickListener to the card image view
            cardImage.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            selectedCard = card;
                            ((MainActivity) context).showCardDetails(v);
                        }
                    });

            this.marketView.addView(marketCardView);
        }
    }

    /**
     * Returns the selected card from the market
     *
     * @return the selected card from the market
     */
    public Card getSelectedCard() {
        return selectedCard;
    }

    /**
     * Displays the given market
     *
     * @param market The market to be displayed
     */
    @Override
    public void displayMarket(List<Card> market) {
        for (int i = 0; i < 5; i++) {
            Card card = new Card(this.context);

            card.getCardImage().setImageResource(R.drawable.emptycards);
            card.getCardImage().setLayoutParams(new ViewGroup.LayoutParams(250, 250));

            this.marketView.addView(card.getCardImage());
        }
    }

    /**
     * Returns the market view
     *
     * @return The market view
     */
    public LinearLayout getMarketView() {
        return marketView;
    }
}
