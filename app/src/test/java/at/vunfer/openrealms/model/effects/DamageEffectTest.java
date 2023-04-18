/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.model.effects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class DamageEffectTest {

    @Test
    void testInvalidConstructor() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new DamageEffect(-1),
                "Damage must not be negative");
    }

    @Test
    void testToString() {
        DamageEffect effect1 = new DamageEffect(1);

        assertEquals("DamageEffect{damage=1}", effect1.toString());
    }

    @Test
    void testEqualsDifferentValue() {
        DamageEffect effect1 = new DamageEffect(1);
        DamageEffect effect2 = new DamageEffect(2);

        assertNotEquals(effect1, effect2);
    }

    @Test
    void testEqualsExactSameObject() {
        DamageEffect effect1 = new DamageEffect(1);

        assertEquals(effect1, effect1);
    }

    @Test
    void testEqualsIdenticalObject() {
        DamageEffect effect1 = new DamageEffect(1);
        DamageEffect effect2 = new DamageEffect(1);

        assertEquals(effect1, effect2);
    }

    @Test
    void testEqualsDifferentType() {
        DamageEffect effect1 = new DamageEffect(1);
        HealingEffect effect2 = new HealingEffect(1);
        assertNotEquals(effect1, effect2);
    }

    @Test
    void testHashCode() {
        DamageEffect effect1 = new DamageEffect(1);
        DamageEffect effect2 = new DamageEffect(1);

        assertEquals(effect1.hashCode(), effect2.hashCode());
    }
}
