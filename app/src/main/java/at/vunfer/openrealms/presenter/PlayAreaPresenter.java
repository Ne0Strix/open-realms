/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.presenter;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import at.vunfer.openrealms.model.Card;
import at.vunfer.openrealms.view.PlayAreaView;

/**
 * Presenter class for the PlayAreaView. Handles logic for adding cards and updating the view.
 */

public class PlayAreaPresenter {
    private static final Logger LOGGER = Logger.getLogger(PlayAreaPresenter.class.getName());
    private static final String TAG = "PlayAreaPresenter";

    private PlayAreaView view;
    private ArrayList<Card> cards = new ArrayList<>();

    /**
     * Constructor for the PlayAreaPresenter.
     * @param view The PlayAreaView to associate with this presenter.
     */
    public PlayAreaPresenter(PlayAreaView view) {
        this.view = view;
    }

    /**
     * Sets the text of the associated PlayAreaView.
     *
     * @param text The text to set.
     */
    public void setText(String text) {
        try {
            view.setText(text);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, TAG + ": Error setting text: " + e.getMessage());
        }
    }

    /**
     * Adds a card to the ArrayList of cards on the play area.
     *
     * @param card The card to add.
     */
    public void addCard(Card card) {
        cards.add(card);
    }

    /**
     * Updates the view by setting the text and forcing a redraw of the view.
     *
     * @param text The text to set.
     */
    public void updateView(String text) {
        setText(text);
        view.invalidate(); // Forces a redraw of the view
    }
}
