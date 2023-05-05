/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.presenter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import at.vunfer.openrealms.view.CardView;
import at.vunfer.openrealms.view.MarketView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class MarketPresenterTest {
    private MarketView marketView;
    private MarketPresenter marketPresenter;
    private CardView testCard;

    @BeforeEach
    public void setUp() {
        marketView = Mockito.mock(MarketView.class);
        marketPresenter = new MarketPresenter(marketView);

        testCard = mock(CardView.class);
    }

    @Test
    void testAddCardToView() {
        marketPresenter.addCardToView(testCard);

        assertEquals(1, marketPresenter.getListOfDisplayedCards().size());
        assertEquals(testCard, marketPresenter.getListOfDisplayedCards().get(0));
        verify(marketView).updateView(marketPresenter.getListOfDisplayedCards());
    }

    @Test
    void testRemoveCardFromView() {
        marketPresenter.addCardToView(testCard);
        marketPresenter.removeCardFromView(testCard);

        assertEquals(0, marketPresenter.getListOfDisplayedCards().size());
        verify(marketView, times(2)).updateView(marketPresenter.getListOfDisplayedCards());
    }
}
