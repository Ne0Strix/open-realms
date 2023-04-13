/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.model.effects;

import at.vunfer.openrealms.model.Effect;
import at.vunfer.openrealms.model.PlayArea;

public class HealingEffect implements Effect {
    private final int healing;

    public HealingEffect(int healing) {
        this.healing = healing;
    }

    @Override
    public void applyEffect(PlayArea visitor) {
        visitor.visitHealing(healing);
    }

    @Override
    public String toString() {
        return "HealingEffect{" + "healing=" + healing + '}';
    }
}
