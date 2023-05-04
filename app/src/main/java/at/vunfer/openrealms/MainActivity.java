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

    public static PlayAreaPresenter playAreaPresenter;
    public static MarketPresenter marketPresenter;
    public static HandPresenter handPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        MarketView marketView = findViewById(R.id.market_view);
        PlayAreaView playAreaView = findViewById(R.id.play_area);
        HandView handView = findViewById(R.id.hand_view);

        // Initialize presenter
        marketPresenter = new MarketPresenter(marketView);
        handPresenter = new HandPresenter(handView);
        playAreaPresenter = new PlayAreaPresenter(playAreaView);

        // Deck<Card> deck = new Deck<Card>();
        // handView.createFirstHand(deck);
        addPlaceholderCards();

        OverlayView overlayView = new OverlayView(this);

        // Add views to layout
        ConstraintLayout layout = findViewById(R.id.game_area);
        // layout.addView(marketView.getMarketView());
        // layout.addView(playAreaView);
        // layout.addView(handView);
        layout.addView(overlayView.getOverlayView());

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
        List<CardView> marketCardViews = CardView.getViewFromCards(this, playerStarterCards);

        handPresenter.addCardsToHandView(handCardViews);
        marketPresenter.addCardsToMarketView(marketCardViews);
    }
}
