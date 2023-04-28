package at.vunfer.openrealms.view;

import android.content.Context;
import android.os.Build;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.robolectric.annotation.Config;

import androidx.test.core.app.ApplicationProvider;

import at.vunfer.openrealms.R;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(JUnit4.class)
@Config(sdk = Build.VERSION_CODES.P)
public class PlayAreaViewTest {

    private PlayAreaView playAreaView;
    private Context context;

    @Before
    public void setUp() throws Exception {
        context = ApplicationProvider.getApplicationContext();
        playAreaView = new PlayAreaView(context);
    }

    @Test
    public void testTextViewNotNull() {
        TextView textView = playAreaView.findViewById(R.id.text_play_area_view);
        assertNotNull(textView);
    }

    @Test
    public void setText() {
        String text = "Test Text";
        playAreaView.setText(text);
        TextView textView = playAreaView.findViewById(R.id.text_play_area_view);
        assertEquals(textView.getText().toString(), text);
    }
}