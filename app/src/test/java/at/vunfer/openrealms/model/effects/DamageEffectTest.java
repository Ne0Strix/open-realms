/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.model.effects;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class DamageEffectTest {

    @Test
    public void testInvalidConstructor() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new DamageEffect(-1),
                "Damage must not be negative");
    }
}
