/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.presenter;

import at.vunfer.openrealms.presenter.presenter_interfaces.CardPilePresenter;
import at.vunfer.openrealms.view.CardView;
import at.vunfer.openrealms.view.DiscardPileView;

public class DiscardPilePresenter extends CardPilePresenter {
    private final DiscardPileView discardPileView;

    public DiscardPilePresenter(DiscardPileView discardPileView) {
        this.discardPileView = discardPileView;
    }

    @Override
    public void addCardToView(CardView card) {
        listOfDisplayedCards.add(card);
        discardPileView.updateView(listOfDisplayedCards);
    }

    @Override
    public void removeCardFromView(CardView card) {
        listOfDisplayedCards.remove(card);
        discardPileView.updateView(listOfDisplayedCards);
    }
}
