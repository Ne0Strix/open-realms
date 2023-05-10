/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.view;

import static org.junit.Assert.*;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import at.vunfer.openrealms.R;
import at.vunfer.openrealms.model.Card;
import at.vunfer.openrealms.model.effects.CoinEffect;
import at.vunfer.openrealms.model.effects.DamageEffect;
import at.vunfer.openrealms.model.effects.HealingEffect;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class CardViewTest {

    @Test
    public void testConstructCardView() {
        Context targetContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Card exampleCard =
                new Card(
                        "CardName",
                        4,
                        List.of(new CoinEffect(2), new DamageEffect(4), new HealingEffect(19)));

        CardView v = new CardView(targetContext, exampleCard);

        assertTrue(exampleCard.isIdentical(v.getCard()));
        assertEquals("CardName", ((TextView) (v.findViewById(R.id.card_view_name))).getText());
        assertEquals("4", ((TextView) (v.findViewById(R.id.card_view_cost))).getText());
    }

    @Test
    public void testGenerateCardViews() {
        Context targetContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        List<Card> cardList = new ArrayList<>();
        cardList.add(new Card("Card1", 1, List.of(new CoinEffect(1))));
        cardList.add(new Card("Card2", 2, List.of(new DamageEffect(2))));
        cardList.add(new Card("Card3", 3, List.of(new HealingEffect(3))));

        List<CardView> cardViewList = CardView.getViewFromCards(targetContext, cardList);

        for (int i = 0; i < cardList.size(); i++) {
            assertEquals(cardList.get(i).getName(), cardViewList.get(i).getCard().getName());
        }
    }

    @Test
    public void testFullscreenView() throws InterruptedException {
        Context targetContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Card exampleCard = new Card("CardName", 4, List.of(new CoinEffect(2)));

        CardView fullscreenView = new CardView(targetContext);
        fullscreenView.setId(R.id.fullscreen_card);

        CardView v = new CardView(targetContext, exampleCard);
        ((ViewGroup) v.getRootView()).addView(fullscreenView);

        MotionEvent down = MotionEvent.obtain(10, 10, MotionEvent.ACTION_DOWN, 0, 0, 0);
        MotionEvent up = MotionEvent.obtain(10, 510, MotionEvent.ACTION_UP, 0, 0, 0);

        v.onClick(down);
        assertTrue(v.isBeingHeld);
        Thread.sleep(500);
        assertEquals(View.VISIBLE, fullscreenView.getVisibility());
        v.onClick(up);
        assertFalse(v.isBeingHeld);
        assertEquals(View.INVISIBLE, fullscreenView.getVisibility());
    }

    @Test
    public void testShortClick() {
        Context targetContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Card exampleCard = new Card("CardName", 4, List.of(new CoinEffect(2)));

        CardView v = new CardView(targetContext, exampleCard);

        MotionEvent down = MotionEvent.obtain(10, 10, MotionEvent.ACTION_DOWN, 0, 0, 0);
        MotionEvent up = MotionEvent.obtain(10, 20, MotionEvent.ACTION_UP, 0, 0, 0);

        v.onClick(down);
        assertTrue(v.isBeingHeld);

        v.onClick(up);
        assertFalse(v.isBeingHeld);
    }

    @Test
    public void testAbortWithCancelAction() throws InterruptedException {
        Context targetContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Card exampleCard = new Card("CardName", 4, List.of(new CoinEffect(2)));

        CardView fullscreenView = new CardView(targetContext);
        fullscreenView.setId(R.id.fullscreen_card);

        CardView v = new CardView(targetContext, exampleCard);
        ((ViewGroup) v.getRootView()).addView(fullscreenView);

        MotionEvent down = MotionEvent.obtain(10, 10, MotionEvent.ACTION_DOWN, 0, 0, 0);
        MotionEvent up = MotionEvent.obtain(10, 510, MotionEvent.ACTION_CANCEL, 0, 0, 0);

        v.onClick(down);
        assertTrue(v.isBeingHeld);
        Thread.sleep(500);
        assertEquals(View.VISIBLE, fullscreenView.getVisibility());
        v.onClick(up);
        assertFalse(v.isBeingHeld);
        assertEquals(View.INVISIBLE, fullscreenView.getVisibility());
    }

    @Test
    public void testClickOnFaceDownCard() {
        Context targetContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Card exampleCard = new Card("CardName", 4, List.of(new CoinEffect(2)));

        CardView v = new CardView(targetContext, exampleCard);
        v.setFaceDown();

        MotionEvent down = MotionEvent.obtain(10, 10, MotionEvent.ACTION_DOWN, 0, 0, 0);

        v.onClick(down);
        assertFalse(v.isBeingHeld);
    }

    @Test
    public void testFaceUpOrDown() {
        Context targetContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        CardView view =
                new CardView(targetContext, new Card("Card1", 1, List.of(new CoinEffect(1))));
        ImageView backOfCard = view.findViewById(R.id.card_view_back_of_card);

        view.setFaceUp();
        assertEquals(View.INVISIBLE, backOfCard.getVisibility());

        view.setFaceDown();
        assertEquals(View.VISIBLE, backOfCard.getVisibility());

        view.setFaceUpOrDown(false);
        assertEquals(View.INVISIBLE, backOfCard.getVisibility());

        view.setFaceUpOrDown(true);
        assertEquals(View.VISIBLE, backOfCard.getVisibility());
    }
}
