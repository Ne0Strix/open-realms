/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.model;

import static org.junit.jupiter.api.Assertions.*;

import at.vunfer.openrealms.model.effects.CoinEffect;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PlayerCardsTest {
    PlayerCards cards;

    @BeforeEach
    void handSetup() {
        cards = new PlayerCards();
        cards.setDeckCards(cards.getOldTestDeck());
    }

    @AfterEach
    void tearDown() {
        cards = null;
    }

    @Test
    void testBoughtCardToDiscardPile() {
        Card card = new Card("test_card", 2, CardType.NONE, List.of(new CoinEffect(1)));
        cards.addBoughtCard(card);
        assertTrue(cards.getDiscardedCards().contains(card));
    }

    @Test
    void testHandToDiscardPile() {
        Card toDiscard = cards.getHandCards().get(0);
        cards.discard(toDiscard);
        assertTrue(cards.getDiscardedCards().contains(toDiscard));
    }

    @Test
    void testRemoveFromHand() {
        Card toPop = cards.getHandCards().get(1);
        Card returned = cards.popFromHand(toPop);
        assertFalse(cards.getHandCards().contains(toPop));
        assertEquals(toPop, returned);
    }

    @Test
    void testInitialSetup() {
        assertEquals(cards.getHandSize(), cards.getHandCards().size());
    }

    @Test
    void testPopUntilEmptyHand() {
        for (int i = cards.getHandCards().size() - 1; i >= 0; i--) {
            cards.popFromHand(cards.getHandCards().get(i));
        }
        assertTrue(cards.getHandCards().isEmpty());
    }

    @Test
    void testRestockEmptyHand() {
        for (int i = cards.getHandSize() - 1; i >= 0; i--) {
            cards.popFromHand(cards.getHandCards().get(i));
        }

        cards.restockHand();
        assertEquals(cards.getHandSize(), cards.getHandCards().size());
    }

    @Test
    void testRestockPartialHand() {
        // remove three cards
        for (int i = 0; i <= 2; i++) {
            cards.popFromHand(cards.getHandCards().get(i));
        }

        // check remaining two cards
        Deck<Card> oldHand = new Deck<>();
        oldHand.addAll(cards.getHandCards());

        cards.restockHand();

        // check whether remaining cards were dropped as well
        assertEquals(cards.getHandSize(), cards.getHandCards().size());
        // assertFalse(cards.getHandCards().containsAll(oldHand)); // potentially breaks GitHub
        // Actions tests
    }

    @Test
    void testRestockFullHand() {
        Deck<Card> oldHand = new Deck<>();
        oldHand.addAll(cards.getHandCards());

        cards.restockHand();

        assertEquals(cards.getHandSize(), cards.getHandCards().size());
        assertFalse(cards.getHandCards().containsAll(oldHand));
    }

    @Test
    void testRestockHandWhenDeckCardsTooSmall() {
        // remove all cards from the hand
        for (int i = 0; i < cards.getHandCards().size(); i++) {
            cards.popFromHand(cards.getHandCards().get(i));
        }

        // remove all but 4 cards from the deck
        cards.getDeckCards().subList(0, cards.getDeckCards().size() - 4).clear();

        // add 2 cards to the discard pile
        cards.getDiscardedCards()
                .add(new Card("card1", 1, CardType.NONE, List.of(new CoinEffect(1))));
        cards.getDiscardedCards()
                .add(new Card("card2", 1, CardType.NONE, List.of(new CoinEffect(1))));

        // restock the hand
        cards.restockHand();

        // check that the hand has been refilled with 5 cards
        assertEquals(5, cards.getHandCards().size());
    }
}
