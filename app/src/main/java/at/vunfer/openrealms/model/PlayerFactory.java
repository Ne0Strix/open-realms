/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.model;

import android.content.Context;

public class PlayerFactory {

    private static final int INITIAL_HEALTH = 70;

    public static Player createPlayer(String playerName, Context context) {
        PlayerCards playerCards = new PlayerCards(context);
        PlayArea playArea = new PlayArea(INITIAL_HEALTH, playerCards);
        Player player = new Player(playerName, playArea);
        return player;
    }
}
