/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.model.effects;

import at.vunfer.openrealms.model.Effect;
import at.vunfer.openrealms.model.PlayArea;

public class HealingEffect extends Effect {

    public HealingEffect(int value) {
        super(value);
    }

    @Override
    public void resolveAbility(PlayArea area) {
    }

    @Override
    public String toString() {
        return "HealingEffect{" +
                "value=" + value +
                '}';
    }
}
