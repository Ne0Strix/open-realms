/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.presenter;

import android.content.Context;

import at.vunfer.openrealms.model.Card;
import at.vunfer.openrealms.model.Deck;
import at.vunfer.openrealms.model.Market;
import at.vunfer.openrealms.view.MarketView;
import java.util.List;
import java.util.logging.Logger;

/**
 * Presenter for the MarketView.
 */
public class MarketPresenter {
    private static final Logger LOGGER = Logger.getLogger(MarketPresenter.class.getName());
    private View view;
    private final Market market;
    private MarketView marketView;

    /**
     * Constructor for MarketPresenter.
     *
     * @param context The context of the application.
     */
    public MarketPresenter(Context context) {
        this.market = Market.getInstance();
    }

    /**
     * Called when the market has changed.
     *
     * @param cards The new deck of cards in the market.
     */
    public void onMarketChanged(Deck<Card> cards) {
        market.setCards(cards);
        marketView.showMarket(cards);
    }

    /**
     * Called when a card has been purchased from the market.
     *
     * @param card The card that was purchased.
     */
    public void onCardPurchased(Card card) {
        market.removeCard(card);
    }

    /**
     * Attaches a view to the presenter.
     *
     * @param view The view to attach.
     */
    public void attachView(View view) {
        this.view = view;
    }

    /**
     * Detaches the view from the presenter.
     */
    public void detachView() {
        this.view = null;
    }

    /**
     * Displays the market to the user.
     *
     * @param market The list of cards in the market.
     */
    public void displayMarket(List<Card> market) {
        try {
            // Retrieve the market data from a data source
            List<Card> marketData = getMarketData();

            // Pass the market data to the view to display
            view.displayMarket(marketData);
        } catch (Exception e) {
            LOGGER.severe("Error displaying market: " + e.getMessage());
        }
    }

    /**
     * Retrieves the market data from the Market model.
     *
     * @return The list of cards in the market.
     */
    public List<Card> getMarketData() {
        // Retrieve the market data from a data source
        return market.getCards();
    }

    /**
     * Interface for the MarketPresenter view.
     */
    public interface View {
        /**
         * Displays the market to the user.
         *
         * @param market The list of cards in the market.
         */
        void displayMarket(List<Card> market);
    }
}
