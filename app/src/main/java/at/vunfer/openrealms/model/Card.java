/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.model;

public class Card {
    private String name;
    private int cost;
    private int attack;
    private int health;

    public Card(String name) throws IllegalArgumentException {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name must not be null or empty");
        } else if (cost < 0) {
            throw new IllegalArgumentException("Cost must not be negative");
        } else if (attack < 0) {
            throw new IllegalArgumentException("Attack must not be negative");
        } else if (health < 0) {
            throw new IllegalArgumentException("Health must not be negative");
        }
        this.name = name;
        this.cost = cost;
        this.attack = attack;
        this.health = health;
    }

    public String getName() {
        return name;
    }

    public int getCost() {
        return cost;
    }

    public int getAttack() {
        return attack;
    }

    public int getHealth() {
        return health;
    }

    @Override
    public String toString() {
        return "Card{"
                + "name='"
                + name
                + '\''
                + ", cost="
                + cost
                + ", attack="
                + attack
                + ", health="
                + health
                + '}';
    }
}
