/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.presenter;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import at.vunfer.openrealms.model.Card;
import at.vunfer.openrealms.view.PlayAreaView;

public class PlayAreaPresenter {
    private static final Logger LOGGER = Logger.getLogger(PlayAreaPresenter.class.getName());
    private static final String TAG = "PlayAreaPresenter";

    private PlayAreaView view;
    private ArrayList<Card> cards = new ArrayList<>();

    public PlayAreaPresenter(PlayAreaView view) {
        this.view = view;
    }

    public void setText(String text) {
        try {
            view.setText(text);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, TAG + ": Error setting text: " + e.getMessage());
        }
    }

    public void addCard(Card card) {
        cards.add(card);
    }

    public void updateView(String text) {
        setText(text);
        view.invalidate(); // Forces a redraw of the view
    }
}
