/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms;

import static androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;
import android.view.View;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import at.vunfer.openrealms.model.Card;
import at.vunfer.openrealms.model.CardType;
import at.vunfer.openrealms.model.Champion;
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
        assertEquals(View.INVISIBLE, main.findViewById(R.id.end_turn_button).getVisibility());
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
        assertEquals(View.INVISIBLE, main.findViewById(R.id.end_turn_button).getVisibility());
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
        Card testChampion =
                new Champion(
                        "Test1",
                        2,
                        CardType.CHAMPION,
                        Faction.NONE,
                        List.of(new DamageEffect(2)),
                        new Deck<>(),
                        true,
                        10);
        Deck<Card> cardList = new Deck<>();
        cardList.add(testCard);
        cardList.add(testChampion);

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

        // test champion for player
        Message turnNotification = new Message(MessageType.TURN_NOTIFICATION);
        turnNotification.setData(DataKey.TARGET_PLAYER, 0);
        main.updateUI(turnNotification);

        // test adding champions to champion area

        Message addChampionToChampionArea =
                Communication.createAddCardMessage(0, DeckType.CHAMPIONS, testChampion.getId());
        main.updateUI(addChampionToChampionArea);
        getInstrumentation().waitForIdleSync();
        // log size of played Champs
        Log.d(
                "Test",
                "Size of played champs: "
                        + main.playerPlayedChampionsPresenter.getListOfDisplayedCards().size());
        Log.d(
                "Test",
                "Size of en played champs: "
                        + main.opponentPlayedChampionsPresenter.getListOfDisplayedCards().size());

        assertTrue(
                main.opponentPlayedChampionsPresenter
                        .getListOfDisplayedCards()
                        .get(0)
                        .getCard()
                        .isIdentical(testChampion));

        // test expending champions
        Message expendChampion = Communication.createExpendChampionMessage(0, testChampion.getId());
        main.updateUI(expendChampion);
        getInstrumentation().waitForIdleSync();
        assertTrue(
                main.opponentPlayedChampionsPresenter
                        .getListOfDisplayedCards()
                        .get(0)
                        .isExpended());

        // test resetting champions
        Message resetChampion = Communication.createResetChampionMessage(0, testChampion.getId());
        main.updateUI(resetChampion);
        getInstrumentation().waitForIdleSync();
        assertFalse(
                main.opponentPlayedChampionsPresenter
                        .getListOfDisplayedCards()
                        .get(0)
                        .isExpended());

        // test removing champions
        Message removeChampionFromChampionArea =
                Communication.createRemoveCardMessage(0, DeckType.CHAMPIONS, testChampion.getId());
        main.updateUI(removeChampionFromChampionArea);
        getInstrumentation().waitForIdleSync();
        assertTrue(main.opponentPlayedChampionsPresenter.getListOfDisplayedCards().isEmpty());

        // test champion for opponent
        turnNotification = new Message(MessageType.TURN_NOTIFICATION);
        turnNotification.setData(DataKey.TARGET_PLAYER, 1);
        main.updateUI(turnNotification);

        // test adding champions to champion area
        addChampionToChampionArea =
                Communication.createAddCardMessage(1, DeckType.CHAMPIONS, testChampion.getId());
        main.updateUI(addChampionToChampionArea);
        getInstrumentation().waitForIdleSync();
        assertTrue(
                main.playerPlayedChampionsPresenter
                        .getListOfDisplayedCards()
                        .get(0)
                        .getCard()
                        .isIdentical(testChampion));

        // test expending champions
        expendChampion = Communication.createExpendChampionMessage(1, testChampion.getId());
        main.updateUI(expendChampion);
        getInstrumentation().waitForIdleSync();
        assertTrue(
                main.playerPlayedChampionsPresenter.getListOfDisplayedCards().get(0).isExpended());

        // test resetting champions
        resetChampion = Communication.createResetChampionMessage(1, testChampion.getId());
        main.updateUI(resetChampion);
        getInstrumentation().waitForIdleSync();
        assertFalse(
                main.playerPlayedChampionsPresenter.getListOfDisplayedCards().get(0).isExpended());

        // test removing champions
        removeChampionFromChampionArea =
                Communication.createRemoveCardMessage(1, DeckType.CHAMPIONS, testChampion.getId());
        main.updateUI(removeChampionFromChampionArea);
        getInstrumentation().waitForIdleSync();
        assertTrue(main.playerPlayedChampionsPresenter.getListOfDisplayedCards().isEmpty());

        // test handling update player stats
        Message updatePlayerStats = new Message(MessageType.UPDATE_PLAYER_STATS);
        updatePlayerStats.setData(DataKey.TARGET_PLAYER, 1);
        PlayerStats playerStats = new PlayerStats("Player", 10, 10, 10, 10);
        updatePlayerStats.setData(DataKey.PLAYER_STATS, playerStats);
        main.updateUI(updatePlayerStats);
        getInstrumentation().waitForIdleSync();
        assertEquals(
                (main.overlayViewPresenter.getOverlayView().getTurnDamage()),
                Integer.toString(playerStats.getTurnDamage()));
        assertEquals(
                (main.overlayViewPresenter.getOverlayView().getTurnHealing()),
                Integer.toString(playerStats.getTurnHealing()));
        assertEquals(
                (main.overlayViewPresenter.getOverlayView().getTurnCoin()),
                Integer.toString(playerStats.getTurnCoin()));
        assertEquals(
                (main.overlayViewPresenter.getOverlayView().getPlayerName()),
                playerStats.getPlayerName());

        // test handling update opponent stats
        Message updateOpponentStats = new Message(MessageType.UPDATE_PLAYER_STATS);
        updateOpponentStats.setData(DataKey.TARGET_PLAYER, 0);
        PlayerStats opponentStats = new PlayerStats("Opponent", 10, 10, 10, 10);
        updateOpponentStats.setData(DataKey.PLAYER_STATS, opponentStats);
        main.updateUI(updateOpponentStats);
        getInstrumentation().waitForIdleSync();
        assertEquals(
                (main.overlayViewPresenter.getOverlayView().getOpponentName()),
                opponentStats.getPlayerName());
        assertEquals(
                (main.overlayViewPresenter.getOverlayView().getTurnDamage()),
                Integer.toString(opponentStats.getTurnDamage()));
        assertEquals(
                (main.overlayViewPresenter.getOverlayView().getTurnHealing()),
                Integer.toString(opponentStats.getTurnHealing()));
        assertEquals(
                (main.overlayViewPresenter.getOverlayView().getTurnCoin()),
                Integer.toString(opponentStats.getTurnCoin()));
        assertEquals(
                (main.overlayViewPresenter.getOverlayView().getOpponentName()),
                opponentStats.getPlayerName());

        // test handling update player stats for vicory
        updatePlayerStats = new Message(MessageType.UPDATE_PLAYER_STATS);
        updatePlayerStats.setData(DataKey.TARGET_PLAYER, 0);
        playerStats = new PlayerStats("Opponent", 0, 10, 10, 10);
        updatePlayerStats.setData(DataKey.PLAYER_STATS, playerStats);
        main.updateUI(updatePlayerStats);
        getInstrumentation().waitForIdleSync();
        assertEquals(main.getVictoryImage().getVisibility(), View.VISIBLE);
        assertEquals(main.getDefeatImage().getVisibility(), View.INVISIBLE);
        assertEquals(main.getEndTurnButton().getVisibility(), View.INVISIBLE);

        // test handling update opponent stats for defeat
        updateOpponentStats = new Message(MessageType.UPDATE_PLAYER_STATS);
        updateOpponentStats.setData(DataKey.TARGET_PLAYER, 1);
        opponentStats = new PlayerStats("Player", 0, 10, 10, 10);
        updateOpponentStats.setData(DataKey.PLAYER_STATS, opponentStats);
        main.updateUI(updateOpponentStats);
        getInstrumentation().waitForIdleSync();
        assertEquals(main.getVictoryImage().getVisibility(), View.INVISIBLE);
        assertEquals(main.getDefeatImage().getVisibility(), View.VISIBLE);
        assertEquals(main.getEndTurnButton().getVisibility(), View.INVISIBLE);
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
