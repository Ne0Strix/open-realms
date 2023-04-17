/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.model;

import static org.junit.jupiter.api.Assertions.*;

import at.vunfer.openrealms.model.effects.DamageEffect;
import at.vunfer.openrealms.model.effects.HealingEffect;
import java.util.List;
import org.junit.jupiter.api.Test;

class CardTest {

    @Test
    void testEqualsDifferentName() {
        Card card1 = new Card("Name", 1, List.of(new DamageEffect(1)));
        Card card2 = new Card("Different Name", 1, List.of(new DamageEffect(1)));

        assertNotEquals(card1, card2);
    }

    @Test
    void testEqualsDifferentCost() {
        Card card1 = new Card("Name", 1, List.of(new DamageEffect(1)));
        Card card2 = new Card("Name", 2, List.of(new DamageEffect(1)));

        assertNotEquals(card1, card2);
    }

    @Test
    void testEqualsDifferentEffect() {
        Card card1 = new Card("Name", 1, List.of(new DamageEffect(1)));
        Card card2 = new Card("Name", 1, List.of(new HealingEffect(2)));

        assertNotEquals(card1, card2);
    }

    @Test
    void testEqualsExactSameObject() {
        Card card = new Card("Name", 1, List.of(new DamageEffect(1)));

        assertEquals(card, card);
    }

    @Test
    void testEqualsIdenticalObject() {
        Card card1 = new Card("Name", 1, List.of(new DamageEffect(1)));
        Card card2 = new Card("Name", 1, List.of(new DamageEffect(1)));

        assertEquals(card1, card2);
    }

    @Test
    void testEqualsDifferentType() {
        Card card = new Card("Name", 1, List.of(new DamageEffect(1)));

        assertNotEquals(card, "Card");
    }

    @Test
    void testHashCode() {
        Card card1 = new Card("Name", 1, List.of(new DamageEffect(1)));
        Card card2 = new Card("Name", 1, List.of(new DamageEffect(1)));

        assertEquals(card1.hashCode(), card2.hashCode());
    }
}
