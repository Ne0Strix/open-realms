/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import at.vunfer.openrealms.R;
import at.vunfer.openrealms.view.view_interfaces.CardPileView;
import java.util.List;

public class DeckView extends ConstraintLayout implements CardPileView {
    public DeckView(@NonNull Context context) {
        this(context, null);
    }

    public DeckView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DeckView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.deck_view, this);
    }

    @Override
    public void updateView(List<CardView> cardsToDisplay) {
        if (cardsToDisplay.isEmpty()) {
            findViewById(R.id.deck_view_back_of_card).setVisibility(INVISIBLE);
        } else {
            findViewById(R.id.deck_view_back_of_card).setVisibility(VISIBLE);
        }
        TextView txtAmount = findViewById(R.id.deck_view_amount);
        txtAmount.setText("" + cardsToDisplay.size());
    }
}
