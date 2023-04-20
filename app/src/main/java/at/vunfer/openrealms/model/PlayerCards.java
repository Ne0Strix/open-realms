/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.model;

import android.content.Context;
import java.util.List;

public class PlayerCards {
    private static final String TAG = "PlayerCards";
    private Deck<Card> handCards;
    private Deck<Card> deckCards;
    private Deck<Card> discardedCards;
    private Context context;

    private static final int HANDSIZE = 5;

    public PlayerCards(Context context) {
        List<Effect> effects = List.of(new DamageEffect(3), new CoinEffect(4));

        this.handCards = new Deck<Card>();
        this.deckCards = new Deck<Card>();
        this.discardedCards = new Deck<Card>();
        this.deckCards.add(new Card("Dagger", 0, List.of(new DamageEffect(1)), "", context));
        this.deckCards.add(new Card("Heal", 0, List.of(new HealingEffect(2)), "", context));
        this.deckCards.add(new Card("Ruby ", 0, List.of(new CoinEffect(2)), "", context));
        for (int i = 0; i < 7; i++) {
            this.deckCards.add(new Card("Coin", 0, List.of(new CoinEffect(1)), "", context));
        }
        while (handCards.size() < HANDSIZE) {
            handCards.add(deckCards.drawRandom());
        }
    }

    public Deck<Card> getHandCards() {
        return handCards;
    }

    public void discard(Card card) throws IllegalArgumentException {
        discardedCards.add(handCards.draw(card));
    }

    public void discardAll() throws IllegalArgumentException {
        while (!handCards.isEmpty()) discardedCards.add(handCards.draw(handCards.get(0)));
    }

    public void sendToDiscard(Deck<Card> cards) throws IllegalArgumentException {
        while (!cards.isEmpty()) discardedCards.add(cards.draw(cards.get(0)));
    }

    public void addBoughtCard(Card card) {
        discardedCards.add(card);
    }

    public Card popFromHand(Card card) {
        return handCards.draw(card);
    }

    public void restockHand() {
        if (deckCards.size() < HANDSIZE) {
            handCards.addAll(deckCards);
            deckCards.clear();

            deckCards.addAll(discardedCards);
            discardedCards.clear();
        }

        while (handCards.size() < HANDSIZE) {
            handCards.add(deckCards.drawRandom());
        }
    }
}
