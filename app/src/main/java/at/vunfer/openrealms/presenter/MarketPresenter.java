/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.presenter;

import at.vunfer.openrealms.MainActivity;
import at.vunfer.openrealms.model.Card;
import at.vunfer.openrealms.model.Deck;
import at.vunfer.openrealms.model.Market;
import at.vunfer.openrealms.view.MarketView;
import java.util.List;
import java.util.logging.Logger;

public class MarketPresenter {
    private static final Logger LOGGER = Logger.getLogger(MarketPresenter.class.getName());
    private View view;
    private final Market market;
    private MarketView marketView;

    public MarketPresenter(MainActivity mainActivity) {
        this.market = Market.getInstance();
        this.marketView = marketView;
    }

    public void onMarketChanged(Deck<Card> cards) {
        market.setCards(cards);
        marketView.showMarket(cards);
    }

    public void onCardPurchased(Card card) {
        market.removeCard(card);
    }

    public void attachView(View view) {
        this.view = view;
    }

    public void detachView() {
        this.view = null;
    }

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

    public List<Card> getMarketData() {
        // Retrieve the market data from a data source
        return market.getCards();
    }

    public interface View {
        void displayMarket(List<Card> market);
    }
}
