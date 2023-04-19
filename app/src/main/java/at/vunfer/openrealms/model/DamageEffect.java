/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.model;

public class DamageEffect implements Effect {

    private int damage;

    public DamageEffect(int damage) {
        this.damage = damage;
    }

    @Override
    public void applyEffect(PlayArea visitor) {
        visitor.visitDamage(damage);
    }

    @Override
    public String getDescription() {
        return "Deals " + damage + " damage.";
    }
}
