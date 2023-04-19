/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.model.effects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class HealingEffectTest {

    @Test
    void testInvalidConstructor() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new HealingEffect(-1),
                "Healing must not be negative");
    }

    @Test
    void testToString() {
        HealingEffect effect1 = new HealingEffect(1);

        assertEquals("HealingEffect{healing=1}", effect1.toString());
    }

    @Test
    void testEqualsDifferentValue() {
        HealingEffect effect1 = new HealingEffect(1);
        HealingEffect effect2 = new HealingEffect(2);

        assertNotEquals(effect1, effect2);
    }

    @Test
    void testEqualsExactSameObject() {
        HealingEffect effect1 = new HealingEffect(1);

        assertEquals(effect1, effect1);
    }

    @Test
    void testEqualsIdenticalObject() {
        HealingEffect effect1 = new HealingEffect(1);
        HealingEffect effect2 = new HealingEffect(1);

        assertEquals(effect1, effect2);
    }

    @Test
    void testEqualsDifferentType() {
        HealingEffect effect1 = new HealingEffect(1);
        CoinEffect effect2 = new CoinEffect(1);
        assertNotEquals(effect1, effect2);
    }

    @Test
    void testHashCode() {
        HealingEffect effect1 = new HealingEffect(1);
        HealingEffect effect2 = new HealingEffect(1);

        assertEquals(effect1.hashCode(), effect2.hashCode());
    }
}
