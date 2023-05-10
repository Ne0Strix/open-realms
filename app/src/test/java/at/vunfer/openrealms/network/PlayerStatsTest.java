package at.vunfer.openrealms.network;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PlayerStatsTest {
    private PlayerStats playerStats;

    @BeforeEach
    void setUp() {
        playerStats = new PlayerStats("testPlayer", 100, 10, 5, 50);
    }

    @Test
    void testConstructor() {
        assertEquals("testPlayer", playerStats.getPlayerName());
        assertEquals(100, playerStats.getPlayerHealth());
        assertEquals(10, playerStats.getTurnDamage());
        assertEquals(5, playerStats.getTurnHealing());
        assertEquals(50, playerStats.getTurnCoin());
    }

    @Test
    void testSetPlayerName() {
        playerStats.setPlayerName("newPlayer");
        assertEquals("newPlayer", playerStats.getPlayerName());
    }

    @Test
    void testSetPlayerHealth() {
        playerStats.setPlayerHealth(90);
        assertEquals(90, playerStats.getPlayerHealth());
    }

    @Test
    void testSetTurnDamage() {
        playerStats.setTurnDamage(20);
        assertEquals(20, playerStats.getTurnDamage());
    }

    @Test
    void testSetTurnHealing() {
        playerStats.setTurnHealing(10);
        assertEquals(10, playerStats.getTurnHealing());
    }

    @Test
    void testSetTurnCoin() {
        playerStats.setTurnCoin(100);
        assertEquals(100, playerStats.getTurnCoin());
    }
}
