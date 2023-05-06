/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.view;

import static org.junit.Assert.*;

import android.content.Context;
import androidx.test.platform.app.InstrumentationRegistry;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

public class MarketViewTest {

    private MarketView marketView;
    private Context context;

    @Before
    public void setUp() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        marketView = new MarketView(context);
    }

    @Test
    public void testUpdateView() {
        CardView exampleView1 = new CardView(context);
        CardView exampleView2 = new CardView(context);
        CardView exampleView3 = new CardView(context);
        CardView wrongExampleView = new CardView(context);
        List<CardView> cardViews = List.of(exampleView1, exampleView2, exampleView3);
        marketView.addView(wrongExampleView);

        marketView.updateView(cardViews);

        assertEquals(-1, marketView.indexOfChild(wrongExampleView));
        assertEquals(0, marketView.indexOfChild(exampleView1));
        assertEquals(1, marketView.indexOfChild(exampleView2));
        assertEquals(2, marketView.indexOfChild(exampleView3));
    }
}
