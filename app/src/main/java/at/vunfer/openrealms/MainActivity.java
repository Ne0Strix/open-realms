/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;
import at.vunfer.openrealms.model.Card;
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
    private static final int Connection_Port = 1337;
    private String connectionIP;
    private ServerThread server;
    private static ClientConnector connection;
    private static final Logger LOGGER = Logger.getLogger(MainActivity.class.getName());
    private static final String TAG = MainActivity.class.getSimpleName();
    private static List<CardView> cardViews;
    private boolean isHost = false;

    private boolean ingameMusicPlaying = false;
    private boolean menuMusicPlaying = false;
    private static boolean myTurn = false;

    private final Context context = this;
    private int playerId;
    protected String playerName = "";
    public PlayAreaPresenter playAreaPresenter;
    public MarketPresenter marketPresenter;
    public HandPresenter playerHandPresenter;
    public HandPresenter opponentHandPresenter;
    public DiscardPilePresenter playerDiscardPilePresenter;
    public DiscardPilePresenter opponentDiscardPilePresenter;
    public DeckPresenter playerDeckPresenter;
    public DeckPresenter opponentDeckPresenter;
    public OverlayPresenter overlayViewPresenter;
    public PlayedChampionsPresenter playerPlayedChampionsPresenter;
    public PlayedChampionsPresenter opponentPlayedChampionsPresenter;

    ImageView endscreenImage;
    Button endTurnButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_view);

        // Initialize the VideoView
        VideoView videoView = findViewById(R.id.video_view);

        // Set the video file path or URL
        String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.intro_video;
        videoView.setVideoURI(Uri.parse(videoPath));

        // Set an OnPreparedListener to start playing the video when it's ready
        videoView.setOnPreparedListener(mediaPlayer -> videoView.start());

        // Set an OnCompletionListener to transition to the next layout after the video finishes
        videoView.setOnCompletionListener(mediaPlayer -> toMainMenu(new View(context)));

        SensorManager mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        ShakeDetector mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(
                new ShakeDetector.OnShakeListener() {

                    @Override
                    public void onShake() throws IOException {
                        sendCheatMessage();
                    }
                });

        mSensorManager.registerListener(
                mShakeDetector,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_UI);
    }

    public void hostGame(View view) {
        setupName();
        setContentView(R.layout.host);
        startServer(view);
    }

    public void joinGame(View view) {
        setupName();
        setContentView(R.layout.join);
        TextView outline = findViewById(R.id.enterHostPromptOutline);
        outline.getPaint().setStrokeWidth(5);
        outline.getPaint().setStyle(Paint.Style.STROKE);
    }

    public void toMainMenu(View view) {
        setContentView(R.layout.menu);
        if (!playerName.isEmpty()) {
            TextView nameInput = findViewById(R.id.name_chooser);
            nameInput.setText(playerName);
            playerName = "";
        }
        if (server != null) {
            try {
                server.stopServer();
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
            server = null;
        }
        TextView outline = findViewById(R.id.startGamePromptOutline);
        outline.getPaint().setStrokeWidth(5);
        outline.getPaint().setStyle(Paint.Style.STROKE);
        isHost = false;
        if (ingameMusicPlaying) {
            stopService(new Intent(this, OpenRealmsPlayer.class));
            ingameMusicPlaying = false;
        }
        if (!menuMusicPlaying) {
            startMenuMusic(true);
            menuMusicPlaying = true;
        }
    }

    public void setupName() {
        TextView nameBox = findViewById(R.id.name_chooser);
        playerName = nameBox.getText().toString();
    }

    public void startServer(View view) {
        isHost = true;
        server = new ServerThread(this, Connection_Port);

        TextView showIpOutline = (TextView) findViewById(R.id.ip_label_outline);
        TextView promptOutline = findViewById(R.id.server_prompt_outline);
        showIpOutline.getPaint().setStrokeWidth(5);
        showIpOutline.getPaint().setStyle(Paint.Style.STROKE);
        promptOutline.getPaint().setStrokeWidth(5);
        promptOutline.getPaint().setStyle(Paint.Style.STROKE);

        server.start();
    }

    public void showIp(View view) {
        runOnUiThread(
                () -> {
                    TextView showIp = (TextView) findViewById(R.id.ip_label);
                    TextView showIpOutline = (TextView) findViewById(R.id.ip_label_outline);

                    connectionIP = server.getIpAddr();

                    showIp.setText(connectionIP);
                    showIpOutline.setText(connectionIP);

                    connection = new ClientConnector(this);
                    connection.setConnectionTarget(connectionIP, Connection_Port);
                    connection.start();
                });
    }

    public void connectServer(View view) {
        EditText getIp = (EditText) findViewById(R.id.get_text);
        Button join = (Button) findViewById(R.id.join);

        join.setVisibility(View.INVISIBLE);
        connectionIP = getIp.getText().toString();
        Log.i(TAG, "Connecting to IP: " + connectionIP);
        connection = new ClientConnector(this);

        connection.setConnectionTarget(connectionIP, Connection_Port);
        connection.start();
    }

    public void clientCallback(boolean success) {
        runOnUiThread(
                () -> {
                    if (!isHost) {
                        Button join = (Button) findViewById(R.id.join);
                        join.setVisibility(View.VISIBLE);

                        if (!success) {
                            Toast.makeText(
                                            this,
                                            "Unable to make Connection. Please recheck IP-Address.",
                                            Toast.LENGTH_SHORT)
                                    .show();
                            return;
                        }
                        startGame(new View(this));
                    }
                });
    }

    public void startGame(View view) {
        // Start game music, that will loop, but stop when app is minimized
        if (menuMusicPlaying) {
            stopService(new Intent(this, OpenRealmsPlayer.class));
            menuMusicPlaying = false;
        }
        if (!ingameMusicPlaying) {
            startIngameMusic(true);
        }
        ingameMusicPlaying = true;

        setContentView(R.layout.activity_main);

        TextView outline = findViewById(R.id.waiting_for_server_label_outline);
        outline.getPaint().setStrokeWidth(5);
        outline.getPaint().setStyle(Paint.Style.STROKE);

        // Initialize views
        MarketView marketView = findViewById(R.id.market_view);
        PlayAreaView playAreaView = findViewById(R.id.play_area_view);
        HandView playerHandView = findViewById(R.id.player_hand_view);
        HandView opponentHandView = findViewById(R.id.opponent_hand_view);
        DiscardPileView playerDiscardPileView = findViewById(R.id.player_discard_pile_view);
        DiscardPileView opponentDiscardPileView = findViewById(R.id.opponent_discard_pile_view);
        DeckView playerDeckView = findViewById(R.id.player_deck_view);
        DeckView opponentDeckView = findViewById(R.id.opponent_deck_view);
        PlayedChampionsView playerPlayedChampionsView = findViewById(R.id.player_champions_view);
        PlayedChampionsView opponentPlayedChampionsView =
                findViewById(R.id.opponent_champions_view);
        OverlayView overlayView = findViewById(R.id.overlay_view);

        // Initialize presenter
        marketPresenter = new MarketPresenter(marketView);
        playerHandPresenter = new HandPresenter(playerHandView);
        opponentHandPresenter = new HandPresenter(opponentHandView);
        playAreaPresenter = new PlayAreaPresenter(playAreaView);
        playerDiscardPilePresenter = new DiscardPilePresenter(playerDiscardPileView);
        opponentDiscardPilePresenter = new DiscardPilePresenter(opponentDiscardPileView);
        playerDeckPresenter = new DeckPresenter(playerDeckView);
        opponentDeckPresenter = new DeckPresenter(opponentDeckView);
        playerPlayedChampionsPresenter = new PlayedChampionsPresenter(playerPlayedChampionsView);
        opponentPlayedChampionsPresenter =
                new PlayedChampionsPresenter(opponentPlayedChampionsView);
        overlayViewPresenter = new OverlayPresenter(overlayView);

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

        if (!playerName.isEmpty()) {
            Message nameChange = new Message(MessageType.NAME);
            nameChange.setData(DataKey.NAME, playerName);
            nameChange.setData(DataKey.TARGET_PLAYER, playerId);
            try {
                connection.sendMessage(nameChange);
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
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
                            case EXPEND_CHAMPION:
                                expendChampion(message);
                                Log.i(
                                        TAG,
                                        "Expended champion "
                                                + (int) message.getData(DataKey.CARD_ID)
                                                + ".");
                                break;
                            case RESET_CHAMPION:
                                resetChampion(message);
                                Log.i(TAG, "Reset champions.");
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
                                    if (stats.getPlayerHealth() < 1) {
                                        endGame(false);
                                    }
                                } else {
                                    overlayViewPresenter.updateOpponentName(stats.getPlayerName());
                                    overlayViewPresenter.updateOpponentHealth(
                                            stats.getPlayerHealth());
                                    if (stats.getPlayerHealth() < 1) {
                                        endGame(true);
                                    }
                                }
                                overlayViewPresenter.updateTurnDamage(stats.getTurnDamage());
                                overlayViewPresenter.updateTurnHealing(stats.getTurnHealing());
                                overlayViewPresenter.updateTurnCoin(stats.getTurnCoin());
                                break;
                            case FULL_CARD_DECK:
                                Log.i(TAG, "Received full card deck.");
                                cardViews =
                                        CardView.getViewFromCards(
                                                context,
                                                (List<Card>) message.getData(DataKey.COLLECTION));
                                findViewById(R.id.loading_screen).setVisibility(View.GONE);
                                Log.i(TAG, "Created CardViews from Cards.");
                                break;
                            case TURN_NOTIFICATION:
                                if (findViewById(R.id.end_screen).getVisibility() != View.VISIBLE) {
                                    Object targetPlayer = message.getData(DataKey.TARGET_PLAYER);
                                    if (targetPlayer != null) {
                                        Button endTurnButton = findViewById(R.id.end_turn_button);
                                        if (playerId == (Integer) targetPlayer) {
                                            endTurnButton.setVisibility(View.VISIBLE);
                                            myTurn = true;
                                        } else {
                                            endTurnButton.setVisibility(View.INVISIBLE);
                                            myTurn = false;
                                        }
                                    }
                                }
                                break;
                            case CHEAT:
                                boolean cheatActive =
                                        (boolean) message.getData(DataKey.CHEAT_ACTIVATE);
                                overlayViewPresenter.cheatingEnabled(cheatActive);
                                break;
                            case REMATCH:
                                startGame(new View(context));
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
            case CHAMPIONS:
                if (playerId == (int) message.getData(DataKey.TARGET_PLAYER)) {
                    playerPlayedChampionsPresenter.addCardToView(card);
                    playerPlayedChampionsPresenter.expendChampion(card);
                } else {
                    opponentPlayedChampionsPresenter.addCardToView(card);
                    opponentPlayedChampionsPresenter.expendChampion(card);
                }
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
            case CHAMPIONS:
                if (playerId == (int) message.getData(DataKey.TARGET_PLAYER)) {
                    playerPlayedChampionsPresenter.resetChampion(card);
                    playerPlayedChampionsPresenter.removeCardFromView(card);
                } else {
                    opponentPlayedChampionsPresenter.resetChampion(card);
                    opponentPlayedChampionsPresenter.removeCardFromView(card);
                }
        }
    }

    private void expendChampion(Message message) {
        CardView card = getCardViewFromCard((int) message.getData(DataKey.CARD_ID));

        if (card == null) {
            Log.i(TAG, "Card is null");
        }
        Log.v(TAG, "Target player: " + (int) message.getData(DataKey.TARGET_PLAYER));

        if (playerId == (int) message.getData(DataKey.TARGET_PLAYER)) {
            playerPlayedChampionsPresenter.expendChampion(card);
        } else {
            opponentPlayedChampionsPresenter.expendChampion(card);
        }
    }

    private void resetChampion(Message message) {
        CardView card = getCardViewFromCard((int) message.getData(DataKey.CARD_ID));

        if (playerId == (int) message.getData(DataKey.TARGET_PLAYER)) {
            playerPlayedChampionsPresenter.resetChampion(card);
            Log.v(TAG, "Resetting player champion");
        } else {
            opponentPlayedChampionsPresenter.resetChampion(card);
            Log.v(TAG, "Resetting opponent champion");
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

    public void endGame(boolean victory) {
        for (CardView c : cardViews) {
            c.setFaceDown();
        }

        String endGameText;
        int endGameTextColor;
        if (victory) {
            endscreenImage = findViewById(R.id.victory_image);
            endGameText = "VICTORY!";
            endGameTextColor = Color.GREEN;
        } else {
            endscreenImage = findViewById(R.id.defeat_image);
            endGameText = "DEFEAT!";
            endGameTextColor = Color.RED;
        }

        endscreenImage.setVisibility(View.VISIBLE);
        endTurnButton = findViewById(R.id.end_turn_button);
        endTurnButton.setVisibility(View.INVISIBLE);

        TextView outline = findViewById(R.id.endGameLabelOutline);
        outline.getPaint().setStrokeWidth(5);
        outline.getPaint().setStyle(Paint.Style.STROKE);
        outline.setText(endGameText);
        TextView label = findViewById(R.id.endGameLabel);
        label.setText(endGameText);
        label.setTextColor(endGameTextColor);

        findViewById(R.id.end_screen).setVisibility(View.VISIBLE);
    }

    public void sendRematchRequest(View v) throws IOException {
        Message rematchRequest = new Message(MessageType.REMATCH_REQUEST);
        connection.sendMessage(rematchRequest);
        findViewById(R.id.btn_rematch).setVisibility(View.INVISIBLE);
    }

    public static void sendMessage(Message msg) throws IOException {
        if (myTurn) {
            connection.sendMessage(msg);
        }
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
    protected void onDestroy() {
        stopService(new Intent(this, OpenRealmsPlayer.class));
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopService(new Intent(this, OpenRealmsPlayer.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ingameMusicPlaying) {
            startIngameMusic(false);
        }
        if (menuMusicPlaying) {
            startMenuMusic(false);
        }
    }

    public void startIngameMusic(boolean resetPosition) {
        startMusic(R.raw.background_music, resetPosition);
    }

    public void startMenuMusic(boolean resetPosition) {
        startMusic(R.raw.menu_music, resetPosition);
    }

    public void startMusic(int track, boolean resetPosition) {
        if (resetPosition) {
            SharedPreferences prefs =
                    getSharedPreferences(OpenRealmsPlayer.PREF_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(OpenRealmsPlayer.KEY_POSITION + track, 0);
            editor.apply();
        }
        Intent i = new Intent(this, OpenRealmsPlayer.class);
        i.putExtra("track", track);
        startService(i);
    }

    public void setGameStarted(boolean b) {
        ingameMusicPlaying = b;
    }

    public ImageView getEndscreenImage() {
        return endscreenImage;
    }

    public Button getEndTurnButton() {
        return endTurnButton;
    }

    public void sendCheatMessage() throws IOException {
        Message cheatMessage = new Message(MessageType.CHEAT);
        cheatMessage.setData(DataKey.CHEAT_ACTIVATE, true);
        if (myTurn) {
            connection.sendMessage(cheatMessage);
        }
    }

    public void uncoverCheat(View view) throws IOException {
        Log.i(TAG, "Uncovering cheat.");
        Message uncoverMessage = new Message(MessageType.UNCOVER_CHEAT);
        if (!myTurn) {
            connection.sendMessage(uncoverMessage);
        }
    }
}
