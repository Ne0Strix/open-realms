/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.model;

public class HealingEffect extends Effect {

    public HealingEffect(int value) {
        super(value);
    }

    @Override
    public void resolveAbility(PlayArea area) {
        area.setTurnHealing(area.getTurnHealing() + value);
    }
}
