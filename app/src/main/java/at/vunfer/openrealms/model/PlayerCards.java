/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.model;

import android.util.Log;
import at.vunfer.openrealms.model.effects.CoinEffect;
import at.vunfer.openrealms.model.effects.DamageEffect;
import at.vunfer.openrealms.model.effects.HealingEffect;
import java.util.List;

public class PlayerCards {
    private static final String TAG = "PlayerCards";
    private final Deck<Card> handCards;
    private final Deck<Card> deckCards;
    private final Deck<Card> discardedCards;

    /** The maximum size of the hand. */
    private static final int HANDSIZE = 5;

    /**
     * Constructor for a new player's cards object. Initializes the hand, deck and discarded decks
     * with the necessary cards.
     */
    public PlayerCards() {
        this.handCards = new Deck<>();
        this.deckCards = new Deck<>();
        this.discardedCards = new Deck<>();
    }

    public void setDeckCards(Deck<Card> deckCards) {
        this.deckCards.addAll(deckCards);

        for (Card c : deckCards) {
            Log.i(TAG, "Added card " + c.getName() + " to deck.");
        }
        while (handCards.size() < HANDSIZE) {
            handCards.add(deckCards.drawRandom());
            Log.i(TAG, "Added random card + to hand.");
        }
    }

    /**
     * Returns the player's hand cards.
     *
     * @return The player's hand cards.
     */
    public Deck<Card> getHandCards() {
        return handCards;
    }

    /**
     * Returns the player's discarded cards.
     *
     * @return The player's discarded cards.
     */
    public Deck<Card> getDiscardedCards() {
        return discardedCards;
    }

    /**
     * Returns the player's deck cards.
     *
     * @return The player's deck cards.
     */
    public Deck<Card> getDeckCards() {
        return deckCards;
    }

    /**
     * Returns the maximum size of the player's hand.
     *
     * @return The maximum size of the player's hand.
     */
    public static int getHandSize() {
        return HANDSIZE;
    }

    /**
     * Discards the given card from the player's hand and adds it to the discarded cards.
     *
     * @param card The card to be discarded.
     * @throws IllegalArgumentException if the given card is not in the player's hand.
     */
    public void discard(Card card) throws IllegalArgumentException {
        discardedCards.add(handCards.draw(card));
        Log.i(TAG, "Discarded card " + card.getName() + " from hand.");
        for (Card c : discardedCards) {
            Log.i(TAG, "All Discarded card in discard" + c.getName());
        }
    }

    /**
     * Adds the given card to the discarded cards.
     *
     * @param card The card to be added to the discarded cards.
     */
    public void addBoughtCard(Card card) {
        discardedCards.add(card);
        Log.i(TAG, "Added card " + card.getName() + " to discarded cards.");
        for (Card c : discardedCards) {
            Log.i(TAG, "All Discarded card in adBoughtCard" + c.getName());
        }
    }

    /**
     * Removes the given card from the player's hand and returns it.
     *
     * @param card The card to be removed from the player's hand.
     * @return The card that was removed.
     */
    public Card popFromHand(Card card) {
        return handCards.draw(card);
    }

    /** Restocks the player's hand */
    public Deck<Card> restockHand() {
        Deck<Card> restockedFromDiscarded = null;
        Log.i(TAG, "Restocking hand.");
        Log.i(TAG, "Discarding all cards from hand.");
        for (int i = this.handCards.size() - 1; i >= 0; i--) {
            Log.i("PlayerCards", "Loop 1");
            discardedCards.add(this.popFromHand(this.getHandCards().get(i)));
            // Log.i(TAG, "Discarded card " + this.getHandCards().get(i).getName() + " from hand.");
        }
        Log.i("PlayerCards", "1");

        if (deckCards.size() < HANDSIZE) {
            Log.i(TAG, "Deck is empty. Restocking deck.");
            handCards.addAll(deckCards);
            deckCards.clear();

            deckCards.addAll(discardedCards);

            restockedFromDiscarded = new Deck<>();
            restockedFromDiscarded.addAll(discardedCards);
            discardedCards.clear();
        }

        Log.i(TAG, "Drawing cards from deck.");
        while (handCards.size() < HANDSIZE) {
            handCards.add(deckCards.drawRandom());
            Log.i(TAG, "Added random card to hand.");
        }
        return restockedFromDiscarded;
    }

    public Deck<Card> getOldTestDeck() {
        Deck<Card> testDeck = new Deck<>();
        testDeck.add(new Card("Dagger", 0, List.of(new DamageEffect(1))));
        testDeck.add(new Card("Shortsword", 0, List.of(new HealingEffect(2))));
        testDeck.add(new Card("Ruby ", 0, List.of(new CoinEffect(2))));
        for (int i = 0; i < 7; i++) {
            testDeck.add(new Card("Coin", 0, List.of(new CoinEffect(1))));
        }
        return testDeck;
    }
}
