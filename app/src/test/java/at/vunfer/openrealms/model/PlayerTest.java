/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PlayerTest {
    private Player player;

    @BeforeEach
    void setUp() {
        PlayerCards playerCards = new PlayerCards();
        PlayArea playArea = new PlayArea(70, playerCards);
        player = new Player("Player 1", playArea);
    }

    @Test
    void testConstructor() {
        assertNotNull(player.getPlayArea());
        assertEquals("Player 1", player.getPlayerName());
    }

    @Test
    void testSetPlayerName() {
        player.setPlayerName("CoolerName");
        assertEquals("CoolerName", player.getPlayerName());
    }

    @Test
    void testGetPlayArea() {
        assertNotNull(player.getPlayArea());
    }

    @Test
    void testSetPlayArea() {
        PlayerCards newPlayerCards = new PlayerCards();
        PlayArea newPlayArea = new PlayArea(100, newPlayerCards);
        player.setPlayArea(newPlayArea);
        assertEquals(newPlayArea, player.getPlayArea());
    }

    @Test
    void testToString() {
        assertEquals("Player 1", player.toString());
    }
}
