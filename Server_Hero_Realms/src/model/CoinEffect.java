/* Licensed under GNU GPL v3.0 (C) 2023 */
package model;

public class CoinEffect implements Effect {

    private int coin;

    public CoinEffect(int coin) {
        this.coin = coin;
    }

    @Override
    public void applyEffect(PlayArea visitor) {
        visitor.visitCoin(coin);
    }
}
