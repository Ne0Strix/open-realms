package at.vunfer.openrealms.view;

import static org.junit.Assert.*;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;

import at.vunfer.openrealms.model.Card;

public class CardImageViewTest {
    private Context context;
    private Card card;
    private CardImageView cardImageView;

    @Before
    public void setUp() throws Exception {
        context = InstrumentationRegistry.getInstrumentation().getContext();
        card = new Card(context);
        cardImageView = new CardImageView(context, card);
    }

    @Test
    public void testConstructor() {
        assertNotNull(cardImageView);
    }

    @Test
    public void setCard() {
        assertEquals(card, cardImageView.getCard());
    }

    @Test
    public void getCard() {
        Card newCard = new Card(context);
        cardImageView.setCard(newCard);
        assertEquals(newCard, cardImageView.getCard());
    }
}