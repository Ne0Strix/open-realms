/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PlayAreaTest {
    private PlayerCards playerCards;
    private PlayArea playArea;
    private Player player1;
    private Player player2;
    private GameSession session;

    @BeforeEach
    public void setUp() {
        player1 = PlayerFactory.createPlayer("Player 1");
        player2 = PlayerFactory.createPlayer("Player 2");
        ArrayList<Player> playerList = new ArrayList<Player>();
        playerList.add(player1);
        playerList.add(player2);

        session = new GameSession(playerList, player1);
    }

    @AfterEach
    public void teardown() {
        session = null;
        player1 = null;
        player2 = null;
    }

    @Test
    void testBuyCardTooPoor() {
        Card toBuy = player1.getPlayArea().getMarket().getForPurchase().get(0);
        assertThrows(
                IllegalArgumentException.class,
                () -> {
                    player1.getPlayArea().buyCard(toBuy);
                });
    }

    @Test
    void testBuyCard() {

        Card toBuy = player1.getPlayArea().getMarket().getForPurchase().get(0);

        player1.getPlayArea().visitCoin(10);
        player1.getPlayArea().buyCard(toBuy);

        assertTrue(player1.getPlayArea().getPlayerCards().getDiscardedCards().contains(toBuy));
    }
}
