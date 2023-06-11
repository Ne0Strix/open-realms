/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.view;

import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import at.vunfer.openrealms.MainActivity;
import at.vunfer.openrealms.R;
import at.vunfer.openrealms.model.Card;
import at.vunfer.openrealms.model.Faction;
import at.vunfer.openrealms.model.effects.DamageEffect;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class DiscardPileViewTest {

    private DiscardPileView discardPileView;
    private Context context;
    private MainActivity mainActivity;

    @Before
    public void setUp() throws Throwable {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        mainActivity = activityRule.getActivity();
        activityRule.runOnUiThread(
                () -> {
                    mainActivity.setContentView(R.layout.activity_main);
                    discardPileView = mainActivity.findViewById(R.id.player_discard_pile_view);
                });
    }

    @Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void testUpdateView() throws Throwable {
        CardView exampleView1 =
                new CardView(
                        context,
                        new Card("Test Card 1", 1, Faction.NONE, List.of(new DamageEffect(1))));
        CardView exampleView2 =
                new CardView(
                        context,
                        new Card("Test Card 2", 2, Faction.NONE, List.of(new DamageEffect(1))));
        CardView exampleView3 =
                new CardView(
                        context,
                        new Card("Test Card 3", 3, Faction.NONE, List.of(new DamageEffect(1))));
        CardView wrongExampleView =
                new CardView(
                        context,
                        new Card("Test Card 4", 4, Faction.NONE, List.of(new DamageEffect(1))));
        List<CardView> cardViews = List.of(exampleView1, exampleView2, exampleView3);
        activityRule.runOnUiThread(() -> discardPileView.updateView(List.of(wrongExampleView)));

        activityRule.runOnUiThread(() -> discardPileView.updateView(cardViews));
        CardView discardPileCardView = discardPileView.findViewById(R.id.discardPileCardView);
        assertEquals(discardPileCardView.getCard(), exampleView3.getCard());

        TextView txtAmount = discardPileView.findViewById(R.id.discardPileAmount);
        assertEquals("3", txtAmount.getText());
    }

    @Test
    public void testUpdateViewEmpty() throws Throwable {
        CardView wrongExampleView =
                new CardView(
                        context,
                        new Card("Test Card 1", 1, Faction.NONE, List.of(new DamageEffect(1))));
        List<CardView> cardViews = new ArrayList<>();
        activityRule.runOnUiThread(() -> discardPileView.updateView(List.of(wrongExampleView)));

        activityRule.runOnUiThread(() -> discardPileView.updateView(cardViews));
        CardView discardPileCardView = discardPileView.findViewById(R.id.discardPileCardView);
        assertEquals(View.GONE, discardPileCardView.getVisibility());

        TextView txtAmount = discardPileView.findViewById(R.id.discardPileAmount);
        assertEquals("0", txtAmount.getText());
    }

    @Test
    public void testClickOnEmptyView() throws Throwable {
        activityRule.runOnUiThread(
                () -> {
                    discardPileView
                            .findViewById(R.id.discardPileCardView)
                            .findViewById(R.id.card_view_background)
                            .performClick();
                    assertEquals(
                            View.GONE,
                            mainActivity
                                    .findViewById(R.id.fullscreen_discard_pile)
                                    .getVisibility());
                });
    }

    @Test
    public void testClickOnNonEmptyView() throws Throwable {
        activityRule.runOnUiThread(
                () -> {
                    discardPileView.updateView(
                            List.of(
                                    new CardView(
                                            context,
                                            new Card(
                                                    "Test Card 1",
                                                    1,
                                                    Faction.NONE,
                                                    List.of(new DamageEffect(1))))));
                    discardPileView
                            .findViewById(R.id.discardPileCardView)
                            .findViewById(R.id.card_view_background)
                            .performClick();
                    LinearLayout fullscreenDiscard =
                            mainActivity.findViewById(R.id.fullscreen_discard_pile);
                    assertEquals(View.VISIBLE, fullscreenDiscard.getVisibility());
                    assertEquals(1, fullscreenDiscard.getChildCount());
                });
    }

    @Test
    public void testClickAndCloseAgain() throws Throwable {
        activityRule.runOnUiThread(
                () -> {
                    discardPileView.updateView(
                            List.of(
                                    new CardView(
                                            context,
                                            new Card(
                                                    "Test Card 1",
                                                    1,
                                                    Faction.NONE,
                                                    List.of(new DamageEffect(1))))));
                    discardPileView
                            .findViewById(R.id.discardPileCardView)
                            .findViewById(R.id.card_view_background)
                            .performClick();
                    discardPileView
                            .findViewById(R.id.discardPileCardView)
                            .findViewById(R.id.card_view_background)
                            .performClick();
                    LinearLayout fullscreenDiscard =
                            mainActivity.findViewById(R.id.fullscreen_discard_pile);
                    assertEquals(View.GONE, fullscreenDiscard.getVisibility());
                });
    }

    @Test
    public void testClickAndCloseByOpponentAgain() throws Throwable {
        activityRule.runOnUiThread(
                () -> {
                    discardPileView.updateView(
                            List.of(
                                    new CardView(
                                            context,
                                            new Card(
                                                    "Test Card 1",
                                                    1,
                                                    Faction.NONE,
                                                    List.of(new DamageEffect(1))))));
                    ((DiscardPileView) (mainActivity.findViewById(R.id.opponent_discard_pile_view)))
                            .updateView(
                                    List.of(
                                            new CardView(
                                                    context,
                                                    new Card(
                                                            "Test Card 1",
                                                            1,
                                                            Faction.NONE,
                                                            List.of(new DamageEffect(1))))));
                    discardPileView
                            .findViewById(R.id.discardPileCardView)
                            .findViewById(R.id.card_view_background)
                            .performClick();
                    mainActivity
                            .findViewById(R.id.opponent_discard_pile_view)
                            .findViewById(R.id.discardPileCardView)
                            .findViewById(R.id.card_view_background)
                            .performClick();
                    LinearLayout fullscreenDiscard =
                            mainActivity.findViewById(R.id.fullscreen_discard_pile);
                    assertEquals(View.VISIBLE, fullscreenDiscard.getVisibility());
                });
    }

    @Test
    public void testUpdateViewWhileFullscreenIsUp() throws Throwable {
        activityRule.runOnUiThread(
                () -> {
                    discardPileView.updateView(
                            List.of(
                                    new CardView(
                                            context,
                                            new Card(
                                                    "Test Card 1",
                                                    1,
                                                    Faction.NONE,
                                                    List.of(new DamageEffect(1))))));
                    discardPileView
                            .findViewById(R.id.discardPileCardView)
                            .findViewById(R.id.card_view_background)
                            .performClick();
                    discardPileView.updateView(
                            List.of(
                                    new CardView(
                                            context,
                                            new Card(
                                                    "Test Card 1",
                                                    1,
                                                    Faction.NONE,
                                                    List.of(new DamageEffect(1)))),
                                    new CardView(
                                            context,
                                            new Card(
                                                    "Test Card 2",
                                                    2,
                                                    Faction.NONE,
                                                    List.of(new DamageEffect(1))))));
                    mainActivity
                            .findViewById(R.id.opponent_discard_pile_view)
                            .findViewById(R.id.discardPileCardView)
                            .findViewById(R.id.card_view_background)
                            .performClick();
                    LinearLayout fullscreenDiscard =
                            mainActivity.findViewById(R.id.fullscreen_discard_pile);
                    assertEquals(View.VISIBLE, fullscreenDiscard.getVisibility());
                    assertEquals(2, fullscreenDiscard.getChildCount());
                });
    }
}
