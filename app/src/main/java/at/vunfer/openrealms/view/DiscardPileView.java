/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.view;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import at.vunfer.openrealms.R;
import at.vunfer.openrealms.view.view_interfaces.CardPileView;
import java.util.ArrayList;
import java.util.List;

public class DiscardPileView extends ConstraintLayout implements CardPileView {
    List<CardView> allCards = new ArrayList<>();

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

        TextView outline = findViewById(R.id.discardPileAmountOutline);
        outline.getPaint().setStrokeWidth(5);
        outline.getPaint().setStyle(Paint.Style.STROKE);

        CardView topCard = findViewById(R.id.discardPileCardView);
        topCard.findViewById(R.id.card_view_background).setOnTouchListener(null);
        topCard.findViewById(R.id.card_view_background)
                .setOnClickListener(
                        (view) -> {
                            // Log.i("DiscardView", "GOT A CLICK! " + allCards.size());
                            LinearLayout fullScreenCards =
                                    getRootView().findViewById(R.id.fullscreen_discard_pile);
                            if (fullScreenCards.getVisibility() == GONE) {
                                enableFullscreenView();
                            } else {
                                disableFullscreenView();
                            }
                        });
        /*  FrameLayout cardHolder = findViewById(R.id.discardPileCardHolder);
                cardHolder.setOnTouchListener((view, motionEvent) -> {
                    Log.e("DISCRAD PILE", motionEvent.toString());
                    return false;
                });
        */
    }

    @Override
    public void updateView(List<CardView> cardsToDisplay) {
        allCards.clear();
        allCards.addAll(cardsToDisplay);

        TextView txtAmount = findViewById(R.id.discardPileAmount);
        txtAmount.setText("" + allCards.size());

        TextView txtAmountOutline = findViewById(R.id.discardPileAmountOutline);
        txtAmountOutline.setText("" + allCards.size());

        CardView visibleCard = findViewById(R.id.discardPileCardView);
        if (!allCards.isEmpty()) {
            CardView lastCard = allCards.get(allCards.size() - 1);
            lastCard.setHealthSize(8);
            visibleCard.setCard(lastCard.getCard());
            visibleCard.setVisibility(VISIBLE);
        } else {
            visibleCard.setVisibility(GONE);
            disableFullscreenView();
        }
    }

    private void enableFullscreenView() {
        HorizontalScrollView fullScreenCardsParent =
                getRootView().findViewById(R.id.fullschreen_discard_pile_parent);
        LinearLayout fullScreenCards = getRootView().findViewById(R.id.fullscreen_discard_pile);

        fullScreenCardsParent.setVisibility(VISIBLE);
        fullScreenCards.setVisibility(VISIBLE);
        fullScreenCards.removeAllViews();
        for (CardView c : allCards) {
            fullScreenCards.addView(c);
        }
    }

    private void disableFullscreenView() {
        HorizontalScrollView fullScreenCardsParent =
                getRootView().findViewById(R.id.fullschreen_discard_pile_parent);
        LinearLayout fullScreenCards = getRootView().findViewById(R.id.fullscreen_discard_pile);

        fullScreenCardsParent.setVisibility(GONE);
        fullScreenCards.setVisibility(GONE);
        fullScreenCards.removeAllViews();
    }
}
