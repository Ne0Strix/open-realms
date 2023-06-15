/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.presenter;

import at.vunfer.openrealms.presenter.presenter_interfaces.CardPilePresenter;
import at.vunfer.openrealms.view.CardView;
import at.vunfer.openrealms.view.MarketView;

/** Presenter for the MarketView. */
public class MarketPresenter extends CardPilePresenter {
    private MarketView marketView;

    public MarketPresenter(MarketView view) {
        marketView = view;
    }

    @Override
    public void addCardToView(CardView card) {
        listOfDisplayedCards.add(card);
        marketView.updateView(listOfDisplayedCards);
    }

    @Override
    public void removeCardFromView(CardView card) {
        listOfDisplayedCards.remove(card);
        marketView.updateView(listOfDisplayedCards);
    }
}
