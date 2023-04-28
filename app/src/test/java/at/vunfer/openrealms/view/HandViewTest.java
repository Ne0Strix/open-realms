package at.vunfer.openrealms.view;

import static org.junit.Assert.*;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;

import at.vunfer.openrealms.model.Card;
import at.vunfer.openrealms.model.Deck;

public class HandViewTest {
    private HandView handView;

    @Before
    public void setUp() throws Exception {
        handView = new HandView(null);
    }

    @Test
    public void getHandView() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        HandView handView = new HandView(context);

        assertNotNull(handView.getHandView());
    }

    @Test
    public void getView() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        HandView handView = new HandView(context);

        assertNotNull(handView.getView());
    }

    @Test
    public void createFirstHand() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        HandView handView = new HandView(context);
        Deck<Card> playerStarterCards = new Deck<>();

        // Add some cards to the deck
        playerStarterCards.add(new Card(context));
        playerStarterCards.add(new Card(context));
        playerStarterCards.add(new Card(context));

        handView.createFirstHand(playerStarterCards);

        // Verify that the correct number of cards were added to the HandView
        assertEquals(playerStarterCards.size(), handView.getHandView().getChildCount());
    }

}