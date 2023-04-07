/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.view;

import at.vunfer.openrealms.model.PlayArea;
import at.vunfer.openrealms.model.PlayerCards;

public class Player {
    private String playerName;
    private PlayerCards playerCards;
    private PlayArea playArea;

    public Player(String playerName, PlayerCards playerCards, PlayArea playArea) {
        this.playerName = playerName;
        this.playerCards = playerCards;
        this.playArea = playArea;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public PlayerCards getPlayerCards() {
        return playerCards;
    }

    public void setPlayerCards(PlayerCards playerCards) {
        this.playerCards = playerCards;
    }

    public PlayArea getPlayArea() {
        return playArea;
    }

    public void setPlayArea(PlayArea playArea) {
        this.playArea = playArea;
    }
}
