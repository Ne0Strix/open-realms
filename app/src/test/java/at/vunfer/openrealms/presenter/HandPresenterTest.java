package at.vunfer.openrealms.presenter;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import at.vunfer.openrealms.view.HandView;

public class HandPresenterTest {
    private HandView handView;
    private HandPresenter handPresenter;

    @Before
    public void setUp() {
        handView = Mockito.mock(HandView.class);
        handPresenter = new HandPresenter(handView);
    }

    @Test
    public void createFirstHand() {
        handPresenter.createFirstHand();

        // verify that the correct number of cards were created
        assert handPresenter.getCards().size() == 5;
    }
    @Test
    public void getCards() {
        handPresenter.createFirstHand();

        // verify that the list of cards is not null
        assert handPresenter.getCards() != null;
    }
}