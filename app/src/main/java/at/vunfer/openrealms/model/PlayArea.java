/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.model;

import java.util.ArrayList;
import java.util.List;

public class PlayArea {
    private int health;
    private int turnDamage;
    private int turnHealing;
    private int turnCoins;
    private List<Card> playedCards;
    private List<Card> playedChampions;
    private PlayerCards playerCards;

    public PlayArea(
            int health,
            PlayerCards playerCards) {

        this.health = health;
        this.turnDamage = 0;
        this.turnHealing = 0;
        this.turnCoins = 0;
        this.playedCards = new ArrayList<Card>();
        this.playedChampions = new ArrayList<Card>();
        this.playerCards = playerCards;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getTurnDamage() {
        return turnDamage;
    }

    public void setTurnDamage(int turnDamage) {
        this.turnDamage = turnDamage;
    }

    public int getTurnHealing() {
        return turnHealing;
    }

    public void setTurnHealing(int turnHealing) {
        this.turnHealing = turnHealing;
    }

    public int getTurnCoins() {
        return turnCoins;
    }

    public void setTurnCoins(int turnCoins) {
        this.turnCoins = turnCoins;
    }

    public List<Card> getPlayedCards() {
        return playedCards;
    }

    public void setPlayedCards(List<Card> playedCards) {
        this.playedCards = playedCards;
    }

    public List<Card> getPlayedChampions() {
        return playedChampions;
    }

    public void setPlayedChampions(List<Card> playedChampions) {
        this.playedChampions = playedChampions;
    }

    public PlayerCards getPlayerCards() {
        return playerCards;
    }

    public void setPlayerCards(PlayerCards playerCards) {
        this.playerCards = playerCards;
    }

    public Card playCard(Card card) {
        if (playerCards.playCard(card) != null) {
            playedCards.add(card);
            useCardEffect(card);
            return card;
        }
        return null;
    }

    public void useCardEffect(Card card) {
        card.getAbility().resolveAbility(this);
    }

    public Card useCardAllyEffect(Card card) {
        return null;
    }

    public Card useCardSacrificeEffect(Card card) {
        return null;
    }

    public Card useCardExpendEffect() {

        return null;
    }

    public Card attackChampion(Champion champion, PlayArea playArea) {
        return null;
    }

    public Card championIsAttacked(Champion champion) {
        return null;
    }

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
}
