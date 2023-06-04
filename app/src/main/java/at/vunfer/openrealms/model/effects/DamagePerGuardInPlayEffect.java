/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.model.effects;

import at.vunfer.openrealms.model.Effect;
import at.vunfer.openrealms.model.PlayArea;
import java.io.Serializable;
import java.util.Objects;

public class DamagePerGuardInPlayEffect implements Effect, Serializable {

    private final int damagePerGuard;

    public DamagePerGuardInPlayEffect(int damagePerGuard) {
        if (damagePerGuard < 0) {
            throw new IllegalArgumentException("Damage per guard must not be negative");
        }
        this.damagePerGuard = damagePerGuard;
    }

    public int getDamagePerGuard() {
        return damagePerGuard;
    }

    @Override
    public void applyEffect(PlayArea visitor) {
        visitor.visitDamagePerGuardInPlay(damagePerGuard);
    }

    @Override
    public String toString() {
        return "DamagePerGuardInPlayEffect{" + "damage_per_guard=" + damagePerGuard + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DamagePerGuardInPlayEffect)) return false;
        DamagePerGuardInPlayEffect that = (DamagePerGuardInPlayEffect) o;
        return damagePerGuard == that.damagePerGuard;
    }

    @Override
    public int hashCode() {
        return Objects.hash(damagePerGuard);
    }

    public int getAmount() {
        return damagePerGuard;
    }
}
