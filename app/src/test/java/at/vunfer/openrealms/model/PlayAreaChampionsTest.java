/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import at.vunfer.openrealms.model.effects.DamageEffect;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PlayAreaChampionsTest {

    private PlayArea playArea;
    private PlayerCards playerCards;

    @BeforeEach
    void setUp() {
        playerCards = new PlayerCards();
        playArea = new PlayArea(70, playerCards);

        Deck<Card> deck = new Deck<>();
        deck.add(
                new Champion(
                        "Champion 1",
                        1,
                        CardType.CHAMPION,
                        Faction.GUILD,
                        List.of(new DamageEffect(1)),
                        List.of(),
                        true,
                        2));
        deck.add(
                new Champion(
                        "Champion 2",
                        2,
                        CardType.CHAMPION,
                        Faction.GUILD,
                        List.of(),
                        List.of(),
                        false,
                        2));
        deck.add(
                new Champion(
                        "Champion 3",
                        3,
                        CardType.CHAMPION,
                        Faction.IMPERIAL,
                        List.of(),
                        List.of(),
                        false,
                        3));
        deck.add(
                new Champion(
                        "Champion 4",
                        4,
                        CardType.CHAMPION,
                        Faction.IMPERIAL,
                        List.of(),
                        List.of(),
                        false,
                        4));
        deck.add(
                new Card(
                        "Card 5",
                        5,
                        CardType.ACTION,
                        Faction.GUILD,
                        List.of(),
                        List.of(new DamageEffect(2))));
        playerCards.setDeckCards(deck);
    }

    @AfterEach
    void tearDown() {
        playArea = null;
        playerCards = null;
    }

    @Test
    void testPlayChampion() {
        Champion champion = (Champion) playerCards.getHandCards().get(0);
        playArea.playCard(champion);
        assertTrue(playArea.getPlayedChampions().contains(champion));
        assertFalse(playerCards.getHandCards().contains(champion));
        assertTrue(champion.isExpended());
    }

    @Test
    void testExpendChampion() {
        Champion champion = (Champion) playerCards.getHandCards().get(0);
        playArea.getPlayedChampions().add(champion);
        assertTrue(playArea.expendChampion(champion));
        assertTrue(champion.isExpended());
        assertFalse(playArea.expendChampion(champion));
    }

    @Test
    void testExpendChampionById() {
        Champion champion = (Champion) playerCards.getHandCards().get(0);
        playArea.getPlayedChampions().add(champion);
        assertTrue(playArea.expendChampionById(champion.getId()));
        assertTrue(champion.isExpended());
    }

    @Test
    void testAttackChampionKilled() {
        Champion champion = (Champion) playerCards.getHandCards().get(0);
        playArea.playCard(champion);
        playArea.visitDamage(champion.getHealth() + 1);
        assertTrue(playArea.attackChampion(champion, playArea));
        assertFalse(playArea.getPlayedChampions().contains(champion));
    }

    @Test
    void testAttackChampionNotKilled() {
        Champion champion = (Champion) playerCards.getHandCards().get(0);
        playArea.getPlayedChampions().add(champion);
        playArea.visitDamage(champion.getHealth() - 1);
        assertFalse(playArea.attackChampion(champion, playArea));
        assertTrue(playArea.getPlayedChampions().contains(champion));
    }

    @Test
    void testAttackChampionById() {
        Champion champion = (Champion) playerCards.getHandCards().get(0);
        playArea.playCard(champion);
        playArea.visitDamage(champion.getHealth() + 1);
        assertTrue(playArea.attackChampionById(champion.getId(), playArea));
        assertFalse(playArea.getPlayedChampions().contains(champion));
    }

    @Test
    void testChampionIsAttacked() {
        Champion champion = (Champion) playerCards.getHandCards().get(0);
        assertTrue(playArea.championIsAttacked(champion, champion.getHealth() + 1));
        assertFalse(champion.isExpended());
        assertFalse(playArea.getPlayedChampions().contains(champion));
        assertTrue(playArea.getPlayerCards().getDiscardedCards().contains(champion));
    }

    @Test
    void testResetChampions() {
        Champion champion = (Champion) playerCards.getHandCards().get(0);
        playArea.getPlayedChampions().add(champion);
        assertFalse(champion.isExpended());
        playArea.expendChampion(champion);
        assertTrue(champion.isExpended());
        playArea.resetChampions();
        assertFalse(champion.isExpended());
    }

    @Test
    void testGetAtTurnEndDiscardedChampions() {
        Champion champion = null;
        for (Card c : playerCards.getHandCards()) {
            if (c.getId() == 0) {
                champion = (Champion) c;
            }
        }
        if (champion != null) {
            assertSame("Champion 1", champion.getName());
            playArea.playCard(champion);
            playArea.takeDamage(champion.getHealth());
            assertTrue(playArea.getAtTurnEndDiscardedChampions().contains(champion));
        }
    }

    @Test
    void testTakeDamageWithGuardKilled() {
        int health = playArea.getHealth();
        Champion guard = null;
        Champion champion = null;
        for (Card c : playerCards.getHandCards()) {
            if (Objects.equals(c.getName(), "Champion 1")) {
                guard = (Champion) c;
            }
            if (Objects.equals(c.getName(), "Champion 2")) {
                champion = (Champion) c;
            }
        }
        assert guard != null;
        assert champion != null;
        playArea.playCard(guard);
        playArea.playCard(champion);
        assertEquals(playArea.getPlayedChampions().size(), 2);
        playArea.takeDamage(guard.getHealth() + 1);
        assertTrue(playArea.getAtTurnEndDiscardedChampions().contains(guard));
        assertFalse(playArea.getAtTurnEndDiscardedChampions().contains(champion));
        assertTrue(playArea.getPlayedChampions().contains(champion));
        assertFalse(playArea.getPlayedChampions().contains(guard));
        assertEquals(health - 1, playArea.getHealth());
    }

    @Test
    void testTakeDamageWithGuardBlocking() {
        int health = playArea.getHealth();
        Champion guard = null;
        Champion champion = null;
        assertEquals(playerCards.getHandCards().size(), 5);
        for (Card c : playerCards.getHandCards()) {
            if (Objects.equals(c.getName(), "Champion 1")) {
                guard = (Champion) c;
            }
            if (Objects.equals(c.getName(), "Champion 2")) {
                champion = (Champion) c;
            }
        }

        assert guard != null;
        assert champion != null;
        playArea.playCard(guard);
        playArea.playCard(champion);
        assertEquals(playArea.getPlayedChampions().size(), 2);
        playArea.takeDamage(guard.getHealth() - 1);
        assertFalse(playArea.getAtTurnEndDiscardedChampions().contains(guard));
        assertFalse(playArea.getAtTurnEndDiscardedChampions().contains(champion));
        assertTrue(playArea.getPlayedChampions().contains(champion));
        assertTrue(playArea.getPlayedChampions().contains(guard));
        assertEquals(health, playArea.getHealth());
    }

    @Test
    void testTakeZeroDamage() {
        int health = playArea.getHealth();
        Champion guard = null;
        Champion champion = null;
        assertEquals(playerCards.getHandCards().size(), 5);
        for (Card c : playerCards.getHandCards()) {
            if (Objects.equals(c.getName(), "Champion 1")) {
                guard = (Champion) c;
            }
            if (Objects.equals(c.getName(), "Champion 2")) {
                champion = (Champion) c;
            }
        }

        assert guard != null;
        assert champion != null;
        playArea.playCard(guard);
        playArea.playCard(champion);
        assertEquals(playArea.getPlayedChampions().size(), 2);
        playArea.takeDamage(0);
        assertFalse(playArea.getAtTurnEndDiscardedChampions().contains(guard));
        assertFalse(playArea.getAtTurnEndDiscardedChampions().contains(champion));
        assertTrue(playArea.getPlayedChampions().contains(champion));
        assertTrue(playArea.getPlayedChampions().contains(guard));
        assertEquals(health, playArea.getHealth());
    }

    @Test
    void testChampionSynergyWhenNotExpended() {
        Champion guard = null;
        Card card = null;
        assertEquals(playerCards.getHandCards().size(), 5);
        for (Card c : playerCards.getHandCards()) {
            if (Objects.equals(c.getName(), "Champion 1")) {
                guard = (Champion) c;
            }
            if (Objects.equals(c.getName(), "Card 5")) {
                card = c;
            }
        }

        assert guard != null;
        assert card != null;
        playArea.getPlayedChampions().add(guard);
        playArea.playCard(card);
        assertTrue(playArea.getPlayedChampions().contains(guard));
        assertTrue(playArea.getPlayedCards().contains(card));
        assertEquals(2, playArea.getTurnDamage());
        assertTrue(playArea.expendChampion(guard));
        assertEquals(3, playArea.getTurnDamage());
    }

    @Test
    void testBehaviorWhenCardsNotFound() {
        Champion champion =
                new Champion(
                        "Null",
                        0,
                        CardType.CHAMPION,
                        Faction.GUILD,
                        List.of(),
                        List.of(),
                        false,
                        3);
        assertFalse(playArea.expendChampionById(champion.getId()));
        assertFalse(playArea.attackChampionById(champion.getId(), playArea));
    }
}
