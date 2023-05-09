/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.view;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import android.content.Context;
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
    public void testClickCard() {
        /*
                Context targetContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
                Card exampleCard = new Card("CardName", 4, List.of(new CoinEffect(2)));

                LinearLayout l = new LinearLayout(targetContext);
                CardView fullscreenView = spy(new CardView(targetContext));
                fullscreenView.setId(R.id.fullscreen_card);
                l.addView(fullscreenView);

                CardView v = new CardView(targetContext,exampleCard);
                CardView spyView = spy(v);
                when(spyView.getRootView()).thenReturn(l);

                onView(withId(R.id.card_view_background)).perform(ViewActions.long)
                // PowerMockito.verifyPrivate(v).invoke("applyCardDetail");

                //spyView.performClick();

                verify(fullscreenView).setVisibility(View.VISIBLE);
                // PowerMockito.verifyPrivate(v).invoke("setFullscreen");
        */
    }
}
