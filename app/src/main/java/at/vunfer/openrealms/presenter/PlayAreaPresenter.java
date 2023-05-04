/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.presenter;

import at.vunfer.openrealms.view.CardView;
import at.vunfer.openrealms.view.PlayAreaView;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/** Presenter class for the PlayAreaView. Handles logic for adding cards and updating the view. */
public class PlayAreaPresenter {
    private static final Logger LOGGER = Logger.getLogger(PlayAreaPresenter.class.getName());
    private static final String TAG = "PlayAreaPresenter";

    private PlayAreaView playAreaView;
    private ArrayList<CardView> cards = new ArrayList<>();

    /**
     * Constructor for the PlayAreaPresenter.
     *
     * @param view The PlayAreaView to associate with this presenter.
     */
    public PlayAreaPresenter(PlayAreaView view) {
        this.playAreaView = view;
    }

    public void removeCardFromPlayArea(CardView card) {
        playAreaView.getDisplayedCards().remove(card);
        playAreaView.updatePlayArea();
    }

    public void addCardToPlayArea(CardView card) {
        playAreaView.getDisplayedCards().add(card);
        playAreaView.updatePlayArea();
    }

    public void addCardsToPlayArea(List<CardView> cards) {
        for (CardView card : cards) {
            playAreaView.getDisplayedCards().add(card);
        }
        playAreaView.updatePlayArea();
    }

    public CardView findViewByCardId(int cardID) {
        for (CardView view : playAreaView.getDisplayedCards()) {
            // if(view.getCard().getID == cardID) TODO: implement CardID
            return view;
        }
        return null;
    }
}
