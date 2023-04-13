/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.model;

public class CoinEffect extends Effect {

    public CoinEffect(int value) {
        super(value);
    }

    @Override
    public void resolveAbility(PlayArea area) {
        area.setTurnCoins(area.getTurnCoins() + value);
    }
}
