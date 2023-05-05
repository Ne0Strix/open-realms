/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.presenter;

import at.vunfer.openrealms.presenter.presenter_interfaces.CardPilePresenter;
import at.vunfer.openrealms.view.CardView;
import at.vunfer.openrealms.view.PlayAreaView;
import java.util.logging.Logger;

/** Presenter class for the PlayAreaView. Handles logic for adding cards and updating the view. */
public class PlayAreaPresenter extends CardPilePresenter {
    private static final Logger LOGGER = Logger.getLogger(PlayAreaPresenter.class.getName());
    private static final String TAG = "PlayAreaPresenter";

    private PlayAreaView playAreaView;

    /**
     * Constructor for the PlayAreaPresenter.
     *
     * @param view The PlayAreaView to associate with this presenter.
     */
    public PlayAreaPresenter(PlayAreaView view) {
        this.playAreaView = view;
    }

    @Override
    public void addCardToView(CardView card) {
        listOfDisplayedCards.add(card);
        playAreaView.updateView(listOfDisplayedCards);
    }

    @Override
    public void removeCardFromView(CardView card) {
        listOfDisplayedCards.remove(card);
        playAreaView.updateView(listOfDisplayedCards);
    }
}
