/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.model;

import java.util.ArrayList;
import java.util.Random;

public class Deck<T> extends ArrayList<T> {
    private static final Random rand = new Random();

    /**
     * Draws a random item from the deck.
     *
     * @return The drawn item.
     * @throws IllegalStateException If the deck is empty.
     */
    public T drawRandom() throws IllegalStateException {
        if (super.isEmpty()) {
            throw new IllegalStateException("Cannot draw from empty source.");
        }

        int index = rand.nextInt(super.size());
        T drawn = super.get(index);
        super.remove(drawn);

        return drawn;
    }

    /**
     * Draws a specific item from the deck.
     *
     * @param item The item to draw.
     * @return The drawn item.
     * @throws IllegalArgumentException If the item is not in the deck.
     */
    public T draw(T item) throws IllegalArgumentException {
        if (!this.contains(item)) {
            throw new IllegalArgumentException("Item not in deck.");
        }
        this.remove(item);
        return item;
    }
}
