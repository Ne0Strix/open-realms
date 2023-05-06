/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.presenter.presenter_interfaces;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import at.vunfer.openrealms.view.CardView;
import java.util.List;
import org.junit.jupiter.api.Test;

class CardPilePresenterTest {

    @Test
    void testAddCardsToView() {
        CardView c1 = mock(CardView.class);
        CardView c2 = mock(CardView.class);
        CardView c3 = mock(CardView.class);

        CardPilePresenter c = mock(CardPilePresenter.class);
        doCallRealMethod().when(c).addCardsToView(any());

        c.addCardsToView(List.of(c1, c2, c3));

        verify(c).addCardToView(c1);
        verify(c).addCardToView(c2);
        verify(c).addCardToView(c3);
    }

    @Test
    void testFindViewByCardId() {
        // TODO: implement CardID
    }
}
