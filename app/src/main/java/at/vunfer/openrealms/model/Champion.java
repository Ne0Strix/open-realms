/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.model;

import java.util.List;

public class Champion extends Card {
    public Champion(String name, int cost, CardType type, List<Effect> effects)
            throws IllegalArgumentException {
        super(name, cost, type, effects);
    }
}
