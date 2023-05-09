/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.model;

import java.util.List;

public class GameSession {
    private List<Player> players;
    private Player currentPlayer;
    private Market market;

    /**
     * Constructs a GameSession with a list of players and the current player.
     *
     * @param players the list of players in the game session
     * @param currentPlayer the current player in the game session
     * @throws IllegalArgumentException if the current player is not part of the player list
     */
    public GameSession(List<Player> players, Player currentPlayer) throws IllegalArgumentException {
        if (!players.contains(currentPlayer)) {
            throw new IllegalArgumentException("Current player has to be part of playerlist");
        }
        this.players = players;
        this.currentPlayer = currentPlayer;
        market = Market.getInstance();
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public int getPlayerTurnNumber(Player player) {
        return players.indexOf(player);
    }

    /**
     * Returns the opponent of the current player.
     *
     * @param currentPlayer the current player
     * @return the opponent of the current player
     */
    public Player getOpponent(Player currentPlayer) {
        return players.get(
                ((players.indexOf(currentPlayer)) + 1)
                        % 2); // get opponent of current player (only for the 2 player version)
    }

    /** Sets the next player as the current player. */
    public void nextPlayer() {
        currentPlayer = players.get(((players.indexOf(currentPlayer)) + 1) % 2);
    }

    /**
     * Ends the turn of the current player and switches to the next player. Deals damage to the
     * opponent and heals the current player.
     */
    public void endTurn() {
        dealDamage(
                getOpponent(currentPlayer),
                currentPlayer
                        .getPlayArea()
                        .getTurnDamage()); // deal damage to player next in line, since in this
        // version there will only be 2 players
        healPlayer(currentPlayer.getPlayArea().getTurnHealing());
        currentPlayer.getPlayArea().resetTurnPool();
        market.restock();
        currentPlayer.getPlayArea().getPlayerCards().restockHand();
        nextPlayer();
    }

    /**
     * Deals damage to the opponent of the current player.
     *
     * @param opponent the opponent player
     * @param value the amount of damage to deal
     */
    public void dealDamage(Player opponent, int value) {
        opponent.getPlayArea().takeDamage(value);
    }

    /**
     * Heals the current player.
     *
     * @param value the amount of healing to apply
     */
    public void healPlayer(int value) {
        currentPlayer.getPlayArea().heal(value);
    }
}
