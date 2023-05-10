/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.model;

import android.util.Log;
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
        Log.i("DrawRandom", "drew card index: " + index);
        T drawn = super.get(index);
        Log.i("Deck", "Size of Deck before draw: " + super.size());
        Log.i("Deck", "Drew " + index + " from deck.");
        super.remove(drawn);
        Log.i("Deck", "Size of Deck after draw: " + super.size());

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
