/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import at.vunfer.openrealms.model.Card;
import at.vunfer.openrealms.model.Deck;
import at.vunfer.openrealms.model.effects.CoinEffect;
import at.vunfer.openrealms.model.effects.DamageEffect;
import at.vunfer.openrealms.model.effects.HealingEffect;
import at.vunfer.openrealms.network.DataKey;
import at.vunfer.openrealms.network.DeckType;
import at.vunfer.openrealms.network.Message;
import at.vunfer.openrealms.network.MessageType;
import at.vunfer.openrealms.network.PlayerStats;
import at.vunfer.openrealms.network.client.ClientConnector;
import at.vunfer.openrealms.network.server.ServerThread;
import at.vunfer.openrealms.presenter.*;
import at.vunfer.openrealms.view.*;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity implements UIUpdateListener {
    private final int connectionPort = 1337;
    private String connectionIP;
    private ServerThread server;
    private static ClientConnector connection;
    private static final Logger LOGGER = Logger.getLogger(MainActivity.class.getName());
    private static final String TAG = MainActivity.class.getSimpleName();
    private static List<CardView> cardViews;
    private boolean isHost = false;

    private Context context = this;
    private int playerId;

    public PlayAreaPresenter playAreaPresenter;
    public MarketPresenter marketPresenter;
    public HandPresenter playerHandPresenter;
    public HandPresenter opponentHandPresenter;
    public DiscardPilePresenter playerDiscardPilePresenter;
    public DiscardPilePresenter opponentDiscardPilePresenter;
    public DeckPresenter playerDeckPresenter;
    public DeckPresenter opponentDeckPresenter;
    public OverlayPresenter overlayViewPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.menu);
    }

    public void hostGame(View view) {
        setContentView(R.layout.host);
    }

    public void joinGame(View view) {
        setContentView(R.layout.join);
    }

    public void toMainMenu(View view) throws IOException {
        setContentView(R.layout.menu);
        if (server != null) {
            server.stopServer();
        }
        isHost = false;
    }

    public void startServer(View view) throws InterruptedException {
        isHost = true;
        server = new ServerThread(this, connectionPort);
        TextView showIpPrompt = (TextView) findViewById(R.id.prompt_text);
        Button openLobbyButton = (Button) findViewById(R.id.openLobby);
        Button showIpButton = (Button) findViewById(R.id.showIp);

        showIpPrompt.setText("Tap the button below to get your IP address.");

        openLobbyButton.setVisibility(View.GONE);
        showIpButton.setVisibility(View.VISIBLE);

        server.start();
    }

    public void showIp(View view) throws IOException, InterruptedException {
        TextView showIp = (TextView) findViewById(R.id.prompt_text);
        Button button = (Button) findViewById(R.id.showIp);
        Button startButton = (Button) findViewById(R.id.startGame);

        connectionIP = server.getIpAddr();

        button.setVisibility(View.GONE);
        showIp.setText("Your IP address is:\n" + connectionIP);
        startButton.setVisibility(View.VISIBLE);

        connection = new ClientConnector(this);
        connection.setConnectionTarget(connectionIP, connectionPort);
        connection.start();
    }

    public void connectServer(View view) throws IOException, InterruptedException {
        EditText getIp = (EditText) findViewById(R.id.get_text);
        Button join = (Button) findViewById(R.id.join);
        Button start = (Button) findViewById(R.id.joinGameClient);

        connectionIP = getIp.getText().toString();
        Log.i(TAG, "Connecting to IP: " + connectionIP);
        connection = new ClientConnector(this);

        connection.setConnectionTarget(connectionIP, connectionPort);
        connection.start();

        join.setVisibility(View.GONE);
        start.setVisibility(View.VISIBLE);
    }

    public void startGame(View view) throws IOException {
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

        OverlayView overlayView = new OverlayView(this);
        overlayViewPresenter = new OverlayPresenter(overlayView);

        // Add views to layout
        ConstraintLayout layout = findViewById(R.id.game_area);

        layout.addView(overlayView.getOverlayView());

        LOGGER.log(Level.INFO, "Views initialized");

        if (isHost) {
            server.setupClients();
        }

        if (isHost) {
            this.playerId = 0;
        } else {
            this.playerId = 1;
        }
    }

    @Override
    public void updateUI(Message message) {
        Log.i(TAG, "Received message of type: " + message.getType());
        runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        switch (message.getType()) {
                            case ADD_CARD:
                                addCard(message);
                                Log.i(
                                        TAG,
                                        "Added card "
                                                + (int) message.getData(DataKey.CARD_ID)
                                                + " to deck "
                                                + message.getData(DataKey.DECK)
                                                + ".");

                                break;
                            case REMOVE_CARD:
                                removeCard(message);
                                Log.i(
                                        TAG,
                                        "Removed card "
                                                + (int) message.getData(DataKey.CARD_ID)
                                                + " from deck "
                                                + message.getData(DataKey.DECK)
                                                + ".");

                                break;
                            case CHOOSE_OPTION:
                                // TODO instructions for UI
                            case UPDATE_PLAYER_STATS:
                                int target = (int) message.getData(DataKey.TARGET_PLAYER);
                                PlayerStats stats =
                                        (PlayerStats) message.getData(DataKey.PLAYER_STATS);
                                if (playerId == target) {
                                    overlayViewPresenter.updatePlayerName(stats.getPlayerName());
                                    overlayViewPresenter.updatePlayerHealth(
                                            stats.getPlayerHealth());
                                    overlayViewPresenter.updateTurnDamage(stats.getTurnDamage());
                                    overlayViewPresenter.updateTurnHealing(stats.getTurnHealing());
                                    overlayViewPresenter.updateTurnCoin(stats.getTurnCoin());
                                } else {
                                    overlayViewPresenter.updateOpponentName(stats.getPlayerName());
                                    overlayViewPresenter.updateOpponentHealth(
                                            stats.getPlayerHealth());
                                }
                                break;
                            case FULL_CARD_DECK:
                                Log.i(TAG, "Received full card deck.");
                                cardViews =
                                        CardView.getViewFromCards(
                                                context,
                                                (List<Card>) message.getData(DataKey.COLLECTION));
                                Log.i(TAG, "Created CardViews from Cards.");
                                break;
                            default:
                                Log.i(TAG, "Received message of unknown type.");
                        }
                    }
                });
    }

    private void addCard(Message message) {
        CardView card = getCardViewFromCard((int) message.getData(DataKey.CARD_ID));

        DeckType deck = (DeckType) message.getData(DataKey.DECK);
        switch (deck) {
            case DECK:
                if (playerId == (int) message.getData(DataKey.TARGET_PLAYER)) {
                    playerDeckPresenter.addCardToView(card);
                } else {
                    opponentDeckPresenter.addCardToView(card);
                }
                break;
            case HAND:
                if (playerId == (int) message.getData(DataKey.TARGET_PLAYER)) {
                    playerHandPresenter.addCardToView(card);
                } else {
                    opponentHandPresenter.addCardToView(card);
                }
                break;
            case DISCARD:
                if (playerId == (int) message.getData(DataKey.TARGET_PLAYER)) {
                    playerDiscardPilePresenter.addCardToView(card);
                } else {
                    opponentDiscardPilePresenter.addCardToView(card);
                }
                break;
            case PLAYED:
                playAreaPresenter.addCardToView(card);
                break;
            case FOR_PURCHASE:
                marketPresenter.addCardToView(card);
                break;
        }
    }

    private void removeCard(Message message) {
        CardView card = getCardViewFromCard((int) message.getData(DataKey.CARD_ID));

        DeckType deck = (DeckType) message.getData(DataKey.DECK);
        switch (deck) {
            case DECK:
                if (playerId == (int) message.getData(DataKey.TARGET_PLAYER)) {
                    playerDeckPresenter.removeCardFromView(card);
                } else {
                    opponentDeckPresenter.removeCardFromView(card);
                }
                break;
            case HAND:
                if (playerId == (int) message.getData(DataKey.TARGET_PLAYER)) {
                    playerHandPresenter.removeCardFromView(card);
                } else {
                    opponentHandPresenter.removeCardFromView(card);
                }
                break;
            case DISCARD:
                if (playerId == (int) message.getData(DataKey.TARGET_PLAYER)) {
                    playerDiscardPilePresenter.removeCardFromView(card);
                } else {
                    opponentDiscardPilePresenter.removeCardFromView(card);
                }
                break;
            case PLAYED:
                if (playerId == (int) message.getData(DataKey.TARGET_PLAYER)) {
                    playAreaPresenter.removeCardFromView(card);
                }
                break;
            case FOR_PURCHASE:
                marketPresenter.removeCardFromView(card);
                break;
        }
    }

    private CardView getCardViewFromCard(int cardId) {
        for (CardView card : cardViews) {
            if (card.getCardId() == cardId) {
                return card;
            }
        }

        return null;
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

    public static void sendMessage(Message msg) throws IOException {
        connection.sendMessage(msg);
    }

    public static Message buildTouchMessage(int id) {
        Message message = new Message(MessageType.TOUCHED);
        message.setData(DataKey.CARD_ID, id);
        return message;
    }

    public void endTurn(View view) throws IOException {
        Message endTurn = new Message(MessageType.END_TURN);
        connection.sendMessage(endTurn);
    }
}
