/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms;

import static androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import android.app.ActivityManager;
import android.content.Context;
import android.view.View;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import at.vunfer.openrealms.model.Card;
import at.vunfer.openrealms.model.Deck;
import at.vunfer.openrealms.model.Faction;
import at.vunfer.openrealms.model.effects.DamageEffect;
import at.vunfer.openrealms.network.Communication;
import at.vunfer.openrealms.network.DataKey;
import at.vunfer.openrealms.network.DeckType;
import at.vunfer.openrealms.network.Message;
import at.vunfer.openrealms.network.MessageType;
import at.vunfer.openrealms.network.PlayerStats;
import at.vunfer.openrealms.presenter.OverlayPresenter;
import java.io.IOException;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void testStartGame() throws Throwable {
        MainActivity main = activityRule.getActivity();
        runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        main.startGame(new View(main));
                    }
                });

        assertNotNull(main.marketPresenter);
        assertNotNull(main.playerHandPresenter);
        assertNotNull(main.opponentHandPresenter);
        assertNotNull(main.playAreaPresenter);
        assertNotNull(main.playerDiscardPilePresenter);
        assertNotNull(main.opponentDiscardPilePresenter);
        assertNotNull(main.playerDeckPresenter);
        assertNotNull(main.opponentDeckPresenter);
    }

    @Test
    public void testUpdatePlayerStats() throws Throwable {
        MainActivity main = activityRule.getActivity();
        runOnUiThread(
                () -> {
                    try {
                        main.toMainMenu(new View(main));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    main.startGame(new View(main));
                });

        Message playerStatsMessage = new Message(MessageType.UPDATE_PLAYER_STATS);
        PlayerStats stats = new PlayerStats("Name", 10, 1, 2, 3);
        playerStatsMessage.setData(DataKey.PLAYER_STATS, stats);
        playerStatsMessage.setData(DataKey.TARGET_PLAYER, 1);
        main.overlayViewPresenter = mock(OverlayPresenter.class);
        main.updateUI(playerStatsMessage);
        getInstrumentation().waitForIdleSync();

        verify(main.overlayViewPresenter).updatePlayerName(stats.getPlayerName());
        verify(main.overlayViewPresenter).updatePlayerHealth(stats.getPlayerHealth());
        verify(main.overlayViewPresenter).updateTurnCoin(stats.getTurnCoin());
        verify(main.overlayViewPresenter).updateTurnDamage(stats.getTurnDamage());
        verify(main.overlayViewPresenter).updateTurnHealing(stats.getTurnHealing());
    }

    @Test
    public void testUpdateOpponentStats() throws Throwable {
        MainActivity main = activityRule.getActivity();
        runOnUiThread(
                () -> {
                    try {
                        main.toMainMenu(new View(main));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    main.startGame(new View(main));
                });

        Message playerStatsMessage = new Message(MessageType.UPDATE_PLAYER_STATS);
        PlayerStats stats = new PlayerStats("Name", 10, 1, 2, 3);
        playerStatsMessage.setData(DataKey.PLAYER_STATS, stats);
        playerStatsMessage.setData(DataKey.TARGET_PLAYER, 0);
        main.overlayViewPresenter = mock(OverlayPresenter.class);
        main.updateUI(playerStatsMessage);
        getInstrumentation().waitForIdleSync();

        verify(main.overlayViewPresenter).updateOpponentName(stats.getPlayerName());
        verify(main.overlayViewPresenter).updateOpponentHealth(stats.getPlayerHealth());
        verify(main.overlayViewPresenter).updateTurnCoin(stats.getTurnCoin());
        verify(main.overlayViewPresenter).updateTurnDamage(stats.getTurnDamage());
        verify(main.overlayViewPresenter).updateTurnHealing(stats.getTurnHealing());
    }

    @Test
    public void testVictory() throws Throwable {
        MainActivity main = activityRule.getActivity();
        runOnUiThread(
                () -> {
                    try {
                        main.toMainMenu(new View(main));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    main.startGame(new View(main));
                });
        Card testCard = new Card("Test1", 2, Faction.NONE, List.of(new DamageEffect(2)));
        Deck<Card> cardList = new Deck<>();
        cardList.add(testCard);

        Message sendDeck = new Message(MessageType.FULL_CARD_DECK);
        sendDeck.setData(DataKey.COLLECTION, cardList);
        main.updateUI(sendDeck);

        Message playerStatsMessage = new Message(MessageType.UPDATE_PLAYER_STATS);
        PlayerStats stats = new PlayerStats("Name", 0, 1, 2, 3);
        playerStatsMessage.setData(DataKey.PLAYER_STATS, stats);
        playerStatsMessage.setData(DataKey.TARGET_PLAYER, 1);
        main.updateUI(playerStatsMessage);
        getInstrumentation().waitForIdleSync();

        assertEquals(View.VISIBLE, main.findViewById(R.id.defeat_image).getVisibility());
        assertEquals(View.GONE, main.findViewById(R.id.end_turn_button).getVisibility());
    }

    @Test
    public void testDefeat() throws Throwable {
        MainActivity main = activityRule.getActivity();
        runOnUiThread(
                () -> {
                    try {
                        main.toMainMenu(new View(main));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    main.startGame(new View(main));
                });
        Card testCard = new Card("Test1", 2, Faction.NONE, List.of(new DamageEffect(2)));
        Deck<Card> cardList = new Deck<>();
        cardList.add(testCard);

        Message sendDeck = new Message(MessageType.FULL_CARD_DECK);
        sendDeck.setData(DataKey.COLLECTION, cardList);
        main.updateUI(sendDeck);

        Message playerStatsMessage = new Message(MessageType.UPDATE_PLAYER_STATS);
        PlayerStats stats = new PlayerStats("Name", 0, 1, 2, 3);
        playerStatsMessage.setData(DataKey.PLAYER_STATS, stats);
        playerStatsMessage.setData(DataKey.TARGET_PLAYER, 0);
        main.updateUI(playerStatsMessage);
        getInstrumentation().waitForIdleSync();

        assertEquals(View.VISIBLE, main.findViewById(R.id.victory_image).getVisibility());
        assertEquals(View.GONE, main.findViewById(R.id.end_turn_button).getVisibility());
    }

    @Test
    public void testUpdateUI() throws Throwable {
        MainActivity main = activityRule.getActivity();
        runOnUiThread(
                () -> {
                    try {
                        main.toMainMenu(new View(main));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    main.startGame(new View(main));
                });

        Card testCard = new Card("Test1", 2, Faction.NONE, List.of(new DamageEffect(2)));
        Deck<Card> cardList = new Deck<>();
        cardList.add(testCard);

        Message sendDeck = new Message(MessageType.FULL_CARD_DECK);
        sendDeck.setData(DataKey.COLLECTION, cardList);
        main.updateUI(sendDeck);

        Message addCardToHand =
                Communication.createAddCardMessage(0, DeckType.HAND, testCard.getId());
        main.updateUI(addCardToHand);
        getInstrumentation().waitForIdleSync();
        assertTrue(
                main.opponentHandPresenter
                        .getListOfDisplayedCards()
                        .get(0)
                        .getCard()
                        .isIdentical(testCard));

        Message removeCardFromHand =
                Communication.createRemoveCardMessage(0, DeckType.HAND, testCard.getId());
        main.updateUI(removeCardFromHand);
        getInstrumentation().waitForIdleSync();
        assertTrue(main.opponentHandPresenter.getListOfDisplayedCards().isEmpty());

        Message addCardToOpponentHand =
                Communication.createAddCardMessage(1, DeckType.HAND, testCard.getId());
        main.updateUI(addCardToOpponentHand);
        getInstrumentation().waitForIdleSync();
        assertTrue(
                main.playerHandPresenter
                        .getListOfDisplayedCards()
                        .get(0)
                        .getCard()
                        .isIdentical(testCard));

        Message removeCardFromOpponentHand =
                Communication.createRemoveCardMessage(1, DeckType.HAND, testCard.getId());
        main.updateUI(removeCardFromOpponentHand);
        getInstrumentation().waitForIdleSync();
        assertTrue(main.playerHandPresenter.getListOfDisplayedCards().isEmpty());

        Message addCardToDiscardPile =
                Communication.createAddCardMessage(0, DeckType.DISCARD, testCard.getId());
        main.updateUI(addCardToDiscardPile);
        getInstrumentation().waitForIdleSync();
        assertTrue(
                main.opponentDiscardPilePresenter
                        .getListOfDisplayedCards()
                        .get(0)
                        .getCard()
                        .isIdentical(testCard));

        Message removeCardFromDiscardPile =
                Communication.createRemoveCardMessage(0, DeckType.DISCARD, testCard.getId());
        main.updateUI(removeCardFromDiscardPile);
        getInstrumentation().waitForIdleSync();
        assertTrue(main.opponentDiscardPilePresenter.getListOfDisplayedCards().isEmpty());

        Message addCardToOpponentDiscardPile =
                Communication.createAddCardMessage(1, DeckType.DISCARD, testCard.getId());
        main.updateUI(addCardToOpponentDiscardPile);
        getInstrumentation().waitForIdleSync();
        assertTrue(
                main.playerDiscardPilePresenter
                        .getListOfDisplayedCards()
                        .get(0)
                        .getCard()
                        .isIdentical(testCard));

        Message removeCardFromOpponentDiscardPile =
                Communication.createRemoveCardMessage(1, DeckType.DISCARD, testCard.getId());
        main.updateUI(removeCardFromOpponentDiscardPile);
        getInstrumentation().waitForIdleSync();
        assertTrue(main.playerDiscardPilePresenter.getListOfDisplayedCards().isEmpty());

        Message addCardToDeck =
                Communication.createAddCardMessage(0, DeckType.DECK, testCard.getId());
        main.updateUI(addCardToDeck);
        getInstrumentation().waitForIdleSync();
        assertTrue(
                main.opponentDeckPresenter
                        .getListOfDisplayedCards()
                        .get(0)
                        .getCard()
                        .isIdentical(testCard));

        Message removeCardFromDeck =
                Communication.createRemoveCardMessage(0, DeckType.DECK, testCard.getId());
        main.updateUI(removeCardFromDeck);
        getInstrumentation().waitForIdleSync();
        assertTrue(main.opponentDeckPresenter.getListOfDisplayedCards().isEmpty());

        Message addCardToOpponentDeck =
                Communication.createAddCardMessage(1, DeckType.DECK, testCard.getId());
        main.updateUI(addCardToOpponentDeck);
        getInstrumentation().waitForIdleSync();
        assertTrue(
                main.playerDeckPresenter
                        .getListOfDisplayedCards()
                        .get(0)
                        .getCard()
                        .isIdentical(testCard));

        Message removeCardFromOpponentDeck =
                Communication.createRemoveCardMessage(1, DeckType.DECK, testCard.getId());
        main.updateUI(removeCardFromOpponentDeck);
        getInstrumentation().waitForIdleSync();
        assertTrue(main.playerDeckPresenter.getListOfDisplayedCards().isEmpty());

        Message addCardToMarket =
                Communication.createAddCardMessage(0, DeckType.FOR_PURCHASE, testCard.getId());
        main.updateUI(addCardToMarket);
        getInstrumentation().waitForIdleSync();
        assertTrue(
                main.marketPresenter
                        .getListOfDisplayedCards()
                        .get(0)
                        .getCard()
                        .isIdentical(testCard));

        Message removeCardFromMarket =
                Communication.createRemoveCardMessage(0, DeckType.FOR_PURCHASE, testCard.getId());
        main.updateUI(removeCardFromMarket);
        getInstrumentation().waitForIdleSync();
        assertTrue(main.marketPresenter.getListOfDisplayedCards().isEmpty());

        Message addCardToPlayArea =
                Communication.createAddCardMessage(0, DeckType.PLAYED, testCard.getId());
        main.updateUI(addCardToPlayArea);
        getInstrumentation().waitForIdleSync();
        assertTrue(
                main.playAreaPresenter
                        .getListOfDisplayedCards()
                        .get(0)
                        .getCard()
                        .isIdentical(testCard));

        Message removeCardFromPlayArea =
                Communication.createRemoveCardMessage(0, DeckType.PLAYED, testCard.getId());
        main.updateUI(removeCardFromPlayArea);
        getInstrumentation().waitForIdleSync();
        assertTrue(main.playAreaPresenter.getListOfDisplayedCards().isEmpty());
    }

    @Test
    public void testMusicServiceStartsOnResume() throws Throwable {
        MainActivity mainActivity = activityRule.getActivity();
        activityRule.runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        mainActivity.setGameStarted(true);
                        mainActivity.onResume();
                    }
                });

        assertTrue(isServiceRunning(mainActivity.getApplicationContext(), OpenRealmsPlayer.class));

        activityRule.runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        mainActivity.onPause();
                    }
                });

        assertFalse(isServiceRunning(mainActivity.getApplicationContext(), OpenRealmsPlayer.class));
    }

    @Test
    public void testMusicServiceStopsOnPause() throws Throwable {
        MainActivity mainActivity = activityRule.getActivity();
        activityRule.runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        mainActivity.setGameStarted(true);
                        mainActivity.onResume();
                    }
                });

        assertTrue(isServiceRunning(mainActivity.getApplicationContext(), OpenRealmsPlayer.class));

        activityRule.runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        mainActivity.onPause();
                    }
                });

        assertFalse(isServiceRunning(mainActivity.getApplicationContext(), OpenRealmsPlayer.class));
    }

    public static boolean isServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager =
                (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (manager != null) {
            for (ActivityManager.RunningServiceInfo service :
                    manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
        }
        return false;
    }
}
