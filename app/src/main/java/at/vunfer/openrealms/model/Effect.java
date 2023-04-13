/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.model;

public abstract class Effect {
    protected int value;

    public Effect(int value) {
        this.value = value;
    }

    public abstract void resolveAbility(PlayArea area);

    @Override
    public String toString() {
        return "Effect{" + "value=" + value + '}';
    }
}
