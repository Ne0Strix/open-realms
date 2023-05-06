/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.model;

import static org.junit.jupiter.api.Assertions.*;

import at.vunfer.openrealms.model.effects.CoinEffect;
import at.vunfer.openrealms.model.effects.DamageEffect;
import at.vunfer.openrealms.model.effects.HealingEffect;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CardTest {
    List<Effect> oneCoinTwoDamage = List.of(new CoinEffect(1), new DamageEffect(2));
    List<Effect> twoCoinThreeHealing = List.of(new CoinEffect(2), new HealingEffect(3));
    List<Effect> oneHealingOneDamage = List.of(new HealingEffect(1), new DamageEffect(1));
    List<Effect> emptyList = List.of();
    List<Effect> nullList = null;

    Card card1 = new Card("Card 1", 1, oneCoinTwoDamage);
    Card card2 = new Card("Card 2", 1, twoCoinThreeHealing);
    Card card3 = new Card("Card 3", 1, oneHealingOneDamage);

    PlayArea playArea;

    @BeforeEach
    void setUp() {
        playArea = new PlayArea(70, new PlayerCards());
    }

    void reset() {
        playArea.resetTurnPool();
    }

    @Test
    void testNameConstraints() {
        assertThrows(IllegalArgumentException.class, () -> new Card(null, 1, oneCoinTwoDamage));
        assertThrows(IllegalArgumentException.class, () -> new Card("", 1, twoCoinThreeHealing));
    }

    @Test
    void testCostConstraint() {
        assertThrows(IllegalArgumentException.class, () -> new Card("test", -1, oneCoinTwoDamage));
    }

    @Test
    void testEffectConstraints() {
        assertThrows(IllegalArgumentException.class, () -> new Card("test", 1, nullList));
        assertThrows(IllegalArgumentException.class, () -> new Card("test", 1, emptyList));
    }

    @Test
    void testGetters() {
        assertEquals("Card 1", card1.getName());
        assertEquals(1, card1.getCost());
        assertEquals(oneCoinTwoDamage, card1.getEffects());
    }

    @Test
    void testApplyCoinEffects() {
        card1.applyEffects(playArea);
        card2.applyEffects(playArea);
        card3.applyEffects(playArea);
        assertEquals(3, playArea.getTurnCoins());
    }

    @Test
    void testApplyDamageEffects() {
        card1.applyEffects(playArea);
        card2.applyEffects(playArea);
        card3.applyEffects(playArea);
        assertEquals(3, playArea.getTurnDamage());
    }

    @Test
    void testApplyHealingEffects() {
        card1.applyEffects(playArea);
        card2.applyEffects(playArea);
        card3.applyEffects(playArea);
        assertEquals(4, playArea.getTurnHealing());
    }

    @Test
    void testToString() {
        assertEquals(
                "Card{name='Card 1', cost=1, effects=[CoinEffect{coin=1}, DamageEffect{damage=2}]}",
                card1.toString());
        assertEquals(
                "Card{name='Card 2', cost=1, effects=[CoinEffect{coin=2},"
                        + " HealingEffect{healing=3}]}",
                card2.toString());
        assertEquals(
                "Card{name='Card 3', cost=1, effects=[HealingEffect{healing=1},"
                        + " DamageEffect{damage=1}]}",
                card3.toString());
    }

    @Test
    void testIsIdenticalDifferentName() {
        Card cardA = new Card("Name", 1, List.of(new DamageEffect(1)));
        Card cardB = new Card("Different Name", 1, List.of(new DamageEffect(1)));

        assertFalse(cardA.isIdentical(cardB));
    }

    @Test
    void testIsIdenticalDifferentCost() {
        Card cardA = new Card("Name", 1, List.of(new DamageEffect(1)));
        Card cardB = new Card("Name", 2, List.of(new DamageEffect(1)));

        assertFalse(cardA.isIdentical(cardB));
    }

    @Test
    void testIsIdenticalDifferentEffect() {
        Card cardA = new Card("Name", 1, List.of(new DamageEffect(1)));
        Card cardB = new Card("Name", 1, List.of(new HealingEffect(2)));

        assertFalse(cardA.isIdentical(cardB));
    }

    @Test
    void testIsIdenticalEqualObject() {
        assertTrue(card1.isIdentical(card1));
    }

    @Test
    void testIsIdenticalIdenticalObject() {
        Card cardA = new Card("Name", 1, List.of(new DamageEffect(1)));
        Card cardB = new Card("Name", 1, List.of(new DamageEffect(1)));

        assertTrue(cardA.isIdentical(cardB));
    }
}
