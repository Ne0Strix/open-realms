/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.model;

import java.util.List;

public class Champion extends Card {

    private final boolean isGuard;
    private final int health;
    private boolean isExpended;

    public Champion(
            String name, int cost, CardType type, List<Effect> effects, boolean isGuard, int health)
            throws IllegalArgumentException {
        super(name, cost, type, effects);
        this.isGuard = isGuard;
        this.health = health;
        this.isExpended = false;
    }

    public boolean isKilled(int damage) {
        return health <= damage;
    }

    public boolean isGuard() {
        return isGuard;
    }

    public int getHealth() {
        return health;
    }

    public boolean isExpended() {
        return isExpended;
    }

    public boolean expend() {
        if (isExpended) {
            return false;
        }
        isExpended = true;
        return true;
    }

    public void reset() {
        isExpended = false;
    }
}
