/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.model;

import java.util.ArrayList;
import java.util.Random;

public class Deck<T> extends ArrayList<T> {
    private static final Random rand = new Random();

    public T drawRandom() throws IllegalStateException {
        if (super.isEmpty()) {
            throw new IllegalStateException("Cannot draw from empty source.");
        }
        int index = 0;
        if (super.size() > 1) {
            index = rand.nextInt(super.size() - 1);
        }
        T drawn = super.get(index);
        super.remove(drawn);

        return drawn;
    }

    public T draw(T item) throws IllegalArgumentException {
        if (!this.contains(item)) {
            throw new IllegalArgumentException("Item not in deck.");
        }
        this.remove(item);
        return item;
    }
}
