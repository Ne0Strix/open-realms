/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.model;

public class DamageEffect extends Effect {

    public DamageEffect(int value) {
        super(value);
    }

    @Override
    public void resolveAbility(PlayArea area) {
        area.setTurnDamage(area.getTurnDamage() + value);
    }
}
