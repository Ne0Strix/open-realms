/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.model;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.List;

import at.vunfer.openrealms.network.DataKey;
import at.vunfer.openrealms.network.Message;

/**
 * Represents the play area in the game. Stores the current state of the game and provides methods
 * for manipulating it.
 */
public class PlayArea extends Thread {
    private int health;
    private int turnDamage;
    private int turnHealing;
    private int turnCoins;

    private Market market;
    private final Deck<Card> playedCards;
    private final Deck<Card> playedChampions;
    private final PlayerCards playerCards;
    private final Context context;

    private boolean cheat;

    /**
     * Constructs a new PlayArea object with the specified health and player cards. Initializes the
     * turn damage, healing, and coins to 0, and initializes the played cards and played champions
     * decks.
     *
     * @param health      The health of the player.
     * @param playerCards The player's cards.
     */
    public PlayArea(int health, PlayerCards playerCards, Context context) {

        this.health = health;
        this.turnDamage = 0;
        this.turnHealing = 0;
        this.turnCoins = 0;
        this.playedCards = new Deck<>();
        this.playedChampions = new Deck<>();
        this.playerCards = playerCards;
        this.market = Market.getInstance();
        this.context = context;
        this.market = Market.getInstance();
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

    /**
     * Resets the turn damage, healing, and coins to 0.
     */
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

    public void buyCard(Card card) throws IllegalArgumentException {
        if (this.turnCoins < card.getCost()) {
            throw new IllegalArgumentException("Not enough coins this turn");
        }
        turnCoins -= card.getCost();
        market.purchase(card);
        playerCards.addBoughtCard(card);
    }

    public void buyCard(Message message) throws IllegalArgumentException {
        Card cardToBuy = this.market.forPurchase.stream()
                .filter(card -> card.getId() == (Integer) message.getData(DataKey.CARD_ID))
                .findFirst().orElse(null);

        if (cardToBuy != null) {
            if (this.turnCoins < cardToBuy.getCost()) {
                throw new IllegalArgumentException("Not enough coins this turn");
            }
            if ((Boolean) message.getData(DataKey.CHEAT_ACTIVATE)) {
                // Check if phone is turned over or upside down (cheating condition)
                if (this.cheat) {
                    turnCoins += cardToBuy.getCost();
                }
                turnCoins -= cardToBuy.getCost();
                market.purchase(cardToBuy);
                playerCards.addBoughtCard(cardToBuy);
            } else {
                throw new IllegalArgumentException("Card does not exist");
            }
        }
    }

    public boolean isPhoneTurnedOver() {
        // Get the sensor service
        SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

        // Check if the device has an accelerometer sensor
        if (sensorManager != null && sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            SensorEventListener sensorEventListener = new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {
                    float x = event.values[0];
                    float y = event.values[1];
                    float z = event.values[2];

                    // Calculate the device orientation
                    double magnitude = Math.sqrt(x * x + y * y + z * z);
                    double gravity = SensorManager.GRAVITY_EARTH;
                    double delta = Math.abs(gravity - magnitude);

                    // Check if the phone is turned over or upside down
                    if (delta > 2.0) {
                    } else {
                    }
                }
                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {
                }
            };
            Sensor accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(sensorEventListener, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        return false;
    }

    @Override
    public void run() {
        while (true) {
            this.cheat = this.isPhoneTurnedOver();
        }
    }
}
