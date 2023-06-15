/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.presenter;

import android.util.Log;
import at.vunfer.openrealms.presenter.presenter_interfaces.CardPilePresenter;
import at.vunfer.openrealms.view.CardView;
import at.vunfer.openrealms.view.PlayedChampionsView;

public class PlayedChampionsPresenter extends CardPilePresenter {
    private final PlayedChampionsView playedChampionsView;

    public PlayedChampionsPresenter(PlayedChampionsView playedChampionsView) {
        this.playedChampionsView = playedChampionsView;
    }

    @Override
    public void addCardToView(CardView card) {
        listOfDisplayedCards.add(card);
        playedChampionsView.updateView(listOfDisplayedCards);
    }

    @Override
    public void removeCardFromView(CardView card) {
        listOfDisplayedCards.remove(card);
        playedChampionsView.updateView(listOfDisplayedCards);
    }

    public void expendChampion(CardView card) {
        Log.v("PlayedChampionsPresenter", "expendChampion" + card.getCard());
        playedChampionsView.expendChampion(card);
        playedChampionsView.updateView(listOfDisplayedCards);
    }

    public void resetChampion(CardView card) {
        Log.v("PlayedChampionsPresenter", "resetChampion" + card.getCard());
        playedChampionsView.resetChampion(card);
        playedChampionsView.updateView(listOfDisplayedCards);
    }
}
