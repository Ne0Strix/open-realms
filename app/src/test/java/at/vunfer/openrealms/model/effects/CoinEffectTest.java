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
}
