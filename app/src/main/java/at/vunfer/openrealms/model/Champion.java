/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.model;

import java.util.List;
import java.util.Objects;

public class Champion extends Card {

    private final boolean isGuard;
    private final int health;
    private boolean isExpended;

    public Champion(
            String name,
            int cost,
            CardType type,
            Faction faction,
            List<Effect> effects,
            List<Effect> synergyEffects,
            boolean isGuard,
            int health)
            throws IllegalArgumentException {
        super(name, cost, type, faction, effects, synergyEffects);
        this.isGuard = isGuard;
        if (health < 0) {
            throw new IllegalArgumentException("Health must not be negative");
        } else {
            this.health = health;
        }
        this.isExpended = false;
    }

    public Champion(Champion c) {
        this(
                c.getName(),
                c.getCost(),
                c.getType(),
                c.getFaction(),
                c.getEffects(),
                c.getSynergyEffects(),
                c.isGuard,
                c.health);
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

    @Override
    public String toString() {
        return "Champion{"
                + "name='"
                + name
                + '\''
                + ", cost="
                + cost
                + ", type="
                + type
                + ", faction="
                + faction
                + ", effects="
                + effects
                + ", synergyEffects="
                + synergyEffects
                + ", id="
                + id
                + '}'
                + "isGuard="
                + isGuard
                + ", health="
                + health
                + ", isExpended="
                + isExpended
                + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Champion champion = (Champion) o;
        return isGuard == champion.isGuard && health == champion.health;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), isGuard, health);
    }
}
