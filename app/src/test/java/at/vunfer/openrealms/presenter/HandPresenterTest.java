/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.presenter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import at.vunfer.openrealms.view.CardView;
import at.vunfer.openrealms.view.HandView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class HandPresenterTest {
    private HandView handView;
    private HandPresenter handPresenter;
    private CardView testCard;

    @BeforeEach
    public void setUp() {
        handView = Mockito.mock(HandView.class);
        handPresenter = new HandPresenter(handView);

        testCard = mock(CardView.class);
    }

    @Test
    void testAddCardToView() {
        handPresenter.addCardToView(testCard);

        assertEquals(1, handPresenter.getListOfDisplayedCards().size());
        assertEquals(testCard, handPresenter.getListOfDisplayedCards().get(0));
        verify(handView).updateView(handPresenter.getListOfDisplayedCards());
    }

    @Test
    void testRemoveCardFromView() {
        handPresenter.addCardToView(testCard);
        handPresenter.removeCardFromView(testCard);

        assertEquals(0, handPresenter.getListOfDisplayedCards().size());
        verify(handView, times(2)).updateView(handPresenter.getListOfDisplayedCards());
    }
}
