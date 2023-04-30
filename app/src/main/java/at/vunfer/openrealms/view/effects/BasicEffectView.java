/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.view.effects;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import at.vunfer.openrealms.R;
import at.vunfer.openrealms.model.Effect;
import at.vunfer.openrealms.model.effects.CoinEffect;
import at.vunfer.openrealms.model.effects.DamageEffect;
import at.vunfer.openrealms.model.effects.HealingEffect;

public class BasicEffectView extends ConstraintLayout {
    private Effect effect;

    public BasicEffectView(@NonNull Context context) {
        super(context);
        init();
    }

    public BasicEffectView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BasicEffectView(
            @NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public BasicEffectView(
            @NonNull Context context,
            @Nullable AttributeSet attrs,
            int defStyleAttr,
            int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public BasicEffectView(Context context, Effect effect) {
        super(context);
        this.effect = effect;
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.effect_view, this);
        if (effect != null) {
            // if-else was used, since java does not support Swtich-case with class attributes
            ImageView effectIcon = findViewById(R.id.effectIcon);
            TextView txtAmount = findViewById(R.id.effectAmount);

            if (effect instanceof DamageEffect) {
                effectIcon.setImageResource(R.drawable.attack);
                txtAmount.setText(((DamageEffect) effect).getDamage() + "");
            } else if (effect instanceof HealingEffect) {
                effectIcon.setImageResource(R.drawable.heal);
                txtAmount.setText(((HealingEffect) effect).getHealing() + "");
            } else if (effect instanceof CoinEffect) {
                effectIcon.setImageResource(R.drawable.coin);
                txtAmount.setText(((CoinEffect) effect).getCoin() + "");
            }
        }
    }
}
