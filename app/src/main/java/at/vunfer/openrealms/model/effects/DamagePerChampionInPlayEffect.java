/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.model.effects;

import at.vunfer.openrealms.model.Effect;
import at.vunfer.openrealms.model.PlayArea;
import java.io.Serializable;
import java.util.Objects;

public class DamagePerChampionInPlayEffect implements Effect, Serializable {

    private final int damagePerChampion;

    public DamagePerChampionInPlayEffect(int damagePerGuard) {
        if (damagePerGuard < 0) {
            throw new IllegalArgumentException("Damage per champion must not be negative");
        }
        this.damagePerChampion = damagePerGuard;
    }

    public int getDamagePerChampion() {
        return damagePerChampion;
    }

    @Override
    public void applyEffect(PlayArea visitor) {
        visitor.visitDamagePerChampionInPlayEffect(damagePerChampion);
    }

    @Override
    public String toString() {
        return "DamagePerGuardInPlayEffect{" + "damage_per_guard=" + damagePerChampion + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DamagePerChampionInPlayEffect)) return false;
        DamagePerChampionInPlayEffect that = (DamagePerChampionInPlayEffect) o;
        return damagePerChampion == that.damagePerChampion;
    }

    @Override
    public int hashCode() {
        return Objects.hash(damagePerChampion);
    }

    public int getAmount() {
        return damagePerChampion;
    }
}
