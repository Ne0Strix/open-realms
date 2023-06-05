/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.model.effects;

import at.vunfer.openrealms.model.Effect;
import at.vunfer.openrealms.model.PlayArea;
import java.io.Serializable;
import java.util.Objects;

public class DrawEffect implements Effect, Serializable {

    private final int amount;

    public DrawEffect(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount must not be negative.");
        }
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public void applyEffect(PlayArea visitor) {
        visitor.visitDraw();
    }

    @Override
    public String toString() {
        return "DrawEffect{" + "amount=" + amount + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DrawEffect)) return false;
        DrawEffect that = (DrawEffect) o;
        return amount == that.amount;
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount);
    }
}
