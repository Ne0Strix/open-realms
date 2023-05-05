/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.presenter.presenter_interfaces;

import at.vunfer.openrealms.view.CardView;
import java.util.ArrayList;
import java.util.List;

public abstract class CardPilePresenter {
    protected List<CardView> listOfDisplayedCards;

    protected CardPilePresenter() {
        listOfDisplayedCards = new ArrayList<>();
    }

    public abstract void addCardToView(CardView card);

    public abstract void removeCardFromView(CardView card);

    public void addCardsToView(List<CardView> cards) {
        for (CardView card : cards) addCardToView(card);
    }

    public CardView findViewByCardId(int cardID) {
        for (CardView view : listOfDisplayedCards) {
            // if(view.getCard().getID == cardID) TODO: implement CardID
            return view;
        }
        return null;
    }

    public List<CardView> getListOfDisplayedCards() {
        return listOfDisplayedCards;
    }
}
