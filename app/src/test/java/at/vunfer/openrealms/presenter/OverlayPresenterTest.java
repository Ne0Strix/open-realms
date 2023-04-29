/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.presenter;

import static org.mockito.Mockito.verify;

import at.vunfer.openrealms.model.GameSession;
import at.vunfer.openrealms.view.viewInterfaces.OverlayViewInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class OverlayPresenterTest {
    private OverlayPresenter overlayPresenter;
    private OverlayViewInterface overlayViewInterface;
    private GameSession gameSession;

    @BeforeEach
    public void setUp() {
        overlayViewInterface = Mockito.mock(OverlayViewInterface.class);
        gameSession = Mockito.mock(GameSession.class);
        overlayPresenter = new OverlayPresenter(overlayViewInterface, gameSession);
    }

    @Test
    public void testUpdatePlayerName() {
        String playerName = "Player";
        overlayPresenter.updatePlayerName(playerName);
        verify(overlayViewInterface).setPlayerName(playerName);
    }

    @Test
    public void testUpdatePlayerHealth() {
        int playerHealth = 100;
        overlayPresenter.updatePlayerHealth(playerHealth);
        verify(overlayViewInterface).setPlayerHealth(playerHealth);
    }

    @Test
    public void testUpdateOpponentName() {
        String opponentName = "Opponent";
        overlayPresenter.updateOpponentName(opponentName);
        verify(overlayViewInterface).setOpponentName(opponentName);
    }

    @Test
    public void testUpdateOpponentHealth() {
        int opponentHealth = 100;
        overlayPresenter.updateOpponentHealth(opponentHealth);
        verify(overlayViewInterface).setOpponentHealth(opponentHealth);
    }

    @Test
    public void testUpdateTurnDamage() {
        int turnDamage = 10;
        overlayPresenter.updateTurnDamage(turnDamage);
        verify(overlayViewInterface).setTurnDamage(turnDamage);
    }

    @Test
    public void testUpdateTurnHealing() {
        int turnHealing = 5;
        overlayPresenter.updateTurnHealing(turnHealing);
        verify(overlayViewInterface).setTurnHealing(turnHealing);
    }

    @Test
    public void testUpdateTurnCoin() {
        int turnCoin = 20;
        overlayPresenter.updateTurnCoin(turnCoin);
        verify(overlayViewInterface).setTurnCoin(turnCoin);
    }
}
