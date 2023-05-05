/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.view;

import static org.junit.Assert.assertEquals;

import android.content.Context;
import androidx.test.platform.app.InstrumentationRegistry;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

public class PlayAreaViewTest {

    private PlayAreaView playAreaView;
    private Context context;

    @Before
    public void setUp() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        playAreaView = new PlayAreaView(context);
    }

    @Test
    public void testUpdateView() {
        CardView exampleView1 = new CardView(context);
        CardView exampleView2 = new CardView(context);
        CardView exampleView3 = new CardView(context);
        CardView wrongExampleView = new CardView(context);
        List<CardView> cardViews = List.of(exampleView1, exampleView2, exampleView3);
        playAreaView.addView(wrongExampleView);

        playAreaView.updateView(cardViews);

        assertEquals(-1, playAreaView.indexOfChild(wrongExampleView));
        assertEquals(0, playAreaView.indexOfChild(exampleView1));
        assertEquals(1, playAreaView.indexOfChild(exampleView2));
        assertEquals(2, playAreaView.indexOfChild(exampleView3));
    }
}
