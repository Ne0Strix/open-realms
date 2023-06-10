/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import at.vunfer.openrealms.model.effects.CoinEffect;
import at.vunfer.openrealms.model.effects.DamageEffect;
import at.vunfer.openrealms.network.DataKey;
import at.vunfer.openrealms.network.Message;
import at.vunfer.openrealms.network.MessageType;
import java.util.List;
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
    void testPlayCardWithOneSynergy() {
        Card c1 =
                new Card(
                        "Test1",
                        0,
                        Faction.IMPERIAL,
                        List.of(new CoinEffect(1)),
                        List.of(new DamageEffect(1)));
        Card c2 = new Card(c1);
        Card c3 =
                new Card(
                        "Test3",
                        0,
                        Faction.GUILD,
                        List.of(new CoinEffect(1)),
                        List.of(new DamageEffect(1)));

        playerCards.getHandCards().add(c1);
        playerCards.getHandCards().add(c2);
        playerCards.getHandCards().add(c3);

        playArea.playCard(c1);
        assertEquals(1, playArea.getTurnCoins());
        assertEquals(0, playArea.getTurnDamage());

        playArea.playCard(c2);
        assertEquals(2, playArea.getTurnCoins());
        assertEquals(2, playArea.getTurnDamage());

        playArea.playCard(c3);
        assertEquals(3, playArea.getTurnCoins());
        assertEquals(2, playArea.getTurnDamage());
    }

    @Test
    void testPlayCardWithTwoSynergy() {
        Card c1 =
                new Card(
                        "Test1",
                        0,
                        Faction.IMPERIAL,
                        List.of(new CoinEffect(1)),
                        List.of(new DamageEffect(1)));
        Card c2 = new Card(c1);
        Card c3 = new Card(c2);

        playerCards.getHandCards().add(c1);
        playerCards.getHandCards().add(c2);
        playerCards.getHandCards().add(c3);

        playArea.playCard(c1);
        assertEquals(1, playArea.getTurnCoins());
        assertEquals(0, playArea.getTurnDamage());

        playArea.playCard(c2);
        assertEquals(2, playArea.getTurnCoins());
        assertEquals(2, playArea.getTurnDamage());

        playArea.playCard(c3);
        assertEquals(3, playArea.getTurnCoins());
        assertEquals(3, playArea.getTurnDamage());
    }

    @Test
    void testPlayCardWithoutType() {
        Card c1 =
                new Card(
                        "Test1",
                        0,
                        Faction.NONE,
                        List.of(new CoinEffect(1)),
                        List.of(new DamageEffect(1)));
        Card c2 = new Card(c1);

        playerCards.getHandCards().add(c1);
        playerCards.getHandCards().add(c2);

        playArea.playCard(c1);
        assertEquals(1, playArea.getTurnCoins());
        assertEquals(0, playArea.getTurnDamage());

        playArea.playCard(c2);
        assertEquals(2, playArea.getTurnCoins());
        assertEquals(0, playArea.getTurnDamage());
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
    void testVisitDraw() {
        int initialHandSize = playArea.getPlayerCards().getHandCards().size();
        playArea.visitDraw();
        assertEquals(initialHandSize + 1, playArea.getPlayerCards().getHandCards().size());
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
    void testVisitDamagePerChampionInPlay() {
        int initialTurnDamage = playArea.getTurnDamage();
        Card champ =
                new Champion(
                        "Test1",
                        0,
                        CardType.CHAMPION,
                        Faction.NONE,
                        List.of(),
                        List.of(),
                        false,
                        5);
        playArea.getPlayerCards().addToHand(champ);
        playArea.playCardById(champ.getId());
        playArea.visitDamagePerChampionInPlay(5);
        assertEquals(initialTurnDamage + 5, playArea.getTurnDamage());
    }

    @Test
    void testVisitDamagePerGuardInPlay() {
        int initialTurnDamage = playArea.getTurnDamage();
        Card champ =
                new Champion(
                        "Test1", 0, CardType.CHAMPION, Faction.NONE, List.of(), List.of(), true, 5);
        playArea.getPlayerCards().addToHand(champ);
        playArea.playCardById(champ.getId());
        playArea.visitDamagePerGuardInPlay(5);
        assertEquals(initialTurnDamage + 5, playArea.getTurnDamage());
    }

    @Test
    void testVisitHealingPerChampionInPlay() {
        int initialTurnHealing = playArea.getTurnHealing();
        Card champ =
                new Champion(
                        "Test1",
                        0,
                        CardType.CHAMPION,
                        Faction.NONE,
                        List.of(),
                        List.of(),
                        false,
                        5);
        playArea.getPlayerCards().addToHand(champ);
        playArea.playCardById(champ.getId());
        playArea.visitHealingPerChampionInPlay(5);
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

    /*@Test
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
    }*/

    @Test
    void testClearPlayedCards() {
        Card c = new Card("Test", 2, Faction.NONE, List.of(new DamageEffect(2)));
        playArea.getPlayedCards().add(c);

        playArea.clearPlayedCards();

        assertTrue(playArea.getPlayedCards().isEmpty());
        assertTrue(playArea.getPlayerCards().getDiscardedCards().contains(c));
    }

    @Test
    void testPlayCardByIdNotFound() {
        Card c = new Card("Card", 0, Faction.NONE, List.of(new DamageEffect(2)));
        assertEquals(playArea.playCardById(c.getId()), 0);
    }

    @Test
    void testPlayCardByIdFound() {
        Card c = new Card("Card", 0, Faction.NONE, List.of(new DamageEffect(2)));
        playerCards.getHandCards().add(c);
        assertEquals(playArea.playCardById(c.getId()), 1);
    }

    @Test
    void testBuyCardByIdNotFound() {
        Card c = new Card("Card", 0, Faction.NONE, List.of(new DamageEffect(2)));
        assertFalse(playArea.buyCardById(c.getId()));
    }

    @Test
    void testBuyCardByIdFound() {
        Card c = new Card("Card", 0, Faction.NONE, List.of(new DamageEffect(2)));
        market.forPurchase.add(c);
        assertTrue(playArea.buyCardById(c.getId()));
    }

    @Test
    void testGetId() {
        // I don't really know how I am supposed to test this...
        assertEquals(playArea.getId(), playArea.getId());
    }

    @Test
    void testGetDrawnCard() {
        playArea.visitDraw();
        assertTrue(playArea.getCardDrawnFromSpecialAbility() != null);
    }

    @Test
    void testResetDrawnCard() {
        playArea.visitDraw();
        playArea.resetCardDrawnFromSpecialAbility();
        assertTrue(playArea.getCardDrawnFromSpecialAbility() == null);
    }

    @AfterEach
    void tearDown() {
        playArea = null;
        playerCards = null;
        player1 = null;
    }
}
