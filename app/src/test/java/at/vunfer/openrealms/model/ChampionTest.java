/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.model;

import static org.junit.jupiter.api.Assertions.*;

import at.vunfer.openrealms.model.effects.CoinEffect;
import at.vunfer.openrealms.model.effects.DamageEffect;
import at.vunfer.openrealms.model.effects.HealingEffect;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class ChampionTest {

    @Test
    void testConstructorValid() {
        // Test valid construction
        List<Effect> effects =
                List.of(new DamageEffect(1), new HealingEffect(1), new CoinEffect(1));
        Champion champion =
                new Champion(
                        "Test Champion",
                        3,
                        CardType.CHAMPION,
                        Faction.NONE,
                        effects,
                        new ArrayList<>(),
                        false,
                        10);
        assertEquals("Test Champion", champion.getName());
        assertEquals(3, champion.getCost());
        assertEquals(effects, champion.getEffects());
        assertFalse(champion.isGuard());
        assertEquals(10, champion.getHealth());
        assertFalse(champion.isExpended());
    }

    @Test
    void testConstructorInvalid() {
        // Test invalid construction
        List<Effect> effects =
                List.of(new DamageEffect(1), new HealingEffect(1), new CoinEffect(1));
        assertThrows(
                IllegalArgumentException.class,
                () ->
                        new Champion(
                                "",
                                3,
                                CardType.CHAMPION,
                                Faction.NONE,
                                effects,
                                new ArrayList<>(),
                                false,
                                10));
        assertThrows(
                IllegalArgumentException.class,
                () ->
                        new Champion(
                                "Test Champion",
                                -3,
                                CardType.CHAMPION,
                                Faction.NONE,
                                effects,
                                new ArrayList<>(),
                                false,
                                10));
        assertThrows(
                IllegalArgumentException.class,
                () ->
                        new Champion(
                                "Test Champion",
                                3,
                                CardType.CHAMPION,
                                Faction.NONE,
                                null,
                                new ArrayList<>(),
                                false,
                                10));
        assertThrows(
                IllegalArgumentException.class,
                () ->
                        new Champion(
                                "Test Champion",
                                3,
                                CardType.CHAMPION,
                                Faction.NONE,
                                effects,
                                new ArrayList<>(),
                                false,
                                -1));
    }

    @Test
    void testIsKilled() {
        Champion champion =
                new Champion(
                        "Test Champion",
                        3,
                        CardType.CHAMPION,
                        Faction.NONE,
                        new ArrayList<>(),
                        new ArrayList<>(),
                        false,
                        10);
        assertFalse(champion.isKilled(5));
        assertTrue(champion.isKilled(10));
        assertTrue(champion.isKilled(15));
    }

    @Test
    void testExpendAndReset() {
        Champion champion =
                new Champion(
                        "Test Champion",
                        3,
                        CardType.CHAMPION,
                        Faction.NONE,
                        new ArrayList<>(),
                        new ArrayList<>(),
                        false,
                        10);
        assertFalse(champion.isExpended());
        assertTrue(champion.expend());
        assertTrue(champion.isExpended());
        assertFalse(champion.expend());
        champion.reset();
        assertFalse(champion.isExpended());
    }

    @Test
    void testToString() {
        Champion champion =
                new Champion(
                        "Test Champion",
                        3,
                        CardType.CHAMPION,
                        Faction.NONE,
                        List.of(new DamageEffect(1), new HealingEffect(1), new CoinEffect(1)),
                        new ArrayList<>(),
                        true,
                        10);
        assertEquals(
                "Champion{name='Test Champion', cost=3, type=CHAMPION, faction=NONE,"
                        + " effects=[DamageEffect{damage=1}, HealingEffect{healing=1},"
                        + " CoinEffect{coin=1}], synergyEffects=[], id="
                        + champion.getId()
                        + "}",
                champion.toString().split("isGuard")[0]);
        assertTrue(champion.toString().contains("isGuard=true"));
        assertTrue(champion.toString().contains("health=10"));
        assertTrue(champion.toString().contains("isExpended=false"));
    }
}
