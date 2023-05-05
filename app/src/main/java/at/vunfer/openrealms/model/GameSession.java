/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.model;

import java.util.List;

public class GameSession {
    private List<Player> players;
    private Player currentPlayer;

    public GameSession(List<Player> players, Player currentPlayer) throws IllegalArgumentException {
        if (!players.contains(currentPlayer)) {
            throw new IllegalArgumentException("Current player has to be part of playerlist");
        }
        this.players = players;
        this.currentPlayer = currentPlayer;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public Player getOpponent(Player currentPlayer) {
        return players.get(
                ((players.indexOf(currentPlayer)) + 1)
                        % 2); // get opponent of current player (only for the 2 player version)
    }

    public void nextPlayer() {
        currentPlayer = players.get(((players.indexOf(currentPlayer)) + 1) % 2);
    }

    public void endTurn() {
        dealDamage(
                getOpponent(currentPlayer),
                currentPlayer
                        .getPlayArea()
                        .getTurnDamage()); // deal damage to player next in line, since in this
        // version there will only be 2 players
        healPlayer(currentPlayer.getPlayArea().getTurnHealing());
        currentPlayer.getPlayArea().resetTurnPool();
        Market.getInstance().restock();
        nextPlayer();
    }

    public void dealDamage(Player opponent, int value) {
        opponent.getPlayArea().takeDamage(value);
    }

    public void healPlayer(int value) {
        currentPlayer.getPlayArea().heal(value);
    }
}
