/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.presenter;

import static org.mockito.Mockito.verify;

import at.vunfer.openrealms.view.view_interfaces.OverlayViewInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class OverlayPresenterTest {
    private OverlayPresenter overlayPresenter;
    private OverlayViewInterface overlayViewInterface;

    @BeforeEach
    void setUp() {
        overlayViewInterface = Mockito.mock(OverlayViewInterface.class);
        overlayPresenter = new OverlayPresenter(overlayViewInterface);
    }

    @Test
    void testUpdatePlayerName() {
        String playerName = "Player";
        overlayPresenter.updatePlayerName(playerName);
        verify(overlayViewInterface).setPlayerName(playerName);
    }

    @Test
    void testUpdatePlayerHealth() {
        int playerHealth = 100;
        overlayPresenter.updatePlayerHealth(playerHealth);
        verify(overlayViewInterface).setPlayerHealth(playerHealth);
    }

    @Test
    void testUpdateOpponentName() {
        String opponentName = "Opponent";
        overlayPresenter.updateOpponentName(opponentName);
        verify(overlayViewInterface).setOpponentName(opponentName);
    }

    @Test
    void testUpdateOpponentHealth() {
        int opponentHealth = 100;
        overlayPresenter.updateOpponentHealth(opponentHealth);
        verify(overlayViewInterface).setOpponentHealth(opponentHealth);
    }

    @Test
    void testUpdateTurnDamage() {
        int turnDamage = 10;
        overlayPresenter.updateTurnDamage(turnDamage);
        verify(overlayViewInterface).setTurnDamage(turnDamage);
    }

    @Test
    void testUpdateTurnHealing() {
        int turnHealing = 5;
        overlayPresenter.updateTurnHealing(turnHealing);
        verify(overlayViewInterface).setTurnHealing(turnHealing);
    }

    @Test
    void testUpdateTurnCoin() {
        int turnCoin = 20;
        overlayPresenter.updateTurnCoin(turnCoin);
        verify(overlayViewInterface).setTurnCoin(turnCoin);
    }
}
