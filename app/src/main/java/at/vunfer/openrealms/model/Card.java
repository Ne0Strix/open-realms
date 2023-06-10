/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Card implements Serializable {
    protected static int idCounter = 0;
    protected final String name;
    protected final int cost;
    protected final CardType type;
    protected final Faction faction;
    protected final List<Effect> effects;
    protected final List<Effect> synergyEffects;
    protected final int id;
    protected static final Deck<Card> fullCardCollection = new Deck<>();

    // TODO: Make id final
    public Card(Card c) {
        this(
                c.name,
                c.cost,
                c.type,
                c.faction,
                new ArrayList<>(c.effects),
                new ArrayList<>(c.synergyEffects));
    }

    public Card(String name, int cost, Faction faction, List<Effect> effects) {
        this(name, cost, faction, effects, new ArrayList<>());
    }

    public Card(
            String name,
            int cost,
            Faction faction,
            List<Effect> effects,
            List<Effect> synergyEffects) {
        this(name, cost, CardType.ITEM, faction, effects, synergyEffects);
    }

    public Card(
            String name,
            int cost,
            CardType type,
            Faction faction,
            List<Effect> effects,
            List<Effect> synergyEffects)
            throws IllegalArgumentException {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name must not be null or empty");
        } else if (cost < 0) {
            throw new IllegalArgumentException("Cost must not be negative");
        } else if (effects == null) {
            throw new IllegalArgumentException("Ability must not be null");
        } else if (synergyEffects == null) {
            throw new IllegalArgumentException("Synergy Ability must not be null");
        } else if (type == null) {
            throw new IllegalArgumentException("Type must not be null");
        }
        this.name = name;
        this.type = type;
        this.cost = cost;
        this.effects = effects;
        this.synergyEffects = synergyEffects;
        this.faction = faction;
        this.id = idCounter++;
        fullCardCollection.add(this);
    }

    public String getName() {
        return name;
    }

    public int getCost() {
        return cost;
    }

    public CardType getType() {
        return type;
    }

    public Faction getFaction() {
        return faction;
    }

    public int getId() {
        return id;
    }

    public List<Effect> getEffects() {
        return effects;
    }

    public List<Effect> getSynergyEffects() {
        return synergyEffects;
    }

    public void applyEffects(PlayArea visitor) {
        applyEffects(visitor, effects);
    }

    public void applySynergyEffects(PlayArea visitor) {
        applyEffects(visitor, synergyEffects);
    }

    protected void applyEffects(PlayArea visitor, List<Effect> effectsToApply) {
        for (Effect effect : effectsToApply) {
            effect.applyEffect(visitor);
        }
    }

    @Override
    public String toString() {
        return "Card{"
                + "name='"
                + name
                + '\''
                + ", cost="
                + cost
                + ", type="
                + type
                + ", faction="
                + faction
                + ", effects="
                + effects
                + ", synergyEffects="
                + synergyEffects
                + ", id="
                + id
                + '}';
    }

    public boolean isIdentical(Card c) {
        if (this == c) return true;
        return cost == c.cost
                && faction == c.faction
                && name.equals(c.name)
                && effects.equals(c.effects)
                && synergyEffects.equals(c.synergyEffects);
    }

    public static Deck<Card> getFullCardCollection() {
        return fullCardCollection;
    }

    public static Card getCardById(int id) {
        for (Card c : fullCardCollection) {
            if (c.getId() == id) {
                return c;
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return cost == card.cost
                && faction == card.faction
                && Objects.equals(name, card.name)
                && Objects.equals(effects, card.effects)
                && Objects.equals(synergyEffects, card.synergyEffects)
                && id == card.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, cost, effects, id);
    }
}
