/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.model;

public class Card {
    private String name;
    private int cost;
    private Effect ability;

    public Card(String name, int cost, Effect ability) throws IllegalArgumentException {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name must not be null or empty");
        } else if (cost < 0) {
            throw new IllegalArgumentException("Cost must not be negative");
        } else if (ability == null) {
            throw new IllegalArgumentException("Ability must not be null");
        }
        this.name = name;
        this.cost = cost;
        this.ability = ability;
    }

    public String getName() {
        return name;
    }

    public int getCost() {
        return cost;
    }

    public Effect getAbility() {
        return ability;
    }

    @Override
    public String toString() {
        return "Card{" + "name='" + name + '\'' + ", cost=" + cost + '}';
    }
}
