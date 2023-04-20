/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.model;

import android.util.Log;
import java.util.List;

public class Card {
    private static final String TAG = "Card";
    private final String name;
    private final int cost;
    private final List<Effect> effects;

    public Card(String name, int cost, List<Effect> effects) throws IllegalArgumentException {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name must not be null or empty");
        } else if (cost < 0) {
            throw new IllegalArgumentException("Cost must not be negative");
        } else if (effects == null || effects.isEmpty()) {
            throw new IllegalArgumentException("Ability must not be empty");
        }
        this.name = name;
        this.cost = cost;
        this.effects = effects;
        Log.v(TAG, "Created Card: " + this);
    }

    public String getName() {
        return name;
    }

    public int getCost() {
        return cost;
    }

    public List<Effect> getEffects() {
        return effects;
    }

    public void applyEffects(PlayArea visitor) {
        for (Effect effect : effects) {
            effect.applyEffect(visitor);
        }
    }

    @Override
    public String toString() {
        return "Card{" + "name='" + name + '\'' + ", cost=" + cost + "}";
    }

    public boolean isIdentical(Card c) {
        if (this == c) return true;
        return cost == c.cost && name.equals(c.name) && effects.equals(c.effects);
    }
}
