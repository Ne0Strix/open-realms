/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.model;

import androidx.annotation.NonNull;

public class Player {
    private String playerName;
    private PlayArea playArea;

    public Player(String playerName, PlayArea playArea) {
        this.playerName = playerName;
        this.playArea = playArea;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public PlayArea getPlayArea() {
        return playArea;
    }

    public void setPlayArea(PlayArea playArea) {
        this.playArea = playArea;
    }

    @NonNull @Override
    public String toString() {
        return getPlayerName();
    }
}
