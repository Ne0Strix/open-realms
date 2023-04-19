/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.model;

import android.content.Context;
import android.widget.ImageView;
import at.vunfer.openrealms.R;
import java.util.ArrayList;
import java.util.List;

public class Card {
    private static final String TAG = "Card";
    private String name;
    private int cost;
    private List<Effect> effects;

    private final int imageResource;
    private final String description;
    private ImageView cardImage;

    public Card(Context context) {
        this.name = "Empty Card";
        this.cost = 0;
        this.effects = new ArrayList<>();
        this.imageResource = R.drawable.emptycards;
        this.cardImage = new ImageView(context);
        this.description = "";
    }

    public Card(String name, int cost, List<Effect> effects, String description)
            throws IllegalArgumentException {
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
        this.description = description;
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
        return "Card{" + "name='" + name + '\'' + ", cost=" + cost + '}';
    }

    public String getDescription() {
        return description;
    }

    public int getImageResource() {
        return imageResource;
    }

    public ImageView getCardImage() {
        return cardImage;
    }

    public void setCardImage(ImageView cardImage) {
        this.cardImage = cardImage;
    }
}
