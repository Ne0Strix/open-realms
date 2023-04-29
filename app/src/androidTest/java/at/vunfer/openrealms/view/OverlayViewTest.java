/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.view;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.widget.TextView;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import at.vunfer.openrealms.R;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class OverlayViewTest {
    private OverlayView overlayView;

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        overlayView = new OverlayView(context);
    }

    @Test
    public void testSetPlayerName() {
        String playerName = "Player";
        overlayView.setPlayerName(playerName);
        TextView playerNameTextView = overlayView.getOverlayView().findViewById(R.id.playerName);
        assertEquals(playerName, playerNameTextView.getText().toString());
        assertThat(overlayView.getPlayerName(), is(playerName));
    }

    @Test
    public void testSetPlayerHealth() {
        int playerHealth = 100;
        overlayView.setPlayerHealth(playerHealth);
        TextView playerHealthTextView =
                overlayView.getOverlayView().findViewById(R.id.playerHealth);
        assertEquals(String.valueOf(playerHealth), playerHealthTextView.getText().toString());
        assertThat(overlayView.getPlayerHealth(), is(Integer.toString(playerHealth)));
    }

    @Test
    public void testSetOpponentName() {
        String opponentName = "Opponent";
        overlayView.setOpponentName(opponentName);
        TextView opponentNameTextView =
                overlayView.getOverlayView().findViewById(R.id.opponentName);
        assertEquals(opponentName, opponentNameTextView.getText().toString());
        assertThat(overlayView.getOpponentName(), is(opponentName));
    }

    @Test
    public void testSetOpponentHealth() {
        int opponentHealth = 100;
        overlayView.setOpponentHealth(opponentHealth);
        TextView opponentHealthTextView =
                overlayView.getOverlayView().findViewById(R.id.opponentHealth);
        assertEquals(String.valueOf(opponentHealth), opponentHealthTextView.getText().toString());
        assertThat(overlayView.getOpponentHealth(), is(Integer.toString(opponentHealth)));
    }

    @Test
    public void testSetTurnDamage() {
        int turnDamage = 10;
        overlayView.setTurnDamage(turnDamage);
        TextView turnDamageTextView = overlayView.getOverlayView().findViewById(R.id.turnDamage);
        assertEquals(String.valueOf(turnDamage), turnDamageTextView.getText().toString());
        assertThat(overlayView.getTurnDamage(), is(Integer.toString(turnDamage)));
    }

    @Test
    public void testSetTurnHealing() {
        int turnHealing = 5;
        overlayView.setTurnHealing(turnHealing);
        TextView turnHealingTextView = overlayView.getOverlayView().findViewById(R.id.turnHealing);
        assertEquals(String.valueOf(turnHealing), turnHealingTextView.getText().toString());
        assertThat(overlayView.getTurnHealing(), is(Integer.toString(turnHealing)));
    }

    @Test
    public void testSetTurnCoin() {
        int turnCoin = 20;
        overlayView.setTurnCoin(turnCoin);
        TextView turnCoinTextView = overlayView.getOverlayView().findViewById(R.id.turnCoin);
        assertEquals(String.valueOf(turnCoin), turnCoinTextView.getText().toString());
        assertThat(overlayView.getTurnCoin(), is(Integer.toString(turnCoin)));
    }
}
