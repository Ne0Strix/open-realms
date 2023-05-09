/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.model.effects;

import at.vunfer.openrealms.model.Effect;
import at.vunfer.openrealms.model.PlayArea;
import java.io.Serializable;
import java.util.Objects;

public class DamageEffect implements Effect, Serializable {

    private final int damage;

    public DamageEffect(int damage) {
        if (damage < 0) {
            throw new IllegalArgumentException("Damage must not be negative");
        }
        this.damage = damage;
    }

    public int getDamage() {
        return damage;
    }

    @Override
    public void applyEffect(PlayArea visitor) {
        visitor.visitDamage(damage);
    }

    @Override
    public String toString() {
        return "DamageEffect{" + "damage=" + damage + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DamageEffect)) return false;
        DamageEffect that = (DamageEffect) o;
        return damage == that.damage;
    }

    @Override
    public int hashCode() {
        return Objects.hash(damage);
    }
}
