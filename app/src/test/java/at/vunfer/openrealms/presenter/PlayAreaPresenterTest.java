/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.presenter;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import at.vunfer.openrealms.model.Card;
import at.vunfer.openrealms.view.PlayAreaView;
import org.junit.Before;
import org.junit.Test;

public class PlayAreaPresenterTest {
    private PlayAreaView view;
    private PlayAreaPresenter presenter;
    private Card card;

    @Before
    public void setUp() {
        view = mock(PlayAreaView.class);
        presenter = new PlayAreaPresenter(view);
        card = mock(Card.class);
    }

    @Test
    public void setText() {
        presenter.setText("Test Text");
        verify(view, times(1)).setText("Test Text");
    }

    @Test
    public void addCard() {
        presenter.addCard(card);
        assertEquals(1, presenter.getCards().size());
    }

    @Test
    public void updateView() {
        presenter.addCard(card);
        presenter.updateView("New Text");
        verify(view, times(1)).setText("New Text");
        verify(view, times(1)).invalidate();
    }
}
