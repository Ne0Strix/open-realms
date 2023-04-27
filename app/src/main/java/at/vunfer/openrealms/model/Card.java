/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.model;

import at.vunfer.openrealms.R;
import java.util.ArrayList;
import java.util.List;

public class Card {
    private static final String TAG = "Card";
    private final String name;
    private final int cost;
    private final List<Effect> effects;

    private final int imageResource;
    private final String description;

    public Card(Card c) {
        this(c.name, c.cost, new ArrayList<>(c.effects));
    }

    public Card() {
        this.name = "Empty Card";
        this.cost = 0;
        this.effects = new ArrayList<>();
        this.imageResource = R.drawable.emptycards;
        this.description = "";
    }

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

        this.imageResource = R.drawable.emptycards;
        this.description = toString();
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

    public String getDescription() {
        return description;
    }

    public int getImageResource() {
        return imageResource;
    }
}
