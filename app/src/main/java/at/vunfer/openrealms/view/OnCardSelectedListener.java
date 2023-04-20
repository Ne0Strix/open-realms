/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.view;

import android.util.Log;
import at.vunfer.openrealms.model.Card;
import at.vunfer.openrealms.model.PlayArea;
import at.vunfer.openrealms.presenter.TurnValuesPresenter;

public class OnCardSelectedListener implements OnCardSelectedListenerInterface {

    TurnValuesPresenter turnValuesPresenter;

    public OnCardSelectedListener(TurnValuesPresenter turnValuesPresenter) {
        this.turnValuesPresenter = turnValuesPresenter;
    }

    @Override
    public void onCardSelected(Card card, PlayArea playArea, HandView handView) {
        if (!playArea.getPlayedCards().contains(card)) {
            Log.v("UI", card.toString() + " ," + card.getId());
            playArea.playCard(card);
            turnValuesPresenter.updateTurnValuesView();
            ((HandView) handView).resetHand();
        }
    }
}
