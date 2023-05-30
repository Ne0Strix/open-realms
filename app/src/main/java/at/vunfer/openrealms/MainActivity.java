/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import at.vunfer.openrealms.model.Card;
import at.vunfer.openrealms.model.Market;
import at.vunfer.openrealms.model.PlayArea;
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
    private static final int connectionPort = 1337;
    private String connectionIP;
    private ServerThread server;
    private static ClientConnector connection;
    private static final Logger LOGGER = Logger.getLogger(MainActivity.class.getName());
    private static final String TAG = MainActivity.class.getSimpleName();
    private static List<CardView> cardViews;
    private boolean isHost = false;

    private MarketView marketView;
    private PlayAreaView playAreaView;
    private PlayAreaPresenter playAreaPresenter;
    private MarketPresenter marketPresenter;
    private Market market;
    private HandView handView;
    private HandPresenter handPresenter;
    private PlayArea playArea;
    public HandPresenter playerHandPresenter;
    public HandPresenter opponentHandPresenter;
    public DiscardPilePresenter playerDiscardPilePresenter;
    public DiscardPilePresenter opponentDiscardPilePresenter;
    public DeckPresenter playerDeckPresenter;
    public DeckPresenter opponentDeckPresenter;
    public OverlayPresenter overlayViewPresenter;

    private Context context = this;
    private int playerId;

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

    public void startServer(View view) {
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

    public void showIp(View view) {
        TextView showIp = (TextView) findViewById(R.id.prompt_text);
        Button button = (Button) findViewById(R.id.showIp);
        Button startButton = (Button) findViewById(R.id.startGame);

        connectionIP = server.getIpAddr();

        if (connectionIP != null) {
            button.setVisibility(View.GONE);
            showIp.setText(
                    "Your IP address is:\n"
                            + connectionIP
                            + "\n(Start after Guest\nhas joined and started)");
            startButton.setVisibility(View.VISIBLE);

            connection = new ClientConnector(this);
            connection.setConnectionTarget(connectionIP, connectionPort);
            connection.start();
        }else {
            Toast.makeText(this, "Failed to retrieve IP address. Please check your network connection.", Toast.LENGTH_SHORT).show();
        }
    }

    public void connectServer(View view) {
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

    public void startGame(View view) {
        setContentView(R.layout.activity_main);

        PlayArea.setContext(this);

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

        OverlayView overlayView = new OverlayView(this);
        overlayViewPresenter = new OverlayPresenter(overlayView);

        // Add views to layout
        ConstraintLayout layout = findViewById(R.id.game_area);

        Button endTurnButton = findViewById(R.id.end_turn_button);
        endTurnButton.setVisibility(View.INVISIBLE);

        layout.addView(overlayView.getOverlayView());

        // Flip the Text on opponent DiscardPile and Deck, to always be right-side up
        opponentDiscardPileView.findViewById(R.id.discardPileAmount).setScaleY(-1);
        opponentDiscardPileView.findViewById(R.id.discardPileAmountOutline).setScaleY(-1);
        opponentDeckView.findViewById(R.id.deck_view_amount).setScaleY(-1);
        opponentDeckView.findViewById(R.id.deck_view_amount_outline).setScaleY(-1);

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
        Log.i(
                TAG,
                "Received message of type: "
                        + message.getType()
                        + " Card ID: "
                        + message.getData(DataKey.CARD_ID)
                        + " Deck: "
                        + message.getData(DataKey.DECK));
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
                                    if (stats.getPlayerHealth() < 1) {
                                        //                                        //this adds the
                                        // defeat screen on top of the game
                                        ImageView defeatImage = findViewById(R.id.defeat_image);
                                        defeatImage.setVisibility(View.VISIBLE);
                                        defeatImage.getParent().bringChildToFront(defeatImage);
                                        Button endTurnButton = findViewById(R.id.end_turn_button);
                                        endTurnButton.setVisibility(View.GONE);
                                    }
                                } else {
                                    overlayViewPresenter.updateOpponentName(stats.getPlayerName());
                                    overlayViewPresenter.updateOpponentHealth(
                                            stats.getPlayerHealth());
                                    overlayViewPresenter.updateTurnDamage(stats.getTurnDamage());
                                    overlayViewPresenter.updateTurnHealing(stats.getTurnHealing());
                                    overlayViewPresenter.updateTurnCoin(stats.getTurnCoin());
                                    if (stats.getPlayerHealth() < 1) {
                                        ImageView victoryImage = findViewById(R.id.victory_image);
                                        victoryImage.setVisibility(View.VISIBLE);
                                        victoryImage.getParent().bringChildToFront(victoryImage);
                                        Button endTurnButton = findViewById(R.id.end_turn_button);
                                        endTurnButton.setVisibility(View.GONE);
                                    }
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
                            case TURN_NOTIFICATION:
                                if (findViewById(R.id.defeat_image).getVisibility() != View.VISIBLE
                                        && findViewById(R.id.victory_image).getVisibility()
                                                != View.VISIBLE) {
                                    Object targetPlayer = message.getData(DataKey.TARGET_PLAYER);
                                    if (targetPlayer != null) {
                                        Button endTurnButton = findViewById(R.id.end_turn_button);
                                        if (playerId == (Integer) targetPlayer) {
                                            endTurnButton.setVisibility(View.VISIBLE);
                                        } else {
                                            endTurnButton.setVisibility(View.INVISIBLE);
                                        }
                                    }
                                }
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
                playAreaPresenter.removeCardFromView(card);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_new_game) {
            Toast.makeText(this, "Neues Spiel gestartet", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_settings) {
            Toast.makeText(this, "Einstellungen geöffnet", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
