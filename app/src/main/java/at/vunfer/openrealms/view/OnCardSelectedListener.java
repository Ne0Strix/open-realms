package at.vunfer.openrealms.view;

import android.content.Context;

import at.vunfer.openrealms.model.Card;
import at.vunfer.openrealms.model.PlayArea;
import at.vunfer.openrealms.presenter.TurnValuesPresenter;

public class OnCardSelectedListener implements OnCardSelectedListenerInterface{

    TurnValuesPresenter turnValuesPresenter;

    public OnCardSelectedListener(TurnValuesPresenter turnValuesPresenter) {
        this.turnValuesPresenter = turnValuesPresenter;
    }

    @Override
    public void onCardSelected(Card card, PlayArea playArea) {
        playArea.playCard(card);
        turnValuesPresenter.updateTurnValuesView();
    }
}
