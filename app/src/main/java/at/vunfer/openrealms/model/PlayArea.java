/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.model;

import java.util.ArrayList;
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
    private int id;

    private Market market;
    private Deck<Card> playedCards;
    private Deck<Card> playedChampions;
    private PlayerCards playerCards;
    private Deck<Card> cardDrawnFromSpecialAbility;
    private Deck<Card> cardsThatUsedSynergies;
    private Deck<Card> atTurnEndDiscardedChampions;
    private Deck<Card> drawnByCheat = new Deck<>();
    private boolean cheat = false;

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
        this.cardDrawnFromSpecialAbility = new Deck<>();
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
     * Sets the health of the player.
     *
     * @param health The health of the player.
     */
    public void setHealth(int health) {
        this.health = health;
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

    public boolean expendChampion(Champion champion) {
        if (champion.expend()) {
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
        atTurnEndDiscardedChampions.clear();
        for (int i = playedChampions.size() - 1; i >= 0; i--) {
            Card c = playedChampions.get(i);
            if (value <= 0) {
                break;
            }
            if (((Champion) c).isGuard()) {
                if (championIsAttacked((Champion) c, value)) {
                    atTurnEndDiscardedChampions.add(c);
                    value -= ((Champion) c).getHealth();
                } else {
                    return;
                }
            }
        }
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

    public void visitDraw(int amount) {
        for (int i = 0; i < amount; i++) {
            /*
            O Lord, I seek Your forgiveness for this flawed code.
            Grant me wisdom to rectify its errors and improve its structure.
            May I learn from this mistake, develop cleaner code, and honor You through my diligence.
            Guide me to create efficient and maintainable systems.
            Amen.
             */
            int numOfRestocked = 0;
            if (playerCards.getRestockedFromDiscarded() != null)
                numOfRestocked = playerCards.getRestockedFromDiscarded().size();
            Card drawnCard = playerCards.drawRandomFromDeck();
            int newNumOfRestocked = 0;
            if (playerCards.getRestockedFromDiscarded() != null)
                newNumOfRestocked = playerCards.getRestockedFromDiscarded().size();

            if (newNumOfRestocked != numOfRestocked) {
                cardDrawnFromSpecialAbility.add(
                        new Card("PLACEHOLDER", 0, Faction.NONE, new ArrayList<>()));
            }
            playerCards.addToHand(drawnCard);
            cardDrawnFromSpecialAbility.add(drawnCard);
        }
    }

    public void visitHealing(int healing) {
        turnHealing += healing;
    }

    public void visitDamagePerGuardInPlay(int damagePerGuard) {
        for (Card c : playedChampions) {
            if (((Champion) c).isGuard()) {
                visitDamage(damagePerGuard);
            }
        }
    }

    public void visitDamagePerChampionInPlay(int damagePerChampion) {
        visitDamage(damagePerChampion * playedChampions.size());
    }

    public void visitHealingPerChampionInPlay(int healingPerChampion) {
        visitHealing(healingPerChampion * playedChampions.size());
    }

    public boolean buyCard(Card card) throws IllegalArgumentException {
        if (!cheat && this.turnCoins < card.getCost()) {
            return false;
        }
        if (cheat) {
            addToDrawnByCheat(card);
        } else {
            turnCoins -= card.getCost();
        }
        market.purchase(card);
        playerCards.addBoughtCard(card);
        return true;
    }

    public int playCardById(int id) {
        Card card = findCardById(playerCards.getHandCards(), id);
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

    public Deck<Card> getCardDrawnFromSpecialAbility() {
        return cardDrawnFromSpecialAbility;
    }

    public void resetCardDrawnFromSpecialAbility() {

        cardDrawnFromSpecialAbility.clear();
    }

    public void setCheat(boolean cheat) {
        this.cheat = cheat;
    }

    public void addToDrawnByCheat(Card card) {
        drawnByCheat.add(card);
    }

    public void clearDrawnByCheat() {
        drawnByCheat.clear();
    }

    public Deck<Card> getDrawnByCheat() {
        return drawnByCheat;
    }

    public void destroyDrawnByCheat() {
        for (Card c : drawnByCheat) {
            playerCards.getDiscardedCards().remove(c);
        }
    }

    public boolean getCheat() {
        return cheat;
    }
}
