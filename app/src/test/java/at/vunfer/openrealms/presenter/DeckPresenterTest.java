/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.presenter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import at.vunfer.openrealms.view.CardView;
import at.vunfer.openrealms.view.DeckView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class DeckPresenterTest {

    private DeckView deckView;
    private DeckPresenter deckPresenter;
    private CardView testCard;

    @BeforeEach
    public void setUp() {
        deckView = Mockito.mock(DeckView.class);
        deckPresenter = new DeckPresenter(deckView);

        testCard = mock(CardView.class);
    }

    @Test
    public void testAddCardToView() {
        deckPresenter.addCardToView(testCard);

        assertEquals(1, deckPresenter.getListOfDisplayedCards().size());
        assertEquals(testCard, deckPresenter.getListOfDisplayedCards().get(0));
        verify(deckView).updateView(deckPresenter.getListOfDisplayedCards());
    }

    @Test
    public void testRemoveCardFromView() {
        deckPresenter.addCardToView(testCard);
        deckPresenter.removeCardFromView(testCard);

        assertEquals(0, deckPresenter.getListOfDisplayedCards().size());
        verify(deckView, times(2)).updateView(deckPresenter.getListOfDisplayedCards());
    }
}
