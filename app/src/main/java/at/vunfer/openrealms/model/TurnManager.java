/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.model;

import java.util.List;

import at.vunfer.openrealms.view.Player;

public class TurnManager {
    private List<Player> players;
    private int turnPlayer;

    public TurnManager(List<Player> players, int turnPlayer) {
        this.players = players;
        this.turnPlayer = turnPlayer;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public int getTurnPlayer() {
        return turnPlayer;
    }

    public void setTurnPlayer(int turnPlayer) {
        this.turnPlayer = turnPlayer;
    }

    public void endTurn() {

    }

    public void dealDamage() {

    }

    public void healPlayer() {

    }
}
