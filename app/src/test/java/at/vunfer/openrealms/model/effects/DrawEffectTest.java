/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.model.effects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import at.vunfer.openrealms.model.PlayArea;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

class DrawEffectTest {
    private PlayArea playArea;
    private DrawEffect effect;

    @BeforeEach
    void setUp(TestInfo testInfo) {
        playArea = mock(PlayArea.class);
        effect = new DrawEffect(1);
        System.out.println("Running test: " + testInfo.getDisplayName());
    }

    @RepeatedTest(1000)
    void testApplyEffectPerformance() {
        long startTime = System.nanoTime();

        effect.applyEffect(playArea);

        long endTime = System.nanoTime();
        long executionTime = endTime - startTime;

        System.out.println("Execution time: " + executionTime + " ns");
    }

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

    @Test
    void testApplyEffect() {
        PlayArea playArea = mock(PlayArea.class);
        DrawEffect effect = new DrawEffect(3);

        effect.applyEffect(playArea);

        verify(playArea).visitDraw(3);
    }

    @Test
    void testApplyEffectWithLargeAmount() {
        PlayArea playArea = mock(PlayArea.class);
        DrawEffect effect = new DrawEffect(1000000);

        effect.applyEffect(playArea);

        verify(playArea).visitDraw(1000000);
    }
}
