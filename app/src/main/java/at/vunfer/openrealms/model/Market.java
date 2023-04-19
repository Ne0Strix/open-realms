/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.model;

import android.util.Log;
import java.util.ArrayList;
import java.util.List;

public class Market {
    private static Market INSTANCE;
    private static final int TOTAL_PURCHASABLE = 5;
    private static final String TAG = "PlayerCards";
    Deck<Card> marketDeck;
    Deck<Card> forPurchase;

    private List<Card> cards;

    public Market() {
        this.cards = new ArrayList<>();
    }

    public static Market getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Market();
        }
        return INSTANCE;
    }

    public ArrayList<Card> getForPurchase() {
        return forPurchase;
    }

    public int restock() {
        int restocked = 0;
        while (forPurchase.size() < TOTAL_PURCHASABLE) {
            try {
                forPurchase.add(marketDeck.drawRandom());
                restocked++;
            } catch (Exception e) {
                Log.v(TAG, "You have no more cards to draw for the market.");
            }
        }
        return restocked;
    }

    public Card purchase(Card card) {
        if (card == null || !forPurchase.contains(card)) {
            throw new IllegalArgumentException("Card is not to purchase.");
        }
        forPurchase.remove(card);
        return card;
    }
    /**
     * Get the list of cards in the market.
     *
     * @return The list of cards in the market.
     */
    public List<Card> getCards() {
        return cards;
    }

    /**
     * Set the list of cards in the market.
     *
     * @param cards The list of cards to set in the market.
     */
    public void setCards(List<Card> cards) {
        this.cards = cards;
    }

    /**
     * Add a card to the market.
     *
     * @param card The card to add to the market.
     */
    public void addCard(Card card) {
        cards.add(card);
    }
    /**
     * Remove a card from the market.
     *
     * @param card The card to remove from the market.
     */
    public void removeCard(Card card) {
        cards.remove(card);
    }

    /** Clear the market of all cards. */
    public void clear() {
        cards.clear();
    }
}
