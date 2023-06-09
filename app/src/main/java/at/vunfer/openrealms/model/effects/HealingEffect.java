/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.model.effects;

import at.vunfer.openrealms.model.Effect;
import at.vunfer.openrealms.model.PlayArea;
import java.io.Serializable;
import java.util.Objects;

public class HealingEffect implements Effect, Serializable {
    private final int healing;

    public HealingEffect(int healing) {
        if (healing < 0) {
            throw new IllegalArgumentException("Healing must not be negative");
        }
        this.healing = healing;
    }

    public int getHealing() {
        return healing;
    }

    @Override
    public void applyEffect(PlayArea visitor) {
        visitor.visitHealing(healing);
    }

    @Override
    public String toString() {
        return "HealingEffect{" + "healing=" + healing + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HealingEffect)) return false;
        HealingEffect that = (HealingEffect) o;
        return healing == that.healing;
    }

    @Override
    public int hashCode() {
        return Objects.hash(healing);
    }
}
