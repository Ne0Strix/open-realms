/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.presenter;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import at.vunfer.openrealms.model.Card;
import at.vunfer.openrealms.model.Deck;
import at.vunfer.openrealms.model.Market;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class MarketPresenterTest {

    private MarketPresenter marketPresenter;
    private MarketPresenter.View viewMock;

    @Before
    public void setUp() {
        marketPresenter = new MarketPresenter(null);
        viewMock = mock(MarketPresenter.View.class);
        marketPresenter.attachView(viewMock);
    }

    @Test
    public void onMarketChanged() {
        List<Card> cards = Arrays.asList(new Card("Card 1", 1, null), new Card("Card 2", 2, null));
        Deck<Card> deck = new Deck<>();
        deck.addAll(cards);
        Market.getInstance().setCards(deck);

        marketPresenter.onMarketChanged(deck);

        verify(viewMock).displayMarket(cards);
        assertEquals(cards, Market.getInstance().getCards());
    }

    @Test
    public void onCardPurchased() {
        Card card1 = new Card("Card 1", 1, null);
        Card card2 = new Card("Card 2", 2, null);
        List<Card> cards = Arrays.asList(card1, card2);
        Deck<Card> deck = new Deck<>();
        deck.addAll(cards);
        Market.getInstance().setCards(deck);

        marketPresenter.onCardPurchased(card1);

        List<Card> expectedCards = Arrays.asList(card2);
        assertEquals(expectedCards, Market.getInstance().getCards());
    }

    @Test
    public void getMarketData() {
        List<Card> cards = Arrays.asList(new Card("Card 1", 1, null), new Card("Card 2", 2, null));
        Deck<Card> deck = new Deck<>();
        deck.addAll(cards);
        Market.getInstance().setCards(deck);

        List<Card> result = marketPresenter.getMarketData();

        assertEquals(cards, result);
    }

    @Test
    public void testDisplayMarket() {
        List<Card> cards = Arrays.asList(new Card("Card 1", 1, null), new Card("Card 2", 2, null));

        marketPresenter.displayMarket(cards);

        verify(viewMock).displayMarket(cards);
    }
}
