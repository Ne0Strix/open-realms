/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.model;

import java.util.List;

/**
 * Represents the play area in the game. Stores the current state of the game and provides methods
 * for manipulating it.
 */
public class PlayArea {
    private int health;
    private int turnDamage;
    private int turnHealing;
    private int turnCoins;

    private Market market;
    private Deck<Card> playedCards;
    private Deck<Card> playedChampions;
    private PlayerCards playerCards;

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

    public void buyCard(Card card) throws IllegalArgumentException {
        if (this.turnCoins < card.getCost()) {
            throw new IllegalArgumentException("Not enough coins this turn");
        }
        turnCoins -= card.getCost();
        market.purchase(card);
        playerCards.addBoughtCard(card);
    }

    public int playCardById(int id) {
        Card card;
        for (Card c : playerCards.getHandCards()) {
            if (c.getId() == id) {
                card = c;
                playCard(card);
                return card.getId();
            }
        }
        return -1;
    }
}
