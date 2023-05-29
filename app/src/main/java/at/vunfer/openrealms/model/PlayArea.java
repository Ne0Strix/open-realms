/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.model;

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
    private Card cardDrawnFromSpecialAbility; // from special ability

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
        this.cardDrawnFromSpecialAbility = null;
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
        // Synergy effects:
        int numOfCardsWithSameType = 0;
        Card cardWithSameType = null;
        for (Card c : playedCards) {
            if (card.getType() != CardType.NONE && c.getType() == card.getType()) {
                numOfCardsWithSameType++;
                cardWithSameType = c;
            }
        }
        if (numOfCardsWithSameType > 0) {
            card.applySynergyEffects(this);
        }
        if (numOfCardsWithSameType == 1) {
            cardWithSameType.applySynergyEffects(this);
        }

        // Default effects:
        card.applyEffects(this);

        playedCards.add(playerCards.popFromHand(card));
        for (Card c : playedCards) {}
    }

    public void clearPlayedCards() {
        playerCards.getDiscardedCards().addAll(playedCards);
        playedCards.clear();
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

    public void visitCoin(int coin) {
        turnCoins += coin;
    }

    /**
     * Adds the specified damage to the total turn damage.
     *
     * @param damage The damage to add.
     */
    public void visitDamage(int damage) {
        turnDamage += damage;
    }

    public void visitDraw() {
        Card drawnCard = playerCards.drawRandomFromDeck();
        playerCards.addToHand(drawnCard);
        cardDrawnFromSpecialAbility = drawnCard;
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

    public int getId() {
        return id;
    }

    public void clearCardDrawnFromSpecialAbility() {
        cardDrawnFromSpecialAbility = null;
    }

    public Card getCardDrawnFromSpecialAbility() {
        return cardDrawnFromSpecialAbility;
    }

    public void resetCardDrawnFromSpecialAbility() {
        cardDrawnFromSpecialAbility = null;
    }
}
