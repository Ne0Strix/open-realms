/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.model;

import java.util.List;

public class PlayArea {
    private int health;
    private int turnDamage;
    private int turnHealing;
    private int turnCoins;

    private Market market;
    private Deck<Card> playedCards;
    private Deck<Card> playedChampions;
    private PlayerCards playerCards;

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

    public int getHealth() {
        return health;
    }

    public int getTurnDamage() {
        return turnDamage;
    }

    public int getTurnHealing() {
        return turnHealing;
    }

    public PlayerCards getPlayerCards() {
        return playerCards;
    }

    public int getTurnCoins() {
        return turnCoins;
    }

    public List<Card> getPlayedCards() {
        return playedCards;
    }

    public List<Card> getPlayedChampions() {
        return playedChampions;
    }

    public Market getMarket() {
        return market;
    }

    public void playCard(Card card) {
        playedCards.add(playerCards.popFromHand(card));
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

    public void resetTurnPool() {
        this.turnDamage = 0;
        this.turnHealing = 0;
        this.turnCoins = 0;
    }

    public void heal(int value) {
        health += value;
    }

    public void takeDamage(int value) {
        health -= value;
    }

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
}
