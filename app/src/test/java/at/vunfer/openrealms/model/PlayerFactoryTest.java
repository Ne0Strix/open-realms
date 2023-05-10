/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class PlayerFactoryTest {

    @Test
    void testCreatePlayer() {
        Player player = PlayerFactory.createPlayer("John");
        player.getPlayArea()
                .getPlayerCards()
                .setDeckCards(player.getPlayArea().getPlayerCards().getOldTestDeck());
        assertEquals("John", player.getPlayerName());
        assertEquals(PlayerFactory.getInitialHealth(), player.getPlayArea().getHealth());
        assertEquals(
                PlayerCards.getHandSize(),
                player.getPlayArea().getPlayerCards().getHandCards().size());
    }
}
