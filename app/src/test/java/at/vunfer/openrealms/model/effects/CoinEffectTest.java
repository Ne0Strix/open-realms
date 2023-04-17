/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.model.effects;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CoinEffectTest {

    @Test
    void testInvalidConstructor() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new CoinEffect(-1),
                "Coin must not be negative");
    }

    @Test
    void testToString() {
        CoinEffect effect1 = new CoinEffect(1);

        assertEquals("CoinEffect{coin=1}", effect1.toString());
    }

    @Test
    void testEqualsDifferentValue() {
        CoinEffect effect1 = new CoinEffect(1);
        CoinEffect effect2 = new CoinEffect(2);

        assertNotEquals(effect1, effect2);
    }

    @Test
    void testEqualsExactSameObject() {
        CoinEffect effect1 = new CoinEffect(1);

        assertEquals(effect1, effect1);
    }

    @Test
    void testEqualsIdenticalObject() {
        CoinEffect effect1 = new CoinEffect(1);
        CoinEffect effect2 = new CoinEffect(1);

        assertEquals(effect1, effect2);
    }

    @Test
    void testEqualsDifferentType() {
        CoinEffect effect1 = new CoinEffect(1);

        assertNotEquals("CoinEffect", effect1);
    }

    @Test
    void testHashCode() {
        CoinEffect effect1 = new CoinEffect(1);
        CoinEffect effect2 = new CoinEffect(1);

        assertEquals(effect1.hashCode(), effect2.hashCode());
    }
}
