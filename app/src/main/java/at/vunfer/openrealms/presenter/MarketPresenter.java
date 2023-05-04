/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.presenter;

import at.vunfer.openrealms.view.CardView;
import at.vunfer.openrealms.view.MarketView;
import java.util.List;
import java.util.logging.Logger;

/** Presenter for the MarketView. */
public class MarketPresenter {
    private static final Logger LOGGER = Logger.getLogger(MarketPresenter.class.getName());
    private MarketView marketView;

    public MarketPresenter(MarketView view) {
        marketView = view;
    }

    public void removeCardFromMarketView(CardView card) {
        marketView.getDisplayedCards().remove(card);
        marketView.updateMarket();
    }

    public void addCardToMarketView(CardView card) {
        marketView.getDisplayedCards().add(card);
        marketView.updateMarket();
    }

    public void addCardsToMarketView(List<CardView> cards) {
        for (CardView card : cards) {
            marketView.getDisplayedCards().add(card);
        }
        marketView.updateMarket();
    }

    public CardView findViewByCardId(int cardID) {
        for (CardView view : marketView.getDisplayedCards()) {
            // if(view.getCard().getID == cardID) TODO: implement CardID
            return view;
        }
        return null;
    }
}
