/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.model;

import android.util.Log;
import at.vunfer.openrealms.model.effects.CoinEffect;
import at.vunfer.openrealms.model.effects.DamageEffect;
import at.vunfer.openrealms.model.effects.HealingEffect;
import java.util.ArrayList;
import java.util.List;

public class Market {
    private static Market marketInstance;
    private static final int TOTAL_PURCHASABLE = 5;
    private static final String TAG = "Market";
    Deck<Card> marketDeck;
    Deck<Card> forPurchase;

    private Market() {
        marketDeck = new Deck<>();
        forPurchase = new Deck<>();
        marketDeck.add(
                new Card(
                        "Testcard1",
                        3,
                        List.of(new DamageEffect(1), new HealingEffect(1), new CoinEffect(1))));
        marketDeck.add(
                new Card(
                        "Testcard2",
                        3,
                        List.of(new DamageEffect(1), new HealingEffect(1), new CoinEffect(1))));
        marketDeck.add(
                new Card(
                        "Testcard3",
                        3,
                        List.of(new DamageEffect(1), new HealingEffect(1), new CoinEffect(1))));
        marketDeck.add(
                new Card(
                        "Testcard4",
                        3,
                        List.of(new DamageEffect(1), new HealingEffect(1), new CoinEffect(1))));
        marketDeck.add(
                new Card(
                        "Testcard5",
                        3,
                        List.of(new DamageEffect(1), new HealingEffect(1), new CoinEffect(1))));
        marketDeck.add(
                new Card(
                        "Testcard6",
                        3,
                        List.of(new DamageEffect(1), new HealingEffect(1), new CoinEffect(1))));

        restock();
    }

    public static Market getInstance() {
        if (marketInstance == null) {
            marketInstance = new Market();
        }
        return marketInstance;
    }

    public ArrayList<Card> getForPurchase() {
        return forPurchase;
    }

    public int restock() {
        int restocked = 0;
        while (forPurchase.size() < TOTAL_PURCHASABLE) {
            Card card = marketDeck.drawRandom();
            if (card != null) {
                forPurchase.add(card);
                restocked++;
            } else {
                Log.i(TAG, "You have no more cards to draw for the market.");
                break;
            }
        }
        return restocked;
    }

    public Card purchase(Card card) {
        if (card == null || !forPurchase.contains(card)) {
            throw new IllegalArgumentException("Card is not to purchase.");
        }
        forPurchase.remove(card);
        return card;
    }
}
