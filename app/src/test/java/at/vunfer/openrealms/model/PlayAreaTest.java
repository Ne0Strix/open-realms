/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.testng.Assert.assertTrue;

import at.vunfer.openrealms.model.effects.DamageEffect;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PlayAreaTest {

    private PlayArea playArea;
    private PlayerCards playerCards;
    private Player player1;
    private Market market;

    @BeforeEach
    void setUp() {
        player1 = PlayerFactory.createPlayer("Player 1");
        playerCards = new PlayerCards();
        playArea = new PlayArea(70, playerCards);
        playerCards.setDeckCards(playerCards.getOldTestDeck());
        market = Market.getInstance();
        market.setMarketDeck(market.getOldTestMarketDeck());
    }

    @Test
    void testGetHealth() {
        assertEquals(70, playArea.getHealth());
    }

    @Test
    void testGetTurnDamage() {
        assertEquals(0, playArea.getTurnDamage());
    }

    @Test
    void testGetTurnHealing() {
        assertEquals(0, playArea.getTurnHealing());
    }

    @Test
    void testGetPlayerCards() {
        assertEquals(playerCards, playArea.getPlayerCards());
    }

    @Test
    void testGetTurnCoins() {
        assertEquals(0, playArea.getTurnCoins());
    }

    @Test
    void testGetPlayedCards() {
        assertTrue(playArea.getPlayedCards().isEmpty());
    }

    @Test
    void testGetPlayedChampions() {
        assertTrue(playArea.getPlayedChampions().isEmpty());
    }

    @Test
    void testGetMarket() {
        assertEquals(market, playArea.getMarket());
    }

    @Test
    void testPlayCard() {
        Card card = playerCards.getHandCards().get(0);
        playArea.playCard(card);
        assertTrue(playArea.getPlayedCards().contains(card));
    }

    @Test
    void testResetTurnPool() {
        playArea.visitDamage(1);
        playArea.visitCoin(1);
        playArea.visitHealing(1);
        playArea.resetTurnPool();
        assertEquals(0, playArea.getTurnDamage());
        assertEquals(0, playArea.getTurnHealing());
        assertEquals(0, playArea.getTurnCoins());
    }

    @Test
    void testHeal() {
        int initialHealth = playArea.getHealth();
        playArea.heal(5);
        assertEquals(initialHealth + 5, playArea.getHealth());
    }

    @Test
    void testTakeDamage() {
        int initialHealth = playArea.getHealth();
        playArea.takeDamage(5);
        assertEquals(initialHealth - 5, playArea.getHealth());
    }

    @Test
    void testVisitDamage() {
        int initialTurnDamage = playArea.getTurnDamage();
        playArea.visitDamage(5);
        assertEquals(initialTurnDamage + 5, playArea.getTurnDamage());
    }

    @Test
    void testVisitCoin() {
        int initialTurnCoins = playArea.getTurnCoins();
        playArea.visitCoin(5);
        assertEquals(initialTurnCoins + 5, playArea.getTurnCoins());
    }

    @Test
    void testVisitHealing() {
        int initialTurnHealing = playArea.getTurnHealing();
        playArea.visitHealing(5);
        assertEquals(initialTurnHealing + 5, playArea.getTurnHealing());
    }

    @Test
    void testBuyCardTooPoor() {
        PlayArea playArea1 = player1.getPlayArea();
        Card toBuy = playArea1.getMarket().getForPurchase().get(0);
        assertThrows(IllegalArgumentException.class, () -> playArea1.buyCard(toBuy));
    }

    @Test
    void testBuyCard() {

        Card toBuy = player1.getPlayArea().getMarket().getForPurchase().get(0);

        player1.getPlayArea().visitCoin(10);
        player1.getPlayArea().buyCard(toBuy);

        assertTrue(player1.getPlayArea().getPlayerCards().getDiscardedCards().contains(toBuy));
    }

    @Test
    void testClearPlayedCards() {
        Card c = new Card("Test", 2, List.of(new DamageEffect(2)));
        playArea.getPlayedCards().add(c);

        playArea.clearPlayedCards();

        assertTrue(playArea.getPlayedCards().isEmpty());
        assertTrue(playArea.getPlayerCards().getDiscardedCards().contains(c));
    }

    @AfterEach
    void tearDown() {
        playArea = null;
        playerCards = null;
        player1 = null;
    }
}
