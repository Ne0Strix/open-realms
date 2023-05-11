/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.view;

import static org.junit.Assert.*;

import android.content.Context;
import androidx.test.platform.app.InstrumentationRegistry;
import at.vunfer.openrealms.model.Card;
import at.vunfer.openrealms.model.effects.DamageEffect;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

public class HandViewTest {

    private HandView handView;
    private Context context;

    @Before
    public void setUp() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        handView = new HandView(context);
    }

    @Test
    public void testUpdateView() {
        Card c = new Card("CardName", 2, List.of(new DamageEffect(2)));
        CardView exampleView1 = new CardView(context, c);
        CardView exampleView2 = new CardView(context, c);
        CardView exampleView3 = new CardView(context, c);
        CardView wrongExampleView = new CardView(context, c);
        List<CardView> cardViews = List.of(exampleView1, exampleView2, exampleView3);
        handView.addView(wrongExampleView);

        handView.updateView(cardViews);

        assertEquals(-1, handView.indexOfChild(wrongExampleView));
        assertEquals(0, handView.indexOfChild(exampleView1));
        assertEquals(1, handView.indexOfChild(exampleView2));
        assertEquals(2, handView.indexOfChild(exampleView3));
    }
}
