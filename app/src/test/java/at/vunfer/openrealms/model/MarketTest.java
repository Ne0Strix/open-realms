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
}