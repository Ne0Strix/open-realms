/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.presenter;

import at.vunfer.openrealms.presenter.presenter_interfaces.CardPilePresenter;
import at.vunfer.openrealms.view.CardView;
import at.vunfer.openrealms.view.DeckView;

public class DeckPresenter extends CardPilePresenter {
    private final DeckView deckView;

    public DeckPresenter(DeckView deckView) {
        this.deckView = deckView;
    }

    @Override
    public void addCardToView(CardView card) {
        listOfDisplayedCards.add(card);
        deckView.updateView(listOfDisplayedCards);
    }

    @Override
    public void removeCardFromView(CardView card) {
        listOfDisplayedCards.remove(card);
        deckView.updateView(listOfDisplayedCards);
    }
}
