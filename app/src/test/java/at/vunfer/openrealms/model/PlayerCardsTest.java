/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Iterator;
import java.util.List;

import org.junit.After;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PlayerCardsTest {
    PlayerCards cards;

    @BeforeEach
    void handSetup(){
        cards = new PlayerCards();
    }

    @AfterEach
    void tearDown(){
        cards = null;
    }

    @Test
    void testBoughtCardToDiscardPile() {
        Card card = new Card("test_card", 2, List.of(new CoinEffect(1)));
        cards.addBoughtCard(card);
        assertTrue(cards.getDiscardedCards().contains(card));
    }

    @Test
    void testHandToDiscardPile(){
        Card toDiscard = cards.getHandCards().get(0);
        cards.discard(toDiscard);
        assertTrue(cards.getDiscardedCards().contains(toDiscard));
    }

    @Test
    void testRemoveFromHand(){
        Card toPop = cards.getHandCards().get(1);
        Card returned = cards.popFromHand(toPop);
        assertFalse(cards.getHandCards().contains(toPop));
        assertEquals(toPop, returned);
    }

    @Test
    void testInitialSetup(){
        assertEquals(cards.getHandsize(), cards.getHandCards().size());
    }

    @Test
    void testPopUntilEmptyHand(){
        for (int i = cards.getHandCards().size() - 1; i >= 0; i--) {
            cards.popFromHand(cards.getHandCards().get(i));
        }
        assertTrue(cards.getHandCards().isEmpty());
    }

    @Test
    void testRestockEmptyHand(){
        for (int i = cards.getHandsize()-1; i >= 0; i--) {
            cards.popFromHand(cards.getHandCards().get(i));
        }

        cards.restockHand();
        assertEquals(cards.getHandsize(), cards.getHandCards().size());
    }

    @Test
    void testRestockPartialHand(){
        // remove three cards
        for (int i = 0; i <= 2; i++) {
            cards.popFromHand(cards.getHandCards().get(i));
        }

        // check remaining two cards
        Deck<Card> oldHand = new Deck<Card>();
        oldHand.addAll(cards.getHandCards());

        cards.restockHand();

        // check whether remaining cards were dropped as well
        assertEquals(cards.getHandsize(), cards.getHandCards().size());
        assertFalse(cards.getHandCards().containsAll(oldHand));
    }

    @Test
    void testRestockFullHand(){
        Deck<Card> oldHand = new Deck<Card>();
        oldHand.addAll(cards.getHandCards());

        cards.restockHand();

        assertEquals(cards.getHandsize(), cards.getHandCards().size());
        assertFalse(cards.getHandCards().containsAll(oldHand));
    }
}
