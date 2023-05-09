/* Licensed under GNU GPL v3.0 (C) 2023 */
package model;

public class DamageEffect implements Effect {

    private int damage;

    public DamageEffect(int damage) {
        this.damage = damage;
    }

    @Override
    public void applyEffect(PlayArea visitor) {
        visitor.visitDamage(damage);
    }
}
