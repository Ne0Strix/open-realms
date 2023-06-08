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
    private static final float CARD_SCALE = 1.1f;
    private final float screenDensity;

    private LinearLayout fullscreenCards;
    private HorizontalScrollView fullscreenCardsParent;
    private CardView topCard;

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
        screenDensity = getResources().getDisplayMetrics().density;

        TextView outline = findViewById(R.id.discardPileAmountOutline);
        outline.getPaint().setStrokeWidth(5);
        outline.getPaint().setStyle(Paint.Style.STROKE);

        topCard = findViewById(R.id.discardPileCardView);
        topCard.setHealthSize(8);
        topCard.findViewById(R.id.card_view_background).setOnTouchListener(null);
        topCard.findViewById(R.id.card_view_background)
                .setOnClickListener(
                        view -> {
                            fullscreenCards.findViewById(R.id.fullscreen_discard_pile);
                            if (!allCards.isEmpty()) {
                                if (fullscreenCards.getVisibility() == GONE) {
                                    enableFullscreenView();
                                } else if (topCard.getAlpha() != 1f) {
                                    disableFullscreenView();
                                }
                            }
                        });
    }

    @Override
    public void updateView(List<CardView> cardsToDisplay) {
        if (fullscreenCards == null) {
            fullscreenCardsParent =
                    getRootView().findViewById(R.id.fullschreen_discard_pile_parent);
            fullscreenCards = getRootView().findViewById(R.id.fullscreen_discard_pile);
        }
        allCards.clear();
        allCards.addAll(cardsToDisplay);

        TextView txtAmount = findViewById(R.id.discardPileAmount);
        txtAmount.setText("" + allCards.size());

        TextView txtAmountOutline = findViewById(R.id.discardPileAmountOutline);
        txtAmountOutline.setText("" + allCards.size());

        if (!allCards.isEmpty()) {
            CardView lastCard = allCards.get(allCards.size() - 1);
            topCard.setCard(lastCard.getCard());
            topCard.setVisibility(VISIBLE);
            if (topCard.getAlpha() != 1f) {
                enableFullscreenView();
            }
        } else {
            topCard.setVisibility(GONE);
            disableFullscreenView();
        }
    }

    public void enableFullscreenView() {
        fullscreenCardsParent.setVisibility(VISIBLE);
        fullscreenCards.setVisibility(VISIBLE);
        fullscreenCards.removeAllViews();
        for (CardView c : allCards) {
            fullscreenCards.addView(c);
            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(
                            (int) (CARD_SCALE * screenDensity * 77),
                            (int) (CARD_SCALE * screenDensity * 106));
            params.leftMargin = 20;
            params.rightMargin = 20;
            c.setLayoutParams(params);
            c.setFaceUp();
            c.setRotation(0);
        }
        topCard.setAlpha(0.5f);
    }

    public void disableFullscreenView() {
        fullscreenCards.removeAllViews();
        fullscreenCardsParent.setVisibility(GONE);
        fullscreenCards.setVisibility(GONE);
        topCard.setAlpha(1f);
    }
}
