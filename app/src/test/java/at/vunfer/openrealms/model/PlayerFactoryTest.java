/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class PlayerFactoryTest {

    @Test
    void testCreatePlayer() {
        Player player = PlayerFactory.createPlayer("John");
        assertEquals("John", player.getPlayerName());
        assertEquals(70, player.getPlayArea().getHealth());
        assertEquals(5, player.getPlayArea().getPlayerCards().getHandCards().size());
    }
}
