/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import at.vunfer.openrealms.R;
import java.util.ArrayList;
import java.util.List;

/** View class for the play area. */
public class PlayAreaView extends LinearLayout {
    private List<CardView> displayedCards = new ArrayList<>();
    /**
     * Constructor for PlayAreaView.
     *
     * @param context The context of the application.
     */
    public PlayAreaView(@NonNull Context context) {
        this(context, null);
    }

    /**
     * @param context The context of the application.
     * @param attrs The attributes of the view.
     */
    public PlayAreaView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Constructor for PlayAreaView.
     *
     * @param context The context of the application.
     * @param attrs The attributes of the view.
     * @param defStyleAttr The default style attribute.
     */
    public PlayAreaView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.play_area_view, this);
    }

    public void updatePlayArea() {
        removeAllViews();
        for (CardView view : displayedCards) {
            addView(view);
        }
    }

    public List<CardView> getDisplayedCards() {
        return displayedCards;
    }
}
