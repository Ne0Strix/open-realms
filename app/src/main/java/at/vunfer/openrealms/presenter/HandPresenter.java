/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.presenter;

import at.vunfer.openrealms.view.CardView;
import at.vunfer.openrealms.view.HandView;
import java.util.ArrayList;
import java.util.List;

public class HandPresenter {

    private final List<CardView> cards = new ArrayList<>();
    private final HandView handView;

    public HandPresenter(HandView handView) {
        this.handView = handView;
    }

    public List<CardView> getCards() {
        return this.cards;
    }

    private void addCard(CardView card) {
        this.handView.addView(card);
    }

    public void removeCardFromHandView(CardView card) {
        handView.getDisplayedCards().remove(card);
        handView.updateHand();
    }

    public void addCardToHandView(CardView card) {
        handView.getDisplayedCards().add(card);
        handView.updateHand();
    }

    public void addCardsToHandView(List<CardView> cards) {
        for (CardView card : cards) {
            handView.getDisplayedCards().add(card);
        }
        handView.updateHand();
    }

    public CardView findViewByCardId(int cardID) {
        for (CardView view : handView.getDisplayedCards()) {
            // if(view.getCard().getID == cardID) TODO: implement CardID
            return view;
        }
        return null;
    }
}
