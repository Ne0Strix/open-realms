/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.model.effects;

import at.vunfer.openrealms.model.Effect;
import at.vunfer.openrealms.model.PlayArea;
import java.io.Serializable;
import java.util.Objects;

public class DamagePerChampionInPlayEffect implements Effect, Serializable {

    private final int damagePerChampion;

    public DamagePerChampionInPlayEffect(int damagePerChampion) {
        if (damagePerChampion < 0) {
            throw new IllegalArgumentException("Damage per champion must not be negative");
        }
        this.damagePerChampion = damagePerChampion;
    }

    public int getDamagePerChampion() {
        return damagePerChampion;
    }

    @Override
    public void applyEffect(PlayArea visitor) {
        visitor.visitDamagePerChampionInPlay(damagePerChampion);
    }

    @Override
    public String toString() {
        return "DamagePerChampionInPlayEffect{" + "damage_per_champion=" + damagePerChampion + '}';
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
