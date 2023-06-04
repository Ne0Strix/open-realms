/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.model.effects;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class DamagePerChampionInPlayEffectTest {
    @Test
    void testInvalidConstructor() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new DamagePerChampionInPlayEffect(-1),
                "Damage per champion must not be negative");
    }

    @Test
    void testToString() {
        DamagePerChampionInPlayEffect effect1 = new DamagePerChampionInPlayEffect(1);

        assertEquals("DamagePerChampionInPlayEffect{damage_per_champion=1}", effect1.toString());
    }

    @Test
    void testGetDamagePerChampion() {
        DamagePerChampionInPlayEffect effect1 = new DamagePerChampionInPlayEffect(1);

        assertEquals(1, effect1.getAmount());
    }

    @Test
    void testEqualsDifferentValue() {
        DamagePerChampionInPlayEffect effect1 = new DamagePerChampionInPlayEffect(1);
        DamagePerChampionInPlayEffect effect2 = new DamagePerChampionInPlayEffect(2);

        assertNotEquals(effect1, effect2);
    }

    @Test
    void testEqualsExactSameObject() {
        DamagePerChampionInPlayEffect effect1 = new DamagePerChampionInPlayEffect(1);

        assertEquals(effect1, effect1);
    }

    @Test
    void testEqualsIdenticalObject() {
        DamagePerChampionInPlayEffect effect1 = new DamagePerChampionInPlayEffect(1);
        DamagePerChampionInPlayEffect effect2 = new DamagePerChampionInPlayEffect(1);

        assertEquals(effect1, effect2);
    }

    @Test
    void testEqualsDifferentType() {
        DamagePerChampionInPlayEffect effect1 = new DamagePerChampionInPlayEffect(1);
        HealingEffect effect2 = new HealingEffect(1);
        assertNotEquals(effect1, effect2);
    }

    @Test
    void testHashCode() {
        DamagePerChampionInPlayEffect effect1 = new DamagePerChampionInPlayEffect(1);
        DamagePerChampionInPlayEffect effect2 = new DamagePerChampionInPlayEffect(1);

        assertEquals(effect1.hashCode(), effect2.hashCode());
    }
}
