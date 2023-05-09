/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.presenter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import at.vunfer.openrealms.view.CardView;
import at.vunfer.openrealms.view.DiscardPileView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class DiscardPilePresenterTest {

    private DiscardPileView discardPileView;
    private DiscardPilePresenter discardPilePresenter;
    private CardView testCard;

    @BeforeEach
    public void setUp() {
        discardPileView = Mockito.mock(DiscardPileView.class);
        discardPilePresenter = new DiscardPilePresenter(discardPileView);

        testCard = mock(CardView.class);
    }

    @Test
    void testAddCardToView() {
        discardPilePresenter.addCardToView(testCard);

        assertEquals(1, discardPilePresenter.getListOfDisplayedCards().size());
        assertEquals(testCard, discardPilePresenter.getListOfDisplayedCards().get(0));
        verify(discardPileView).updateView(discardPilePresenter.getListOfDisplayedCards());
    }

    @Test
    void testRemoveCardFromView() {
        discardPilePresenter.addCardToView(testCard);
        discardPilePresenter.removeCardFromView(testCard);

        assertEquals(0, discardPilePresenter.getListOfDisplayedCards().size());
        verify(discardPileView, times(2))
                .updateView(discardPilePresenter.getListOfDisplayedCards());
    }
}
