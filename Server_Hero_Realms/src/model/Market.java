/* Licensed under GNU GPL v3.0 (C) 2023 */
package model;


import java.util.ArrayList;

public class Market {
    private static Market INSTANCE;
    private static final int TOTAL_PURCHASABLE = 5;
    private static final String TAG = "PlayerCards";
    Deck<Card> marketDeck;
    Deck<Card> forPurchase;

    private Market() {}

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
                System.out.println("You have no more cards to draw for the market.");
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
}
