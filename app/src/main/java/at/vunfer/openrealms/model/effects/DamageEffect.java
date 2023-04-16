/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.model.effects;

import at.vunfer.openrealms.model.Effect;
import at.vunfer.openrealms.model.PlayArea;

public class DamageEffect implements Effect {

    private final int damage;

    public DamageEffect(int damage) {
        if (damage < 0) {
            throw new IllegalArgumentException("Damage must not be negative");
        }
        this.damage = damage;
    }

    @Override
    public void applyEffect(PlayArea visitor) {
        visitor.visitDamage(damage);
    }

    @Override
    public String toString() {
        return "DamageEffect{" + "damage=" + damage + '}';
    }
}
