/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.model;

import java.util.List;

public class Card {
    private final String name;
    private final int cost;
    private final List<Effect> abilities;

    public Card(String name, int cost, List<Effect> abilities) throws IllegalArgumentException {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name must not be null or empty");
        } else if (cost < 0) {
            throw new IllegalArgumentException("Cost must not be negative");
        } else if (abilities == null) {
            throw new IllegalArgumentException("Ability must not be null");
        } else if (abilities.size() == 0) {
            throw new IllegalArgumentException("Ability must not be empty");
        }
        this.name = name;
        this.cost = cost;
        this.abilities = abilities;
    }

    public String getName() {
        return name;
    }

    public int getCost() {
        return cost;
    }

    public List<Effect> getAbilities() {
        return abilities;
    }

    @Override
    public String toString() {
        return "Card{" + "name='" + name + '\'' + ", cost=" + cost + ", Abilities= " + abilities.toString() + '}';
    }

}
