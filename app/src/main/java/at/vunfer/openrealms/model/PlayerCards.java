/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.model;

import java.util.List;

public class PlayerCards {
    private List<Card> handCards;
    private List<Card> deckCards;
    private List<Card> discardedCards;
    private Market market;

    public PlayerCards(
            List<Card> handCards,
            List<Card> deckCards,
            List<Card> discardedCards,
            Market market) {
        this.handCards = handCards;
        this.deckCards = deckCards;
        this.discardedCards = discardedCards;
        this.market = market;
    }

    public List<Card> getHandCards() {
        return handCards;
    }

    public void setHandCards(List<Card> handCards) {
        this.handCards = handCards;
    }

    public List<Card> getDeckCards() {
        return deckCards;
    }

    public void setDeckCards(List<Card> deckCards) {
        this.deckCards = deckCards;
    }

    public List<Card> getDiscardedCards() {
        return discardedCards;
    }

    public void setDiscardedCards(List<Card> discardedCards) {
        this.discardedCards = discardedCards;
    }

    public Market getMarket() {
        return market;
    }

    public void setMarket(Market market) {
        this.market = market;
    }

    public Card draw() {
        return null;
    }

    public Card discard() {
        return null;
    }

    public Card playCard(Card card) {
        if (handCards.contains(card)) {
            return card;
        }
        return null;
    }

    public Card buyCard(Card card) {
        return null;
    }
}
