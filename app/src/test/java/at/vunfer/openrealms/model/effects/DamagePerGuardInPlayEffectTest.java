package at.vunfer.openrealms.model.effects;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class DamagePerGuardInPlayEffectTest {
    @Test
    void testInvalidConstructor() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new DamagePerGuardInPlayEffect(-1),
                "Damage per guard must not be negative");
    }

    @Test
    void testToString() {
        DamagePerGuardInPlayEffect effect1 = new DamagePerGuardInPlayEffect(1);

        assertEquals("DamagePerGuardInPlayEffect{damage_per_guard=1}", effect1.toString());
    }

    @Test
    void testGetDamagePerGuard() {
        DamagePerGuardInPlayEffect effect1 = new DamagePerGuardInPlayEffect(1);

        assertEquals(1, effect1.getDamagePerGuard());
    }

    @Test
    void testEqualsDifferentValue() {
        DamagePerGuardInPlayEffect effect1 = new DamagePerGuardInPlayEffect(1);
        DamagePerGuardInPlayEffect effect2 = new DamagePerGuardInPlayEffect(2);

        assertNotEquals(effect1, effect2);
    }

    @Test
    void testEqualsExactSameObject() {
        DamagePerGuardInPlayEffect effect1 = new DamagePerGuardInPlayEffect(1);

        assertEquals(effect1, effect1);
    }

    @Test
    void testEqualsIdenticalObject() {
        DamagePerGuardInPlayEffect effect1 = new DamagePerGuardInPlayEffect(1);
        DamagePerGuardInPlayEffect effect2 = new DamagePerGuardInPlayEffect(1);

        assertEquals(effect1, effect2);
    }

    @Test
    void testEqualsDifferentType() {
        DamagePerGuardInPlayEffect effect1 = new DamagePerGuardInPlayEffect(1);
        HealingEffect effect2 = new HealingEffect(1);
        assertNotEquals(effect1, effect2);
    }

    @Test
    void testHashCode() {
        DamagePerGuardInPlayEffect effect1 = new DamagePerGuardInPlayEffect(1);
        DamagePerGuardInPlayEffect effect2 = new DamagePerGuardInPlayEffect(1);

        assertEquals(effect1.hashCode(), effect2.hashCode());
    }
}