/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import at.vunfer.openrealms.R;
import at.vunfer.openrealms.view.view_interfaces.CardPileView;
import java.util.List;

public class DiscardPileView extends ConstraintLayout implements CardPileView {

    public DiscardPileView(@NonNull Context context) {
        this(context, null);
    }

    public DiscardPileView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DiscardPileView(
            @NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.discard_pile_view, this);
    }

    @Override
    public void updateView(List<CardView> cardsToDisplay) {
        TextView txtAmount = findViewById(R.id.discardPileAmount);
        txtAmount.setText("" + cardsToDisplay.size());

        FrameLayout cardHolder = findViewById(R.id.discardPileCardHolder);
        cardHolder.removeAllViews();

        if (!cardsToDisplay.isEmpty()) {
            CardView lastCard = cardsToDisplay.get(cardsToDisplay.size() - 1);
            cardHolder.addView(lastCard);
        }
    }
}
