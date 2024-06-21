/* Licensed under GNU GPL v3.0 (C) 2024 */
package at.vunfer.openrealms.network.online;

public class Lobby {
    private final String name;
    private int numberOfPlayers;

    private boolean isInUse;

    public Lobby(String name, int numberOfPlayers, boolean isInUse) {
        this.name = name;
        this.numberOfPlayers = numberOfPlayers;
        this.isInUse = isInUse;
    }

    public boolean isInUse() {
        return isInUse;
    }

    public String getName() {
        return name;
    }

    public int getNumberOfPlayers() {
        return numberOfPlayers;
    }
}
