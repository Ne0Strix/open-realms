/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.model;

/** A factory class for creating new Player instances. */
public class PlayerFactory {

    private PlayerFactory() {}

    private static final int INITIAL_HEALTH = 70;

    /**
     * Creates a new Player instance with the given player name, initializes their starting health
     * and provides them with a deck of cards and a play area.
     *
     * @param playerName The name of the player
     * @return A new Player instance
     */
    public static Player createPlayer(String playerName) {
        PlayerCards playerCards = new PlayerCards();
        PlayArea playArea = new PlayArea(INITIAL_HEALTH, playerCards);
        return new Player(playerName, playArea);
    }
}
