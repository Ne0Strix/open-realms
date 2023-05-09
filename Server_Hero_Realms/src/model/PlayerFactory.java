/* Licensed under GNU GPL v3.0 (C) 2023 */
package model;

public class PlayerFactory {

    private static final int INITIAL_HEALTH = 70;

    public static Player createPlayer(String playerName) {
        PlayerCards playerCards = new PlayerCards();
        PlayArea playArea = new PlayArea(INITIAL_HEALTH, playerCards);
        Player player = new Player(playerName, playArea);
        return player;
    }
}
