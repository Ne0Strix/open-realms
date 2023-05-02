/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.view;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import at.vunfer.openrealms.R;
import at.vunfer.openrealms.model.Card;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class MarketViewTest {
    @Mock Context context;
    @Mock Card card;

    private MarketView marketView;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        marketView = new MarketView(context);
    }

    @Test
    public void showMarket() {
        List<Card> market = new ArrayList<>();
        market.add(card);

        when(card.getImageResource()).thenReturn(R.drawable.emptycards);
        when(card.getName()).thenReturn("Card1");
        when(card.getCost()).thenReturn(10);

        marketView.showMarket(market);

        LinearLayout marketLayout = marketView.getMarketView();
        assertEquals(1, marketLayout.getChildCount());

        View marketCardView = marketLayout.getChildAt(0);
        ImageView cardImage = marketCardView.findViewById(R.id.card_image);
        assertEquals(
                R.drawable.emptycards,
                cardImage.getDrawable().getConstantState().newDrawable().getConstantState());

        assertEquals(
                "Card1",
                ((TextView) marketCardView.findViewById(R.id.card_description))
                        .getText()
                        .toString());
        assertEquals(
                "10",
                ((TextView) marketCardView.findViewById(R.id.card_cost)).getText().toString());
    }

    @Test
    public void getSelectedCard() {
        Card selectedCard = new Card(context);
        marketView.selectedCard = selectedCard;
        assertEquals(selectedCard, marketView.getSelectedCard());
    }
}
