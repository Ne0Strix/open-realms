/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.view;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.test.platform.app.InstrumentationRegistry;
import at.vunfer.openrealms.R;
import at.vunfer.openrealms.model.Card;
import at.vunfer.openrealms.model.CardType;
import at.vunfer.openrealms.model.effects.CoinEffect;
import at.vunfer.openrealms.model.effects.DamageEffect;
import at.vunfer.openrealms.model.effects.HealingEffect;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class CardViewTest {

    @Test
    public void testConstructCardView() {
        Context targetContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Card exampleCard =
                new Card(
                        "CardName",
                        4,
                        CardType.NONE,
                        List.of(new CoinEffect(2), new DamageEffect(4), new HealingEffect(19)),
                        List.of(new CoinEffect(2), new DamageEffect(4), new HealingEffect(19)));

        CardView v = new CardView(targetContext, exampleCard);

        assertTrue(exampleCard.isIdentical(v.getCard()));
        assertEquals("CardName", ((TextView) (v.findViewById(R.id.card_view_name))).getText());
        assertEquals("4", ((TextView) (v.findViewById(R.id.card_view_cost))).getText());
        assertEquals(View.INVISIBLE, v.findViewById(R.id.card_view_type_icon).getVisibility());
    }

    @Test
    public void testConstructCardViewTypeIcons() {
        Context targetContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        List<Card> cardList =
                List.of(
                        new Card("Wild", 4, CardType.WILD, List.of(new CoinEffect(2))),
                        new Card("Necros", 4, CardType.NECROS, List.of(new CoinEffect(2))),
                        new Card("Guild", 4, CardType.GUILD, List.of(new CoinEffect(2))),
                        new Card("Imperial", 4, CardType.IMPERIAL, List.of(new CoinEffect(2))));

        List<CardView> views = CardView.getViewFromCards(targetContext, cardList);

        for (CardView c : views) {
            assertEquals(View.VISIBLE, c.findViewById(R.id.card_view_type_icon).getVisibility());
        }
    }

    @Test
    public void testGenerateCardViews() {
        Context targetContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        List<Card> cardList = new ArrayList<>();
        cardList.add(new Card("Card1", 1, CardType.NONE, List.of(new CoinEffect(1))));
        cardList.add(new Card("Card2", 2, CardType.NONE, List.of(new DamageEffect(2))));
        cardList.add(new Card("Card3", 3, CardType.NONE, List.of(new HealingEffect(3))));

        List<CardView> cardViewList = CardView.getViewFromCards(targetContext, cardList);

        for (int i = 0; i < cardList.size(); i++) {
            assertEquals(cardList.get(i).getName(), cardViewList.get(i).getCard().getName());
        }
    }

    @Test
    public void testFullscreenView() throws InterruptedException {
        Context targetContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Card exampleCard = new Card("CardName", 4, CardType.NONE, List.of(new CoinEffect(2)));

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
        Card exampleCard = new Card("CardName", 4, CardType.NONE, List.of(new CoinEffect(2)));

        CardView v = spy(new CardView(targetContext, exampleCard));
        doNothing().when(v).sendTouchMessage();

        MotionEvent down = MotionEvent.obtain(10, 10, MotionEvent.ACTION_DOWN, 0, 0, 0);
        MotionEvent up = MotionEvent.obtain(10, 20, MotionEvent.ACTION_UP, 0, 0, 0);

        v.onClick(down);
        assertTrue(v.isBeingHeld);

        v.onClick(up);
        assertFalse(v.isBeingHeld);
        verify(v).sendTouchMessage();
    }

    @Test
    public void testAbortWithCancelAction() throws InterruptedException {
        Context targetContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Card exampleCard = new Card("CardName", 4, CardType.NONE, List.of(new CoinEffect(2)));

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
        Card exampleCard = new Card("CardName", 4, CardType.NONE, List.of(new CoinEffect(2)));

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
                new CardView(
                        targetContext,
                        new Card("Card1", 1, CardType.NONE, List.of(new CoinEffect(1))));
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

    @Test
    public void testGetCardId() {
        Context targetContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        Card c = new Card("Card1", 1, CardType.NONE, List.of(new CoinEffect(1)));
        CardView view = new CardView(targetContext, c);

        assertEquals(c.getId(), view.getCardId());
    }
}
