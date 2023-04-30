/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import at.vunfer.openrealms.model.Card;
import at.vunfer.openrealms.model.Deck;
import at.vunfer.openrealms.model.DeckGenerator;
import at.vunfer.openrealms.model.Market;
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

    private MarketView marketView;
    private PlayAreaView playAreaView;
    private MarketPresenter marketPresenter;
    private Market market;
    private HandView handView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        marketView = new MarketView(this);
        marketView.displayMarket(null);
        playAreaView = new PlayAreaView(this);
        handView = new HandView(this);

        Deck<Card> playerStarterCards =
                DeckGenerator.generatePlayerStarterDeck(getApplicationContext());

        List<CardView> cardViews = CardView.getViewFromCards(this, playerStarterCards);
        cardViews.remove(0);
        cardViews.remove(0);
        cardViews.remove(0);
        cardViews.remove(0);
        cardViews.remove(0);
        cardViews.remove(0);
        // Card with the longest name in the Original game
        cardViews.add(
                new CardView(
                        this,
                        new Card(
                                "Varrick, the Necromancer",
                                7,
                                List.of(
                                        new DamageEffect(2),
                                        new HealingEffect(4),
                                        new CoinEffect(12)))));
        // Card to test 2 Abilities
        cardViews.add(
                new CardView(
                        this,
                        new Card("Name", 10, List.of(new HealingEffect(4), new CoinEffect(12)))));
        handView.createFirstHand(cardViews);

        // Initialize presenter
        marketPresenter = new MarketPresenter(this);

        // Initialize market
        market = new Market();

        // Add views to layout
        ConstraintLayout layout = findViewById(R.id.play_area);
        layout.addView(marketView.getMarketView());
        layout.addView(playAreaView);
        layout.addView(handView.getHandView());

        LOGGER.log(Level.INFO, "Views initialized");
    }

    /** Method to update the market view */
    public void updateMarketView() {
        marketView.displayMarket(market.getCards());
    }

    /** Method to update the play area view */
    public void updatePlayAreaView() {
        playAreaView.updateView(market.toString());
    }

    public void showCardDialog(Context context, Card card) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.card_details_dialog);
        dialog.getWindow()
                .setLayout(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        // ImageView cardImage = dialog.findViewById(R.id.card_image);
        //  TextView cardName = dialog.findViewById(R.id.card_name);
        // TextView cardDescription = dialog.findViewById(R.id.card_description);

        //  cardImage.setImageResource(card.getImageResource());
        //   cardName.setText(card.getName());
        //  cardDescription.setText(card.getDescription());

        dialog.show();
    }

    public void showCardDetails(View v) {
        CardView cardImageView = (CardView) v;
        Card card = cardImageView.getCard();
        showCardDialog(this, card);
    }

    public void displayMarket(List<Card> market) {
        marketView.showMarket(market);
        LOGGER.log(Level.INFO, "Market displayed");
    }
}
