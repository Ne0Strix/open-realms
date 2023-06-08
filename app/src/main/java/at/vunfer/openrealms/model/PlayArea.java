/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.model;

import static android.content.Context.SENSOR_SERVICE;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.Toast;

import at.vunfer.openrealms.MainActivity;
import at.vunfer.openrealms.network.DataKey;
import at.vunfer.openrealms.network.Message;
import at.vunfer.openrealms.network.client.ClientConnector;

import java.util.List;

/**
 * Represents the play area in the game. Stores the current state of the game and provides methods
 * for manipulating it.
 */
public class PlayArea extends Thread {
    private int health;
    private int turnDamage;
    private int turnHealing;
    private int turnCoins;

    private final Market market;
    private final Deck<Card> playedCards;
    private final Deck<Card> playedChampions;
    private final PlayerCards playerCards;

    private SensorManager sensorManager;
    private Sensor accelerometerSensor;

    private static Context context;

    private boolean cheat = false;
    private ClientConnector clientConnector;
    private double accelerationCurrentValue;
    private double accelerationPreviousValue;

    /**
     * Constructs a new PlayArea object with the specified health and player cards. Initializes the
     * turn damage, healing, and coins to 0, and initializes the played cards and played champions
     * decks.
     *
     * @param health The health of the player.
     * @param playerCards The player's cards.
     */
    public PlayArea(int health, PlayerCards playerCards) {

        this.health = health;
        this.turnDamage = 0;
        this.turnHealing = 0;
        this.turnCoins = 0;
        this.playedCards = new Deck<>();
        this.playedChampions = new Deck<>();
        this.playerCards = playerCards;
        this.market = Market.getInstance();
        this.sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        this.accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    private synchronized void isTurnedOver(SensorManager sensorManager, Sensor accelerometerSensor) {
        SensorEventListener sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                accelerationCurrentValue = Math.sqrt((x * x + y * y + z * z));
                double changeInAcceleration = Math.abs(accelerationCurrentValue - accelerationPreviousValue);

                if (changeInAcceleration > 10) {
                    cheat = true;
                    //((MainActivity) context).runOnUiThread(() -> Toast.makeText(context, "Phone turned over (Cheat activated)", Toast.LENGTH_SHORT).show());
                    Log.d("SensorValues", "x: " + x + ", y: " + y + ", z: " + z);
                }
                accelerationPreviousValue = accelerationCurrentValue;
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                // Handles accuracy changes if needed
            }
        };

        sensorManager.registerListener(sensorEventListener, accelerometerSensor, SensorManager.SENSOR_DELAY_GAME);

