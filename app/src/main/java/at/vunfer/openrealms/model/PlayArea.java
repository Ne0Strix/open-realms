/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.model;

import android.util.Log;
import java.util.List;

/**
 * Represents the play area in the game. Stores the current state of the game and provides methods
 * for manipulating it.
 */
public class PlayArea {
    private static int idCounter = 0;
    private int health;
    private int turnDamage;
    private int turnHealing;
    private int turnCoins;
    private int id;
    private static final String TAG = "PlayArea";

    private Market market;
    private Deck<Card> playedCards;
    private Deck<Card> playedChampions;
    private PlayerCards playerCards;
    private Deck<Card> cardsThatUsedSynergies;
    private Deck<Card> atTurnEndDiscardedChampions;

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
        this.cardsThatUsedSynergies = new Deck<>();
        this.atTurnEndDiscardedChampions = new Deck<>();
        this.playerCards = playerCards;
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

    public Deck<Card> getAtTurnEndDiscardedChampions() {
        return atTurnEndDiscardedChampions;
    }

    /**
     * Plays the specified card from the player's hand. Adds the card to the played cards deck and
     * removes it from the player's hand.
     *
     * @param card The card to play.
     */
    public void playCard(Card card) {
        card.applyEffects(this);
        triggerSynergies(card);
        if (card instanceof Champion) {
            ((Champion) card).expend();
            playedChampions.add(playerCards.popFromHand(card));
        } else {
            playedCards.add(playerCards.popFromHand(card));
        }
    }

    private void triggerSynergies(Card card) {
        triggerSynergiesByType(card, playedCards);
        triggerSynergiesByType(card, playedChampions);
    }

    private void triggerSynergiesByType(Card card, Deck<Card> deck) {

        for (Card c : deck) {
            if (card.getFaction() != Faction.NONE
                    && c.getFaction() == card.getFaction()
                    && c != card) {

                if (!cardsThatUsedSynergies.contains(card)) {
                    card.applySynergyEffects(this);
                    cardsThatUsedSynergies.add(card);
                }

                if (!cardsThatUsedSynergies.contains(c)) {
                    if (c instanceof Champion && !((Champion) c).isExpended()) {
                        continue;
                    }
                    c.applySynergyEffects(this);
                    cardsThatUsedSynergies.add(c);
                }
            }
        }
    }

    public void clearPlayedCards() {
        playerCards.getDiscardedCards().addAll(playedCards);
        playedCards.clear();
    }

    public void clearCardsThatUsedSynergyEffect() {
        cardsThatUsedSynergies.clear();
    }

    //    public Card useCardSacrificeEffect(Card card) {
    //        return null;
    //    }
    //
    //    public Card useCardExpendEffect() {
    //        return null;
    //    }

    public boolean expendChampion(Champion champion) {
        if (champion.expend()) {
            Log.d(TAG, "expendChampion: " + champion.getName());
            champion.applyEffects(this);
            triggerSynergies(champion);
            return true;
        }
        return false;
    }

    public boolean attackChampion(Champion champion, PlayArea enemyPlayArea) {
        if (enemyPlayArea.championIsAttacked(champion, turnDamage)) {
            turnDamage -= champion.getHealth();
            return true;
        }
        return false;
    }

    public boolean championIsAttacked(Champion champion, int turnDamage) {
        if (champion.isKilled(turnDamage)) {
            playedChampions.remove(champion);
            playerCards.getDiscardedCards().add(champion);
            champion.reset();
            return true;
        }
        return false;
    }

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
        //        Log.d(TAG, "takeDamage: " + value);
        atTurnEndDiscardedChampions.clear();
        for (int i = playedChampions.size() - 1; i >= 0; i--) {
            Card c = playedChampions.get(i);
            if (value <= 0) {
                break;
            }
            if (((Champion) c).isGuard()) {
                //                Log.d(TAG, "takeDamage: " + c + " is guard");
                if (championIsAttacked((Champion) c, value)) {
                    //                    Log.d(TAG, "takeDamage: " + c + " is killed");
                    atTurnEndDiscardedChampions.add(c);
                    value -= ((Champion) c).getHealth();
                    //                    Log.d(TAG, "new takeDamage: " + value);
                } else {
                    //                    Log.d(TAG, "takeDamage: " + c + " protects the player!");
                    return;
                }
            }
        }
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

    public boolean buyCard(Card card) throws IllegalArgumentException {
        if (this.turnCoins < card.getCost()) {
            return false;
        }
        turnCoins -= card.getCost();
        market.purchase(card);
        playerCards.addBoughtCard(card);
        return true;
    }

    public int playCardById(int id) {
        Card card = findCardById(playerCards.getHandCards(), id);
        // Log.d("PlayArea", "Card,playCardById: " + card);
        if (card == null) {
            return 0;
        }
        playCard(card);
        if (card instanceof Champion) {
            return 2;
        }
        return 1;
    }

    public boolean buyCardById(int id) {
        Card card = findCardById(market.getForPurchase(), id);
        if (card == null) {
            return false;
        }
        return buyCard(card);
    }

    public boolean expendChampionById(int id) {
        Card card = findCardById(playedChampions, id);
        //        Log.d("PlayArea", "expendChampionById: " + card);
        if (card == null) {
            return false;
        }
        return expendChampion((Champion) card);
    }

    public boolean attackChampionById(int id, PlayArea enemyPlayArea) {
        Card card = findCardById(enemyPlayArea.playedChampions, id);
        if (card == null) {
            return false;
        }
        return attackChampion((Champion) card, enemyPlayArea);
    }

    public void resetChampions() {
        for (Card c : playedChampions) {
            ((Champion) c).reset();
        }
        //        Log.d("PlayArea", "resetChampions: " + playedChampions.size());
    }

    private Card findCardById(List<Card> cards, int id) {
        for (Card c : cards) {
            if (c.getId() == id) {
                return c;
            }
        }
        return null;
    }

    public int getId() {
        return id;
    }
}
