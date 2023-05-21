/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.testng.Assert.assertTrue;

import at.vunfer.openrealms.network.DataKey;
import at.vunfer.openrealms.network.Message;
import at.vunfer.openrealms.network.MessageType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
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
        market = Market.getInstance();
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
    void testBuyCardNotEnoughCoins() {
        Card toBuy = player1.getPlayArea().getMarket().getForPurchase().get(0);
        Message message = new Message(MessageType.BUY_CARD);
        message.setData(DataKey.CARD_ID, Integer.toString(toBuy.getId()));
        message.setData(DataKey.CHEAT_ACTIVATE, Boolean.toString(false));

        int initialCoins = player1.getPlayArea().getTurnCoins();
        assertThrows(IllegalArgumentException.class, () -> player1.getPlayArea().buyCard(message));
        Assertions.assertFalse(
                player1.getPlayArea().getPlayerCards().getDiscardedCards().contains(toBuy));
        assertEquals(initialCoins, player1.getPlayArea().getTurnCoins());
    }

    @Test
    void testBuyCardInvalidCard() {
        Message message = new Message(MessageType.BUY_CARD);
        message.setData(DataKey.CARD_ID, "-1");
        message.setData(DataKey.CHEAT_ACTIVATE, Boolean.toString(true));

        assertThrows(IllegalArgumentException.class, () -> player1.getPlayArea().buyCard(message));
    }

    @AfterEach
    void tearDown() {
        playArea = null;
        playerCards = null;
        player1 = null;
    }
}
