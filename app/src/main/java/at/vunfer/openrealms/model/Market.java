/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.model;

import at.vunfer.openrealms.model.effects.CoinEffect;
import at.vunfer.openrealms.model.effects.DamageEffect;
import at.vunfer.openrealms.model.effects.HealingEffect;
import java.util.ArrayList;
import java.util.List;

public class Market {
    private static Market marketInstance;
    private static final int TOTAL_PURCHASABLE = 5;
    private static final String TAG = "Market";
    Deck<Card> marketDeck;
    Deck<Card> forPurchase;
    Deck<Card> newToPurchase;

    private Market() {
        marketDeck = new Deck<>();
        forPurchase = new Deck<>();
        newToPurchase = new Deck<>();
    }

    public void setMarketDeck(Deck<Card> marketDeck) {
        this.marketDeck.addAll(marketDeck);
        restock();
    }

    /**
     * Get an instance of the market.
     *
     * @return The market instance.
     */
    public static Market getInstance() {
        if (marketInstance == null) {
            marketInstance = new Market();
        }
        return marketInstance;
    }

    /**
     * Get the list of cards available for purchase.
     *
     * @return The list of cards for purchase.
     */
    public ArrayList<Card> getForPurchase() {
        return forPurchase;
    }

    /**
     * Restock the market with cards from the market deck.
     *
     * @return The number of cards restocked.
     */
    public int restock() {
        int restocked = 0;
        newToPurchase = new Deck<>();
        while (forPurchase.size() < TOTAL_PURCHASABLE && !marketDeck.isEmpty()) {
            Card card = marketDeck.drawRandom();
            if (card != null) {
                forPurchase.add(card);
                newToPurchase.add(card);
                restocked++;
            } else {
                break;
            }
        }
        return restocked;
    }

    /**
     * Purchase a card from the market.
     *
     * @param card The card to purchase.
     * @return The purchased card.
     * @throws IllegalArgumentException if the card is not available for purchase.
     */
    public Card purchase(Card card) {
        if (card == null || !forPurchase.contains(card)) {
            throw new IllegalArgumentException("Card is not to purchase.");
        }
        forPurchase.remove(card);
        return card;
    }

    public Deck<Card> getNewToPurchase() {
        return newToPurchase;
    }

    public void setNewToPurchase(Deck<Card> newToPurchase) {
        this.newToPurchase = newToPurchase;
    }

    /**
     * Set the list of cards in the market.
     *
     * @param cards The list of cards to set in the market.
     */
    public void setCards(Deck<Card> cards) {
        this.forPurchase = cards;
    }

    /**
     * Add a card to the market.
     *
     * @param card The card to add to the market.
     */
    public void addCard(Card card) {
        forPurchase.add(card);
    }

    /**
     * Remove a card from the market.
     *
     * @param card The card to remove from the market.
     */
    public void removeCard(Card card) {
        forPurchase.remove(card);
    }

    /** Clear the market of all cards. */
    public void clear() {
        forPurchase.clear();
    }

    public Deck<Card> getOldTestMarketDeck() {
        Deck<Card> testMarketDeck = new Deck<>();
        testMarketDeck.add(
                new Card(
                        "Testcard1",
                        3,
                        Faction.NONE,
                        List.of(new DamageEffect(1), new HealingEffect(1), new CoinEffect(1))));
        testMarketDeck.add(
                new Card(
                        "Testcard2",
                        3,
                        Faction.NONE,
                        List.of(new DamageEffect(1), new HealingEffect(1), new CoinEffect(1))));
        testMarketDeck.add(
                new Card(
                        "Testcard3",
                        3,
                        Faction.NONE,
                        List.of(new DamageEffect(1), new HealingEffect(1), new CoinEffect(1))));
        testMarketDeck.add(
                new Card(
                        "Testcard4",
                        3,
                        Faction.NONE,
                        List.of(new DamageEffect(1), new HealingEffect(1), new CoinEffect(1))));
        testMarketDeck.add(
                new Card(
                        "Testcard5",
                        3,
                        Faction.NONE,
                        List.of(new DamageEffect(1), new HealingEffect(1), new CoinEffect(1))));
        testMarketDeck.add(
                new Card(
                        "Testcard6",
                        3,
                        Faction.NONE,
                        List.of(new DamageEffect(1), new HealingEffect(1), new CoinEffect(1))));
        return testMarketDeck;
    }
}
