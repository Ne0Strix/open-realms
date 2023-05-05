/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.presenter;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import at.vunfer.openrealms.view.CardView;
import at.vunfer.openrealms.view.PlayAreaView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class PlayAreaPresenterTest {

    private PlayAreaView playAreaView;
    private PlayAreaPresenter playAreaPresenter;
    private CardView testCard;

    @BeforeEach
    public void setUp() {
        playAreaView = Mockito.mock(PlayAreaView.class);
        playAreaPresenter = new PlayAreaPresenter(playAreaView);

        testCard = mock(CardView.class);
    }

    @Test
    public void testAddCardToView() {
        playAreaPresenter.addCardToView(testCard);

        assertEquals(1, playAreaPresenter.getListOfDisplayedCards().size());
        assertEquals(testCard, playAreaPresenter.getListOfDisplayedCards().get(0));
        verify(playAreaView).updateView(playAreaPresenter.getListOfDisplayedCards());
    }

    @Test
    public void testRemoveCardFromView() {
        playAreaPresenter.addCardToView(testCard);
        playAreaPresenter.removeCardFromView(testCard);

        assertEquals(0, playAreaPresenter.getListOfDisplayedCards().size());
        verify(playAreaView, times(2)).updateView(playAreaPresenter.getListOfDisplayedCards());
    }
}
