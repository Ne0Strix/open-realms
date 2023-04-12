package at.vunfer.openrealms.model.effects;

import at.vunfer.openrealms.model.Effect;
import at.vunfer.openrealms.model.PlayArea;

public class CoinEffect extends Effect {

    public CoinEffect(int value) {
        super(value);
    }

    @Override
    public void resolveAbility(PlayArea area) {
    }

    @Override
    public String toString() {
        return "CoinEffect{" +
                "value=" + value +
                '}';
    }
}
