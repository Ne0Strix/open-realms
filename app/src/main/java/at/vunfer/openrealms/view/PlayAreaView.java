/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import at.vunfer.openrealms.R;
import at.vunfer.openrealms.model.Card;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/** View class for the play area. */
public class PlayAreaView extends FrameLayout {
    private static final Logger LOGGER = Logger.getLogger(PlayAreaView.class.getName());
    private static final String TAG = "PlayAreaView";

    private TextView textView;
    private ArrayList<Card> cards = new ArrayList<>();
    /**
     * Constructor for PlayAreaView.
     *
     * @param context The context of the application.
     *     <p>public PlayAreaView(@NonNull Context context) { this(context, null); }
     *     <p>/** Constructor for PlayAreaView.
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
        initView();
    }

    /** Initializes the view. */
    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.play_area_view, this, true);
        textView = findViewById(R.id.text_play_area_view);
    }

    /**
     * Sets the text of the view.
     *
     * @param text The text to be set.
     */
    public void setText(String text) {
        try {
            textView.setText(text);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, TAG + ": Error setting text: " + e.getMessage());
        }
    }

    /**
     * Adds a card to the list of cards.
     *
     * @param card The card to be added.
     */
    public void addCard(Card card) {
        cards.add(card);
    }

    /**
     * Updates the view with the given text.
     *
     * @param text The text to be set.
     */
    public void updateView(String text) {
        setText(text);
        invalidate(); // Forces a redraw of the view
    }
}
