/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.model;

public class HealingEffect implements Effect {
    private int healing;

    public HealingEffect(int healing) {
        this.healing = healing;
    }

    @Override
    public void applyEffect(PlayArea visitor) {
        visitor.visitHealing(healing);
    }

    @Override
    public String getDescription() {
        return "Heals the player for " + healing + " points.";
    }
}
