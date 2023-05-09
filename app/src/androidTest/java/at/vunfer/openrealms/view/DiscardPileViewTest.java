/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.view;

import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.test.platform.app.InstrumentationRegistry;
import at.vunfer.openrealms.R;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

public class DiscardPileViewTest {

    private DiscardPileView discardPileView;
    private Context context;

    @Before
    public void setUp() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        discardPileView = new DiscardPileView(context);
    }

    @Test
    public void testUpdateView() {
        CardView exampleView1 = new CardView(context);
        CardView exampleView2 = new CardView(context);
        CardView exampleView3 = new CardView(context);
        CardView wrongExampleView = new CardView(context);
        List<CardView> cardViews = List.of(exampleView1, exampleView2, exampleView3);
        discardPileView.updateView(List.of(wrongExampleView));

        discardPileView.updateView(cardViews);

        FrameLayout holder = discardPileView.findViewById(R.id.discardPileCardHolder);
        assertEquals(-1, holder.indexOfChild(wrongExampleView));
        assertEquals(-1, holder.indexOfChild(exampleView1));
        assertEquals(-1, holder.indexOfChild(exampleView2));
        assertEquals(0, holder.indexOfChild(exampleView3));

        TextView txtAmount = discardPileView.findViewById(R.id.discardPileAmount);
        assertEquals("3", txtAmount.getText());
    }

    @Test
    public void testUpdateViewEmpty() {
        CardView wrongExampleView = new CardView(context);
        List<CardView> cardViews = new ArrayList<>();
        discardPileView.updateView(List.of(wrongExampleView));

        discardPileView.updateView(cardViews);

        FrameLayout holder = discardPileView.findViewById(R.id.discardPileCardHolder);
        assertEquals(0, holder.getChildCount());

        TextView txtAmount = discardPileView.findViewById(R.id.discardPileAmount);
        assertEquals("0", txtAmount.getText());
    }
}
