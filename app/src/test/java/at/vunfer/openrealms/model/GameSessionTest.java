/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.model;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertThrows;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GameSessionTest {

    private Player player1;
    private Player player2;
    private GameSession gameSession;
    List<Player> players;

    @BeforeEach
    void setUp() {
        player1 = PlayerFactory.createPlayer("Player 1");
        player2 = PlayerFactory.createPlayer("Player 2");
        players = Arrays.asList(player1, player2);
        gameSession = new GameSession(players, player1);
    }

    @Test
    void testGameSessionConstructorWithValidPlayers() {
        assertDoesNotThrow(() -> new GameSession(players, player1));
    }

    @Test
    void testGameSessionConstructorWithInvalidPlayers() {
        Player invalidPlayer = PlayerFactory.createPlayer("Invalid Player");
        assertThrows(IllegalArgumentException.class, () -> new GameSession(players, invalidPlayer));
    }

    @Test
    void testGetPlayers() {
        gameSession = new GameSession(players, player1);
        assertEquals(players, gameSession.getPlayers());
    }

    @Test
    public void testPlayersAfterNewSession() {
        assertEquals(2, gameSession.getPlayers().size());
        assertEquals(player1, gameSession.getCurrentPlayer());
    }

    @Test
    void testGetCurrentPlayer() {
        assertEquals(player1, gameSession.getCurrentPlayer());
    }

    @Test
    void testGetOpponent() {
        assertEquals(player2, gameSession.getOpponent(player1));
        assertEquals(player1, gameSession.getOpponent(player2));
    }

    @Test
    void testNextPlayer() {
        gameSession.nextPlayer();
        assertEquals(player2, gameSession.getCurrentPlayer());
    }

    @Test
    void testEndTurn() {
        int initialPlayer1Health = player1.getPlayArea().getHealth();
        int initialPlayer2Health = player2.getPlayArea().getHealth();
        player1.getPlayArea().visitDamage(5);
        assertEquals(player1.getPlayArea().getTurnDamage(), 5);
        player1.getPlayArea().visitHealing(3);
        assertEquals(player1.getPlayArea().getTurnHealing(), 3);
        gameSession.endTurn();
        assertEquals(player2, gameSession.getCurrentPlayer());
        assertEquals(initialPlayer2Health - 5, player2.getPlayArea().getHealth());
        assertEquals(initialPlayer1Health + 3, player1.getPlayArea().getHealth());
    }

    @Test
    void testDealDamage() {
        int initialPlayer2Health = player2.getPlayArea().getHealth();
        gameSession.dealDamage(player2, 5);
        assertEquals(initialPlayer2Health - 5, player2.getPlayArea().getHealth());
    }

    @Test
    void testHealPlayer() {
        int initialPlayer1Health = player1.getPlayArea().getHealth();
        gameSession.healPlayer(3);
        assertEquals(initialPlayer1Health + 3, player1.getPlayArea().getHealth());
    }

    @AfterEach
    public void teardown() {
        gameSession = null;
        player1 = null;
        player2 = null;
        players = null;
    }
}
