/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.model;

import static org.junit.jupiter.api.Assertions.*;

import at.vunfer.openrealms.model.effects.CoinEffect;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MarketTest {
    Market market;

    @BeforeEach
    public void setUp() {
        market = Market.getInstance();
        market.setMarketDeck(market.getOldTestMarketDeck());
    }

    @AfterEach
    public void teardown() {
        market = null;
    }

    @Test
    void testBuyNonexistingCard() {
        assertThrows(IllegalArgumentException.class, () -> market.purchase(null));
        Card cardToBuy = new Card("Test", 5, List.of(new CoinEffect(5)));
        assertThrows(IllegalArgumentException.class, () -> market.purchase(cardToBuy));
    }

    @Test
    void testPurchaseExistingCard() {
        Card toBuy = market.getForPurchase().get(0);
        assertEquals(toBuy, market.purchase(toBuy));
        assertFalse(market.getForPurchase().contains(toBuy));
    }

    @Test
    void testModifyForPurchase() {
        Card newCard = new Card("Test", 5, List.of(new CoinEffect(5)));

        market.addCard(newCard);
        assertTrue(market.getForPurchase().contains(newCard));

        market.removeCard(newCard);
        assertFalse(market.getForPurchase().contains(newCard));
    }

    @Test
    void testSetAndResetForPurchase() {
        Card c1 = new Card("Test", 5, List.of(new CoinEffect(5)));
        Card c2 = new Card("Test", 5, List.of(new CoinEffect(5)));
        Card c3 = new Card("Test", 5, List.of(new CoinEffect(5)));

        Deck<Card> newForPurchase = new Deck<>();
        newForPurchase.add(c1);
        newForPurchase.add(c2);
        newForPurchase.add(c3);
        Deck<Card> oldForPurchase = (Deck<Card>) market.getForPurchase();

        market.setCards(newForPurchase);
        assertEquals(newForPurchase, market.getForPurchase());

        market.clear();
        assertTrue(market.getForPurchase().isEmpty());

        market.setCards(oldForPurchase);
    }

    @Test
    void testNewToPurchase() {
        Card c1 = new Card("Test", 5, List.of(new CoinEffect(5)));
        Card c2 = new Card("Test", 5, List.of(new CoinEffect(5)));
        Card c3 = new Card("Test", 5, List.of(new CoinEffect(5)));

        Deck<Card> newToPurchase = new Deck<>();
        newToPurchase.add(c1);
        newToPurchase.add(c2);
        newToPurchase.add(c3);

        market.setNewToPurchase(newToPurchase);
        assertEquals(newToPurchase, market.getNewToPurchase());
    }
}
