/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.model;

import static org.junit.jupiter.api.Assertions.*;

import at.vunfer.openrealms.model.effects.CoinEffect;
import at.vunfer.openrealms.model.effects.DamageEffect;
import at.vunfer.openrealms.model.effects.HealingEffect;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CardTest {
    List<Effect> oneCoinTwoDamage = List.of(new CoinEffect(1), new DamageEffect(2));
    List<Effect> twoCoinThreeHealing = List.of(new CoinEffect(2), new HealingEffect(3));
    List<Effect> oneHealingOneDamage = List.of(new HealingEffect(1), new DamageEffect(1));
    List<Effect> emptyList = List.of();
    List<Effect> nullList = null;

    Card card1 = new Card("Card 1", 1, CardType.NONE, oneCoinTwoDamage, twoCoinThreeHealing);
    Card card2 = new Card("Card 2", 1, CardType.NONE, twoCoinThreeHealing);
    Card card3 = new Card("Card 3", 1, CardType.NONE, oneHealingOneDamage);

    PlayArea playArea;

    @BeforeEach
    void setUp() {
        playArea = new PlayArea(70, new PlayerCards());
    }

    @AfterEach
    void tearDown() {
        Card.getFullCardCollection().clear();
    }

    void reset() {
        playArea.resetTurnPool();
    }

    @Test
    void testNameConstraints() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Card(null, 1, CardType.NONE, oneCoinTwoDamage));
        assertThrows(
                IllegalArgumentException.class,
                () -> new Card("", 1, CardType.NONE, twoCoinThreeHealing));
    }

    @Test
    void testCostConstraint() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Card("test", -1, CardType.NONE, oneCoinTwoDamage));
    }

    @Test
    void testEffectConstraints() {
        assertThrows(
                IllegalArgumentException.class, () -> new Card("test", 1, CardType.NONE, nullList));
        assertThrows(
                IllegalArgumentException.class,
                () -> new Card("test", 1, CardType.NONE, emptyList));
    }

    @Test
    void testSynergyEffectConstraints() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Card("test", 1, CardType.NONE, oneCoinTwoDamage, nullList));
    }

    @Test
    void testGetters() {
        assertEquals("Card 1", card1.getName());
        assertEquals(1, card1.getCost());
        assertEquals(oneCoinTwoDamage, card1.getEffects());
        assertEquals(CardType.NONE, card1.getType());
        assertEquals(twoCoinThreeHealing, card1.getSynergyEffects());
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
    void testApplySynergyEffects() {
        card1.applySynergyEffects(playArea);
        assertEquals(2, playArea.getTurnCoins());
        assertEquals(3, playArea.getTurnHealing());
    }

    @Test
    void testToString() {
        // Card id has to be split, since the id of the cards is not Test-order independent
        assertEquals(
                "Card{name='Card 1', cost=1, type=NONE, effects=[CoinEffect{coin=1},"
                        + " DamageEffect{damage=2}], synergyEffects=[CoinEffect{coin=2},"
                        + " HealingEffect{healing=3}], ",
                card1.toString().split("id")[0]);
        assertEquals(
                "Card{name='Card 2', cost=1, type=NONE, effects=[CoinEffect{coin=2},"
                        + " HealingEffect{healing=3}], synergyEffects=[], ",
                card2.toString().split("id")[0]);
        assertEquals(
                "Card{name='Card 3', cost=1, type=NONE, effects=[HealingEffect{healing=1},"
                        + " DamageEffect{damage=1}], synergyEffects=[], ",
                card3.toString().split("id")[0]);
    }

    @Test
    void testIsIdenticalDifferentName() {
        Card cardA = new Card("Name", 1, CardType.NONE, List.of(new DamageEffect(1)));
        Card cardB = new Card("Different Name", 1, CardType.NONE, List.of(new DamageEffect(1)));

        assertFalse(cardA.isIdentical(cardB));
    }

    @Test
    void testIsIdenticalDifferentCost() {
        Card cardA = new Card("Name", 1, CardType.NONE, List.of(new DamageEffect(1)));
        Card cardB = new Card("Name", 2, CardType.NONE, List.of(new DamageEffect(1)));

        assertFalse(cardA.isIdentical(cardB));
    }

    @Test
    void testIsIdenticalDifferentEffect() {
        Card cardA = new Card("Name", 1, CardType.NONE, List.of(new DamageEffect(1)));
        Card cardB = new Card("Name", 1, CardType.NONE, List.of(new HealingEffect(2)));

        assertFalse(cardA.isIdentical(cardB));
    }

    @Test
    void testIsIdenticalDifferentSynergyEffect() {
        Card cardA =
                new Card(
                        "Name",
                        1,
                        CardType.NONE,
                        List.of(new HealingEffect(1)),
                        List.of(new DamageEffect(1)));
        Card cardB =
                new Card(
                        "Name",
                        1,
                        CardType.NONE,
                        List.of(new HealingEffect(1)),
                        List.of(new HealingEffect(2)));

        assertFalse(cardA.isIdentical(cardB));
    }

    @Test
    void testIsIdenticalDifferentTypes() {
        Card cardA =
                new Card(
                        "Name",
                        1,
                        CardType.WILD,
                        List.of(new HealingEffect(1)),
                        List.of(new DamageEffect(1)));
        Card cardB =
                new Card(
                        "Name",
                        1,
                        CardType.NECROS,
                        List.of(new HealingEffect(1)),
                        List.of(new HealingEffect(2)));

        assertFalse(cardA.isIdentical(cardB));
    }

    @Test
    void testIsIdenticalEqualObject() {
        assertTrue(card1.isIdentical(card1));
    }

    @Test
    void testIsIdenticalIdenticalObject() {
        Card cardA = new Card("Name", 1, CardType.NONE, List.of(new DamageEffect(1)));
        Card cardB = new Card("Name", 1, CardType.NONE, List.of(new DamageEffect(1)));

        assertTrue(cardA.isIdentical(cardB));
    }

    @Test
    void testEqualsEqualObject() {
        Card card1 = new Card("Name", 1, CardType.NONE, List.of(new DamageEffect(1)));
        assertTrue(card1.equals(card1));
    }

    @Test
    void testEqualsEqualParameter() {
        Card card1 = new Card("Name", 1, CardType.NONE, List.of(new DamageEffect(1)));
        Card card2 = new Card("Name", 1, CardType.NONE, List.of(new DamageEffect(1)));
        assertFalse(card1.equals(card2));
    }

    @Test
    void testEqualsEqualNull() {
        Card card1 = new Card("Name", 1, CardType.NONE, List.of(new DamageEffect(1)));
        assertFalse(card1.equals(null));
    }

    @Test
    void testEqualsDifferentClass() {
        Card card1 = new Card("Name", 1, CardType.NONE, List.of(new DamageEffect(1)));
        assertFalse(card1.equals("Card"));
    }

    @Test
    void testEqualsDifferentEffects() {
        Card card1 = new Card("Name", 1, CardType.NONE, List.of(new DamageEffect(1)));
        Card card2 = new Card("Name", 1, CardType.NONE, List.of(new HealingEffect(1)));
        assertFalse(card1.equals(card2));
    }

    @Test
    void testEqualsDifferentSynergyEffects() {
        Card card1 =
                new Card(
                        "Name",
                        1,
                        CardType.NONE,
                        List.of(new DamageEffect(1)),
                        List.of(new DamageEffect(1)));
        Card card2 =
                new Card(
                        "Name",
                        1,
                        CardType.NONE,
                        List.of(new DamageEffect(1)),
                        List.of(new HealingEffect(1)));
        assertFalse(card1.equals(card2));
    }

    @Test
    void testEqualsDifferentType() {
        Card card1 = new Card("Name", 1, CardType.NONE, List.of(new DamageEffect(1)));
        Card card2 = new Card("Name", 1, CardType.WILD, List.of(new DamageEffect(1)));
        assertFalse(card1.equals(card2));
    }

    @Test
    void testHashCode() {
        Card card1 = new Card("Name", 1, CardType.NONE, List.of(new DamageEffect(1)));
        assertEquals(card1.hashCode(), card1.hashCode());
    }

    @Test
    void testGetCardById() {
        Card card1 = new Card("Name", 1, CardType.NONE, List.of(new DamageEffect(1)));
        int id = card1.getId();
        assertEquals(card1, Card.getCardById(id));
    }

    @Test
    void testGetCardByIdNotIn() {
        Card card1 = new Card("Name", 1, CardType.NONE, List.of(new DamageEffect(1)));
        int id = card1.getId() + 1;
        assertNull(Card.getCardById(id));
    }

    @Test
    void testGetFullCardCollection() {
        assertEquals(List.of(card1, card2, card3), Card.getFullCardCollection());
    }
}
