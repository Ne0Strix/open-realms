/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.view;

import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.test.platform.app.InstrumentationRegistry;
import at.vunfer.openrealms.R;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

public class DeckViewTest {

    private DeckView deckView;
    private Context context;

    @Before
    public void setUp() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        deckView = new DeckView(context);
    }

    @Test
    public void testUpdateView() {
        CardView exampleView1 = new CardView(context);
        CardView exampleView2 = new CardView(context);
        CardView exampleView3 = new CardView(context);
        CardView wrongExampleView = new CardView(context);
        List<CardView> cardViews = List.of(exampleView1, exampleView2, exampleView3);
        deckView.updateView(List.of(wrongExampleView));

        deckView.updateView(cardViews);

        ImageView backOfCard = deckView.findViewById(R.id.deck_view_back_of_card);
        assertEquals(View.VISIBLE, backOfCard.getVisibility());

        TextView txtAmount = deckView.findViewById(R.id.deck_view_amount);
        assertEquals("3", txtAmount.getText());
    }

    @Test
    public void testUpdateViewEmpty() {
        CardView wrongExampleView = new CardView(context);
        List<CardView> cardViews = new ArrayList<>();
        deckView.updateView(List.of(wrongExampleView));

        deckView.updateView(cardViews);

        ImageView backOfCard = deckView.findViewById(R.id.deck_view_back_of_card);
        assertEquals(View.INVISIBLE, backOfCard.getVisibility());

        TextView txtAmount = deckView.findViewById(R.id.deck_view_amount);
        assertEquals("0", txtAmount.getText());
    }
}
