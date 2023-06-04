package at.vunfer.openrealms.model.effects;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class HealingPerChampionInPlayEffectTest {
    @Test
    void testInvalidConstructor() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new HealingPerChampionInPlayEffect(-1),
                "Healing per champion must not be negative");
    }

    @Test
    void testToString() {
        HealingPerChampionInPlayEffect effect1 = new HealingPerChampionInPlayEffect(1);

        assertEquals("HealingPerChampionInPlayEffect{healing_per_champion=1}", effect1.toString());
    }

    @Test
    void testGetHealingPerChampion() {
        HealingPerChampionInPlayEffect effect1 = new HealingPerChampionInPlayEffect(1);

        assertEquals(1, effect1.getHealingPerChampion());
    }

    @Test
    void testEqualsDifferentValue() {
        HealingPerChampionInPlayEffect effect1 = new HealingPerChampionInPlayEffect(1);
        HealingPerChampionInPlayEffect effect2 = new HealingPerChampionInPlayEffect(2);

        assertNotEquals(effect1, effect2);
    }

    @Test
    void testEqualsExactSameObject() {
        HealingPerChampionInPlayEffect effect1 = new HealingPerChampionInPlayEffect(1);

        assertEquals(effect1, effect1);
    }

    @Test
    void testEqualsIdenticalObject() {
        HealingPerChampionInPlayEffect effect1 = new HealingPerChampionInPlayEffect(1);
        HealingPerChampionInPlayEffect effect2 = new HealingPerChampionInPlayEffect(1);

        assertEquals(effect1, effect2);
    }

    @Test
    void testEqualsDifferentType() {
        HealingPerChampionInPlayEffect effect1 = new HealingPerChampionInPlayEffect(1);
        HealingEffect effect2 = new HealingEffect(1);
        assertNotEquals(effect1, effect2);
    }

    @Test
    void testHashCode() {
        HealingPerChampionInPlayEffect effect1 = new HealingPerChampionInPlayEffect(1);
        HealingPerChampionInPlayEffect effect2 = new HealingPerChampionInPlayEffect(1);

        assertEquals(effect1.hashCode(), effect2.hashCode());
    }
}