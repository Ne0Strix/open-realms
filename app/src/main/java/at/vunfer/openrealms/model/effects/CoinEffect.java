/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.model.effects;

import at.vunfer.openrealms.model.Effect;
import at.vunfer.openrealms.model.PlayArea;

public class CoinEffect implements Effect {

    private final int coin;

    public CoinEffect(int coin) {
        if (coin < 0) {
            throw new IllegalArgumentException("Coin must not be negative");
        }
        this.coin = coin;
    }

    @Override
    public void applyEffect(PlayArea visitor) {
        visitor.visitCoin(coin);
    }

    @Override
    public String toString() {
        return "CoinEffect{" + "coin=" + coin + '}';
    }
}