        try {
            Thread.sleep(1000); // Sleeps for 100 milliseconds to allow sensor data to be captured
        } catch (InterruptedException e) {
            // Log.e("PlayArea", "InterruptedException occurred: " + e.getMessage());
            Thread.currentThread().interrupt();
        } finally {
            sensorManager.unregisterListener(sensorEventListener);
        }
    }

    /**
     * Sets the client connector for sending cheat messages.
     *
     * @param clientConnector The client connector to set.
     */
    public void setClientConnector(ClientConnector clientConnector) {
        this.clientConnector = clientConnector;
    }

    /**
     * Sets the context for the PlayArea.
     *
     * @param context The context to set.
     */
    public static void setContext(Context context) {
        PlayArea.context = context;
    }

    /**
     * Returns the health of the player.
     *
     * @return The health of the player.
     */
    public int getHealth() {
        return health;
    }

    /**
     * Returns the total turn damage.
     *
     * @return The total turn damage.
     */
    public int getTurnDamage() {
        return turnDamage;
    }

    /**
     * Returns the total turn healing.
     *
     * @return The total turn healing.
     */
    public int getTurnHealing() {
        return turnHealing;
    }

    /**
     * Returns the player's cards.
     *
     * @return The player's cards.
     */
    public PlayerCards getPlayerCards() {
        return playerCards;
    }

    /**
     * Returns the total turn coins.
     *
     * @return The total turn coins.
     */
    public int getTurnCoins() {
        return turnCoins;
    }

    /**
     * Returns the list of played cards.
     *
     * @return The list of played cards.
     */
    public List<Card> getPlayedCards() {
        return playedCards;
    }

    /**
     * Returns the list of played champions.
     *
     * @return The list of played champions.
     */
    public List<Card> getPlayedChampions() {
        return playedChampions;
    }

    /**
     * Returns the game market.
     *
     * @return The game market.
     */
    public Market getMarket() {
        return market;
    }

    /**
     * Plays the specified card from the player's hand. Adds the card to the played cards deck and
     * removes it from the player's hand.
     *
     * @param card The card to play.
     */
    public void playCard(Card card) {
        playedCards.add(playerCards.popFromHand(card));
        card.applyEffects(this);
        for (Card c : playedCards) {}
    }

    public void clearPlayedCards() {
        playerCards.getDiscardedCards().addAll(playedCards);
        playedCards.clear();
    }

    public void setCheat(boolean cheat) {
        this.cheat = cheat;
    }


    // commented out by since it is not used in first sprint
    //    public Card useCardAllyEffect(Card card) {
    //        return null;
    //    }
    //
    //    public Card useCardSacrificeEffect(Card card) {
    //        return null;
    //    }
    //
    //    public Card useCardExpendEffect() {
    //        return null;
    //    }
    //
    //    public Card attackChampion(Champion champion, PlayArea playArea) {
    //        return null;
    //    }
    //
    //    public Card championIsAttacked(Champion champion) {
    //        return null;
    //    }

    /** Resets the turn damage, healing, and coins to 0. */
    public void resetTurnPool() {
        this.turnDamage = 0;
        this.turnHealing = 0;
        this.turnCoins = 0;
    }

    /**
     * Increases the player's health by the specified value.
     *
     * @param value The value to increase the player's health by.
     */
    public void heal(int value) {
        health += value;
    }

    /**
     * Decreases the player's health by the specified value.
     *
     * @param value The value to decrease the player's health by.
     */
    public void takeDamage(int value) {
        health -= value;
    }

    /**
     * Adds the specified damage to the total turn damage.
     *
     * @param damage The damage to add.
     */
    public void visitDamage(int damage) {
        turnDamage += damage;
    }

    public void visitCoin(int coin) {
        turnCoins += coin;
    }

    public void visitHealing(int healing) {
        turnHealing += healing;
    }

    public boolean buyCard(Card card) {
        if (this.cheat) {
            market.purchase(card);
            playerCards.addBoughtCard(card);
            //((MainActivity) context).runOnUiThread(() -> Toast.makeText(context, "Card bought with cheat (Cheat deactivated)!", Toast.LENGTH_SHORT).show());
            return true;
        } else {
            if (this.turnCoins < card.getCost()) {
                throw new IllegalArgumentException("Insufficient coins to buy the card");
            }
            turnCoins -= card.getCost();
            market.purchase(card);
            playerCards.addBoughtCard(card);
            return true;
        }
    }

    public boolean playCardById(int id) {
        Card card = findCardById(playerCards.getHandCards(), id);
        if (card == null) {
            return false;
        }
        playCard(card);
        return true;
    }

    public boolean buyCardById(int id) {
        Card card = findCardById(market.getForPurchase(), id);
        if (card == null) {
            return false;
        }
        return buyCard(card);
    }

    private Card findCardById(List<Card> cards, int id) {
        for (Card c : cards) {
            if (c.getId() == id) {
                return c;
            }
        }
        return null;
    }

    public void buyCard(Message message) throws IllegalArgumentException {
        int cardId = Integer.parseInt(message.getData(DataKey.CARD_ID).toString());

        Card cardToBuy =
                market.getForPurchase().stream()
                        .filter(card -> card.getId() == cardId)
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("Card does not exist"));

        int cardCost = cardToBuy.getCost();

        boolean cheatActivated =
                Boolean.parseBoolean(message.getData(DataKey.CHEAT_ACTIVATE).toString());
        if (cheatActivated && this.cheat) {
            turnCoins += cardCost;
            Log.d("PlayArea", "Cheat activated. Added " + cardCost + " coins.");
        } else {
            processCardPurchase(cardToBuy, cardCost);
        }
    }

    private void processCardPurchase(Card cardToBuy, int cardCost) {
        if (turnCoins < cardCost) {
            throw new IllegalArgumentException("Not enough coins this turn");
        }
        turnCoins -= cardCost;
        market.purchase(cardToBuy);
        playerCards.addBoughtCard(cardToBuy);
    }

    @Override
    public void run() {

        sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
        if (sensorManager == null) {
            throw new NullPointerException("Sensor service is null");
        }

        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerometerSensor == null) {
            throw new NullPointerException("Accelerometer sensor is not available");
        }

        while (true) {
            try {
                isTurnedOver(sensorManager, accelerometerSensor);
                if (!cheat && clientConnector != null) {
                    clientConnector.sendCheatMessage();
                } else{
                    //showCheatToast(true);
                }
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (NullPointerException e) {
                Log.e("PlayArea", "NullPointerException occurred while checking phone orientation: " + e.getMessage());
            }
        }
    }

    /*private void showCheatToast(boolean isActivated) {
        ((MainActivity) context).runOnUiThread(() ->Toast.makeText(context, "Cheat " + (isActivated ? "activated (Phone turned over)" : "deactivated"), Toast.LENGTH_SHORT).show());
    }*/
}
