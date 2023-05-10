/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import at.vunfer.openrealms.model.Card;
import at.vunfer.openrealms.model.Deck;
import at.vunfer.openrealms.model.effects.CoinEffect;
import at.vunfer.openrealms.model.effects.DamageEffect;
import at.vunfer.openrealms.model.effects.HealingEffect;
import at.vunfer.openrealms.presenter.*;
import at.vunfer.openrealms.view.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity {
    private static final Logger LOGGER = Logger.getLogger(MainActivity.class.getName());
    private static final String TAG = MainActivity.class.getSimpleName();

    public PlayAreaPresenter playAreaPresenter;
    public MarketPresenter marketPresenter;
    public HandPresenter playerHandPresenter;
    public HandPresenter opponentHandPresenter;
    public DiscardPilePresenter playerDiscardPilePresenter;
    public DiscardPilePresenter opponentDiscardPilePresenter;
    public DeckPresenter playerDeckPresenter;
    public DeckPresenter opponentDeckPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        MarketView marketView = findViewById(R.id.market_view);
        PlayAreaView playAreaView = findViewById(R.id.play_area_view);
        HandView playerHandView = findViewById(R.id.player_hand_view);
        HandView opponentHandView = findViewById(R.id.opponent_hand_view);
        DiscardPileView playerDiscardPileView = findViewById(R.id.player_discard_pile_view);
        DiscardPileView opponentDiscardPileView = findViewById(R.id.opponent_discard_pile_view);
        DeckView playerDeckView = findViewById(R.id.player_deck_view);
        DeckView opponentDeckView = findViewById(R.id.opponent_deck_view);

        // Initialize presenter
        marketPresenter = new MarketPresenter(marketView);
        playerHandPresenter = new HandPresenter(playerHandView);
        opponentHandPresenter = new HandPresenter(opponentHandView);
        playAreaPresenter = new PlayAreaPresenter(playAreaView);
        playerDiscardPilePresenter = new DiscardPilePresenter(playerDiscardPileView);
        opponentDiscardPilePresenter = new DiscardPilePresenter(opponentDiscardPileView);
        playerDeckPresenter = new DeckPresenter(playerDeckView);
        opponentDeckPresenter = new DeckPresenter(opponentDeckView);

        // TODO: Remove this and replace it with Cards gotten from Server
        addPlaceholderCards();

        OverlayView overlayView = new OverlayView(this);

        // Add views to layout
        ConstraintLayout layout = findViewById(R.id.game_area);

        layout.addView(overlayView.getOverlayView());

        // Flip the Text on opponent DiscardPile and Deck, to always be right-side up
        opponentDiscardPileView.findViewById(R.id.discardPileAmount).setScaleY(-1);
        opponentDiscardPileView.findViewById(R.id.discardPileAmountOutline).setScaleY(-1);
        opponentDeckView.findViewById(R.id.deck_view_amount).setScaleY(-1);
        opponentDeckView.findViewById(R.id.deck_view_amount_outline).setScaleY(-1);

        LOGGER.log(Level.INFO, "Views initialized");
    }

    public void addPlaceholderCards() {
        // Add Cards to test functionality
        Deck<Card> playerStarterCards = new Deck<>();
        playerStarterCards.add(new Card("Gold", 0, List.of(new CoinEffect(1))));
        playerStarterCards.add(new Card("Gold", 0, List.of(new CoinEffect(1))));
        playerStarterCards.add(new Card("Shortsword", 0, List.of(new DamageEffect(1))));
        // Card with the longest name in the Original game and 3 Effects
        playerStarterCards.add(
                new Card(
                        "Varrick, the Necromancer",
                        7,
                        List.of(new DamageEffect(2), new HealingEffect(4), new CoinEffect(12))));
        // Card with 2 Effects
        playerStarterCards.add(
                new Card("Example", 10, List.of(new HealingEffect(4), new CoinEffect(12))));
        List<CardView> handCardViews = CardView.getViewFromCards(this, playerStarterCards);
        List<CardView> opponentCardViews = CardView.getViewFromCards(this, playerStarterCards);
        List<CardView> marketCardViews = CardView.getViewFromCards(this, playerStarterCards);
        List<CardView> playAreaCardViews = CardView.getViewFromCards(this, playerStarterCards);
        List<CardView> playerDiscardPileCardViews =
                CardView.getViewFromCards(this, playerStarterCards);
        List<CardView> opponentDiscardPileCardViews =
                CardView.getViewFromCards(this, playerStarterCards);
        List<CardView> playerDeckCardViews = CardView.getViewFromCards(this, playerStarterCards);
        List<CardView> opponentDeckCardViews = CardView.getViewFromCards(this, playerStarterCards);

        playerHandPresenter.addCardsToView(handCardViews);
        opponentHandPresenter.addCardsToView(opponentCardViews);
        marketPresenter.addCardsToView(marketCardViews);
        playAreaPresenter.addCardsToView(playAreaCardViews);
        playerDiscardPilePresenter.addCardsToView(playerDiscardPileCardViews);
        opponentDiscardPilePresenter.addCardsToView(opponentDiscardPileCardViews);
        playerDeckPresenter.addCardsToView(playerDeckCardViews);
        opponentDeckPresenter.addCardsToView(opponentDeckCardViews);
    }
}
