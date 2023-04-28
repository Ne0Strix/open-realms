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
    private TextView textView;

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
        LayoutInflater.from(context).inflate(R.layout.play_area_view, this);
        textView = findViewById(R.id.text_play_area_view);
    }
    public void setText(String text) {
        textView.setText(text);
    }
}
