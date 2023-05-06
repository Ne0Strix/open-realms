/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.presenter;

import at.vunfer.openrealms.presenter.presenter_interfaces.CardPilePresenter;
import at.vunfer.openrealms.view.CardView;
import at.vunfer.openrealms.view.HandView;

public class HandPresenter extends CardPilePresenter {
    private final HandView handView;

    public HandPresenter(HandView handView) {
        this.handView = handView;
    }

    @Override
    public void addCardToView(CardView card) {
        listOfDisplayedCards.add(card);
        handView.updateView(listOfDisplayedCards);
    }

    @Override
    public void removeCardFromView(CardView card) {
        listOfDisplayedCards.remove(card);
        handView.updateView(listOfDisplayedCards);
    }
}
