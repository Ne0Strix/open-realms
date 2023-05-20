/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.model;

import static org.junit.jupiter.api.Assertions.*;

import at.vunfer.openrealms.model.effects.DamageEffect;
import java.util.List;
import org.junit.jupiter.api.Test;

class DeckTest {

    @Test
    void drawRandomFromEmptyDeck() {
        Deck<Card> deck = new Deck<>();
        assertThrows(
                IllegalStateException.class, deck::drawRandom, "Cannot draw from empty source.");
    }

    @Test
    void drawCardNotInDeck() {
        Deck<Card> deck = new Deck<>();
        Card card = new Card("Test Card", 2, CardType.NONE, List.of(new DamageEffect(1)));
        assertThrows(IllegalArgumentException.class, () -> deck.draw(card), "Item not in deck.");
    }
}
