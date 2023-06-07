/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.model.effects;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class DrawEffectTest {
    @Test
    void testInvalidConstructor() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new DrawEffect(-1),
                "Amount must not be negative.");
    }

    @Test
    void testToString() {
        DrawEffect effect1 = new DrawEffect(1);

        assertEquals("DrawEffect{amount=1}", effect1.toString());
    }

    @Test
    void testEqualsDifferentValue() {
        DrawEffect effect1 = new DrawEffect(1);
        DrawEffect effect2 = new DrawEffect(2);

        assertNotEquals(effect1, effect2);
    }

    @Test
    void testEqualsExactSameObject() {
        DrawEffect effect1 = new DrawEffect(1);

        assertEquals(effect1, effect1);
    }

    @Test
    void testEqualsIdenticalObject() {
        DrawEffect effect1 = new DrawEffect(1);
        DrawEffect effect2 = new DrawEffect(1);

        assertEquals(effect1, effect2);
    }

    @Test
    void testEqualsDifferentType() {
        DrawEffect effect1 = new DrawEffect(1);
        HealingEffect effect2 = new HealingEffect(1);
        assertNotEquals(effect1, effect2);
    }

    @Test
    void testHashCode() {
        DrawEffect effect1 = new DrawEffect(1);
        DrawEffect effect2 = new DrawEffect(1);

        assertEquals(effect1.hashCode(), effect2.hashCode());
    }
}
