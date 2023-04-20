/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import at.vunfer.openrealms.model.effects.CoinEffect;
import at.vunfer.openrealms.model.effects.DamageEffect;
import at.vunfer.openrealms.model.effects.HealingEffect;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

public class ChampionTest {

    @Test
    public void testConstructorValid() {
        // Test valid construction
        List<Effect> effects =
                List.of(new DamageEffect(1), new HealingEffect(1), new CoinEffect(1));
        Champion champion = new Champion("Test Champion", 3, effects);
        assertEquals("Test Champion", champion.getName());
        assertEquals(3, champion.getCost());
        assertEquals(effects, champion.getEffects());
    }

    @Test
    public void testConstructorInvalid() {
        // Test invalid construction
        List<Effect> effects =
                List.of(new DamageEffect(1), new HealingEffect(1), new CoinEffect(1));
        assertThrows(IllegalArgumentException.class, () -> new Champion("", 3, effects));
        assertThrows(
                IllegalArgumentException.class, () -> new Champion("Test Champion", -3, effects));
        assertThrows(IllegalArgumentException.class, () -> new Champion("Test Champion", 3, null));
        assertThrows(
                IllegalArgumentException.class,
                () -> new Champion("Test Champion", 3, new ArrayList<>()));
    }

    @Test
    public void testApplyEffects() {
        // Set up play area and champion with effects
        PlayArea playArea = new PlayArea(20, new PlayerCards());
        Champion champion =
                new Champion(
                        "Test Champion", 3, List.of(new DamageEffect(1), new HealingEffect(1)));

        // Apply effects to play area
        champion.applyEffects(playArea);

        // Check if effects were applied correctly
        assertEquals(1, playArea.getTurnDamage());
        assertEquals(1, playArea.getTurnHealing());
    }

    @Test
    public void testToString() {
        Champion champion =
                new Champion(
                        "Test Champion",
                        3,
                        List.of(new DamageEffect(1), new HealingEffect(1), new CoinEffect(1)));
        assertEquals("Card{name='Test Champion', cost=3}", champion.toString());
    }
}
