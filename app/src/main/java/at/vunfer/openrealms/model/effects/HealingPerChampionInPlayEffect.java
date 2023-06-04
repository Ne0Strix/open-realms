/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.model.effects;

import at.vunfer.openrealms.model.Effect;
import at.vunfer.openrealms.model.PlayArea;
import java.io.Serializable;
import java.util.Objects;

public class HealingPerChampionInPlayEffect implements Effect, Serializable {
    private final int healingPerChampion;

    public HealingPerChampionInPlayEffect(int healing) {
        if (healing < 0) {
            throw new IllegalArgumentException("Healing per champion must not be negative");
        }
        this.healingPerChampion = healing;
    }

    @Override
    public void applyEffect(PlayArea visitor) {
        visitor.visitHealingPerChampionInPlay(healingPerChampion);
    }

    @Override
    public String toString() {
        return "HealingPerChampionInPlayEffect{"
                + "healing_per_champion="
                + healingPerChampion
                + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HealingPerChampionInPlayEffect)) return false;
        HealingPerChampionInPlayEffect that = (HealingPerChampionInPlayEffect) o;
        return healingPerChampion == that.healingPerChampion;
    }

    @Override
    public int hashCode() {
        return Objects.hash(healingPerChampion);
    }

    public int getAmount() {
        return healingPerChampion;
    }
}
