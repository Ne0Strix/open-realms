/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms;

import static androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import android.app.ActivityManager;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
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
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void testJoinGame() throws Throwable {
        MainActivity main = activityRule.getActivity();
        runOnUiThread(
                () -> {
                    main.toMainMenu(new View(main));
                    main.joinGame(new View(main));
                    assertNotNull(main.findViewById(R.id.toMainMenu));
                    assertNotNull(main.findViewById(R.id.join));
                    assertNotNull(main.findViewById(R.id.get_text));
                });
    }

    @Test
    public void testSaveName() throws Throwable {
        MainActivity main = activityRule.getActivity();
        runOnUiThread(
                () -> {
                    main.toMainMenu(new View(main));
                    ((TextView) main.findViewById(R.id.name_chooser)).setText("NAME");
                    main.joinGame(new View(main));
                    assertEquals("NAME", main.playerName);
                    main.toMainMenu(new View(main));
                    assertEquals(
                            "NAME",
                            ((TextView) main.findViewById(R.id.name_chooser)).getText().toString());
                });
    }

    @Test
    public void testSendName() throws Throwable {
        MainActivity main = activityRule.getActivity();
        runOnUiThread(
                () -> {
                    main.toMainMenu(new View(main));
                    ((TextView) main.findViewById(R.id.name_chooser)).setText("NAME");
                    main.joinGame(new View(main));
                    assertEquals("NAME", main.playerName);
                    assertThrows(NullPointerException.class, () -> main.startGame(new View(main)));
                });
    }

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

        assertNotNull(main.getMarketPresenter());
        assertNotNull(main.getPlayerHandPresenter());
        assertNotNull(main.getOpponentHandPresenter());
        assertNotNull(main.getPlayAreaPresenter());
        assertNotNull(main.getPlayerDiscardPilePresenter());
        assertNotNull(main.getOpponentDiscardPilePresenter());
        assertNotNull(main.getPlayerDeckPresenter());
        assertNotNull(main.getOpponentDeckPresenter());
    }

    @Test
    public void testUpdatePlayerStats() throws Throwable {
        MainActivity main = activityRule.getActivity();
        runOnUiThread(
                () -> {
                    main.toMainMenu(new View(main));
                    main.startGame(new View(main));
                });

        Message playerStatsMessage = new Message(MessageType.UPDATE_PLAYER_STATS);
        PlayerStats stats = new PlayerStats("Name", 10, 1, 2, 3);
        playerStatsMessage.setData(DataKey.PLAYER_STATS, stats);
        playerStatsMessage.setData(DataKey.TARGET_PLAYER, 1);
        main.setOverlayViewPresenter(mock(OverlayPresenter.class));
        main.updateUI(playerStatsMessage);
        getInstrumentation().waitForIdleSync();

        verify(main.getOverlayViewPresenter()).updatePlayerName(stats.getPlayerName());
        verify(main.getOverlayViewPresenter()).updatePlayerHealth(stats.getPlayerHealth());
        verify(main.getOverlayViewPresenter()).updateTurnCoin(stats.getTurnCoin());
        verify(main.getOverlayViewPresenter()).updateTurnDamage(stats.getTurnDamage());
        verify(main.getOverlayViewPresenter()).updateTurnHealing(stats.getTurnHealing());
    }

    @Test
    public void testUpdateOpponentStats() throws Throwable {
        MainActivity main = activityRule.getActivity();
        runOnUiThread(
                () -> {
                    main.toMainMenu(new View(main));
                    main.startGame(new View(main));
                });

        Message playerStatsMessage = new Message(MessageType.UPDATE_PLAYER_STATS);
        PlayerStats stats = new PlayerStats("Name", 10, 1, 2, 3);
        playerStatsMessage.setData(DataKey.PLAYER_STATS, stats);
        playerStatsMessage.setData(DataKey.TARGET_PLAYER, 0);
        main.setOverlayViewPresenter(mock(OverlayPresenter.class));
        main.updateUI(playerStatsMessage);
        getInstrumentation().waitForIdleSync();

        verify(main.getOverlayViewPresenter()).updateOpponentName(stats.getPlayerName());
        verify(main.getOverlayViewPresenter()).updateOpponentHealth(stats.getPlayerHealth());
        verify(main.getOverlayViewPresenter()).updateTurnCoin(stats.getTurnCoin());
        verify(main.getOverlayViewPresenter()).updateTurnDamage(stats.getTurnDamage());
        verify(main.getOverlayViewPresenter()).updateTurnHealing(stats.getTurnHealing());
    }

    @Test
    public void testVictory() throws Throwable {
        MainActivity main = activityRule.getActivity();
        runOnUiThread(
                () -> {
                    main.toMainMenu(new View(main));
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
                    main.toMainMenu(new View(main));
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
    public void testTurnNotification() throws Throwable {
        MainActivity main = activityRule.getActivity();
        runOnUiThread(
                () -> {
                    main.toMainMenu(new View(main));
                    main.startGame(new View(main));
                });

        Message turnNotification = new Message(MessageType.TURN_NOTIFICATION);
        turnNotification.setData(DataKey.TARGET_PLAYER, 0);
        main.updateUI(turnNotification);
        getInstrumentation().waitForIdleSync();
        assertEquals(View.INVISIBLE, main.findViewById(R.id.end_turn_button).getVisibility());

        Message turnNotification2 = new Message(MessageType.TURN_NOTIFICATION);
        turnNotification2.setData(DataKey.TARGET_PLAYER, 1);
        main.updateUI(turnNotification2);
        getInstrumentation().waitForIdleSync();
        assertEquals(View.VISIBLE, main.findViewById(R.id.end_turn_button).getVisibility());
    }

    @Test
    public void testUselessNotification() throws Throwable {
        MainActivity main = activityRule.getActivity();
        runOnUiThread(
                () -> {
                    main.toMainMenu(new View(main));
                    main.startGame(new View(main));
                });

        Message turnNotification = new Message(MessageType.TURN_NOTIFICATION);
        main.updateUI(turnNotification);
        getInstrumentation().waitForIdleSync();
        assertEquals(View.INVISIBLE, main.findViewById(R.id.end_turn_button).getVisibility());

        Card testCard = new Card("Test1", 2, Faction.NONE, List.of(new DamageEffect(2)));
        Deck<Card> cardList = new Deck<>();
        cardList.add(testCard);

        Message sendDeck = new Message(MessageType.FULL_CARD_DECK);
        sendDeck.setData(DataKey.COLLECTION, cardList);
        main.updateUI(sendDeck);
        runOnUiThread(
                () -> {
                    main.endGame(true);
                });
        main.updateUI(turnNotification);
        getInstrumentation().waitForIdleSync();
        assertEquals(View.INVISIBLE, main.findViewById(R.id.end_turn_button).getVisibility());
    }

    @Test
    public void testUpdateUIHand() throws Throwable {
        MainActivity main = activityRule.getActivity();
        runOnUiThread(
                () -> {
                    main.toMainMenu(new View(main));
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
                main.getOpponentHandPresenter()
                        .getListOfDisplayedCards()
                        .get(0)
                        .getCard()
                        .isIdentical(testCard));

        Message removeCardFromHand =
                Communication.createRemoveCardMessage(0, DeckType.HAND, testCard.getId());
        main.updateUI(removeCardFromHand);
        getInstrumentation().waitForIdleSync();
        assertTrue(main.getOpponentHandPresenter().getListOfDisplayedCards().isEmpty());

        Message addCardToOpponentHand =
                Communication.createAddCardMessage(1, DeckType.HAND, testCard.getId());
        main.updateUI(addCardToOpponentHand);
        getInstrumentation().waitForIdleSync();
        assertTrue(
                main.getPlayerHandPresenter()
                        .getListOfDisplayedCards()
                        .get(0)
                        .getCard()
                        .isIdentical(testCard));

        Message removeCardFromOpponentHand =
                Communication.createRemoveCardMessage(1, DeckType.HAND, testCard.getId());
        main.updateUI(removeCardFromOpponentHand);
        getInstrumentation().waitForIdleSync();
        assertTrue(main.getPlayerHandPresenter().getListOfDisplayedCards().isEmpty());
    }

    @Test
    public void testSendCheat() throws Throwable {
        MainActivity main = activityRule.getActivity();
        runOnUiThread(
                () -> {
                    main.toMainMenu(new View(main));
                    main.startGame(new View(main));
                });

        Card testCard = new Card("Test1", 2, Faction.NONE, List.of(new DamageEffect(2)));
        Deck<Card> cardList = new Deck<>();
        cardList.add(testCard);

        Message sendDeck = new Message(MessageType.FULL_CARD_DECK);
        sendDeck.setData(DataKey.COLLECTION, cardList);
        main.updateUI(sendDeck);

        Message cheatActive = new Message(MessageType.CHEAT);
        cheatActive.setData(DataKey.CHEAT_ACTIVATE, Boolean.TRUE);
        main.updateUI(cheatActive);
        getInstrumentation().waitForIdleSync();
        assertEquals(
                main.getResources()
                        .getDrawable(R.drawable.circle_outline_white_48)
                        .getConstantState(),
                ((ImageView) main.findViewById(R.id.turnCoinIcon))
                        .getDrawable()
                        .getConstantState());

        Message cheatDeactivate = new Message(MessageType.CHEAT);
        cheatDeactivate.setData(DataKey.CHEAT_ACTIVATE, Boolean.FALSE);
        main.updateUI(cheatDeactivate);
        getInstrumentation().waitForIdleSync();
        assertEquals(
                main.getResources()
                        .getDrawable(R.drawable.circle_outline_gold_48)
                        .getConstantState(),
                ((ImageView) main.findViewById(R.id.turnCoinIcon))
                        .getDrawable()
                        .getConstantState());
    }

    @Test
    public void testUpdateUIDeck() throws Throwable {
        MainActivity main = activityRule.getActivity();
        runOnUiThread(
                () -> {
                    main.toMainMenu(new View(main));
                    main.startGame(new View(main));
                });

        Card testCard = new Card("Test1", 2, Faction.NONE, List.of(new DamageEffect(2)));
        Deck<Card> cardList = new Deck<>();
        cardList.add(testCard);

        Message sendDeck = new Message(MessageType.FULL_CARD_DECK);
        sendDeck.setData(DataKey.COLLECTION, cardList);
        main.updateUI(sendDeck);

        Message addCardToDeck =
                Communication.createAddCardMessage(0, DeckType.DECK, testCard.getId());
        main.updateUI(addCardToDeck);
        getInstrumentation().waitForIdleSync();
        assertTrue(
                main.getOpponentDeckPresenter()
                        .getListOfDisplayedCards()
                        .get(0)
                        .getCard()
                        .isIdentical(testCard));

        Message removeCardFromDeck =
                Communication.createRemoveCardMessage(0, DeckType.DECK, testCard.getId());
        main.updateUI(removeCardFromDeck);
        getInstrumentation().waitForIdleSync();
        assertTrue(main.getOpponentDeckPresenter().getListOfDisplayedCards().isEmpty());

        Message addCardToOpponentDeck =
                Communication.createAddCardMessage(1, DeckType.DECK, testCard.getId());
        main.updateUI(addCardToOpponentDeck);
        getInstrumentation().waitForIdleSync();
        assertTrue(
                main.getPlayerDeckPresenter()
                        .getListOfDisplayedCards()
                        .get(0)
                        .getCard()
                        .isIdentical(testCard));

        Message removeCardFromOpponentDeck =
                Communication.createRemoveCardMessage(1, DeckType.DECK, testCard.getId());
        main.updateUI(removeCardFromOpponentDeck);
        getInstrumentation().waitForIdleSync();
        assertTrue(main.getPlayerDeckPresenter().getListOfDisplayedCards().isEmpty());
    }

    @Test
    public void testUpdateUiDiscard() throws Throwable {
        MainActivity main = activityRule.getActivity();
        runOnUiThread(
                () -> {
                    main.toMainMenu(new View(main));
                    main.startGame(new View(main));
                });

        Card testCard = new Card("Test1", 2, Faction.NONE, List.of(new DamageEffect(2)));
        Deck<Card> cardList = new Deck<>();
        cardList.add(testCard);

        Message sendDeck = new Message(MessageType.FULL_CARD_DECK);
        sendDeck.setData(DataKey.COLLECTION, cardList);
        main.updateUI(sendDeck);

        Message addCardToDiscard =
                Communication.createAddCardMessage(0, DeckType.DISCARD, testCard.getId());
        main.updateUI(addCardToDiscard);
        getInstrumentation().waitForIdleSync();
        assertTrue(
                main.getOpponentDiscardPilePresenter()
                        .getListOfDisplayedCards()
                        .get(0)
                        .getCard()
                        .isIdentical(testCard));

        Message removeCardFromDiscard =
                Communication.createRemoveCardMessage(0, DeckType.DISCARD, testCard.getId());
        main.updateUI(removeCardFromDiscard);
        getInstrumentation().waitForIdleSync();
        assertTrue(main.getOpponentDiscardPilePresenter().getListOfDisplayedCards().isEmpty());

        Message addCardToOpponentDiscard =
                Communication.createAddCardMessage(1, DeckType.DISCARD, testCard.getId());
        main.updateUI(addCardToOpponentDiscard);
        getInstrumentation().waitForIdleSync();
        assertTrue(
                main.getPlayerDiscardPilePresenter()
                        .getListOfDisplayedCards()
                        .get(0)
                        .getCard()
                        .isIdentical(testCard));

        Message removeCardFromOpponentDiscard =
                Communication.createRemoveCardMessage(1, DeckType.DISCARD, testCard.getId());
        main.updateUI(removeCardFromOpponentDiscard);
        getInstrumentation().waitForIdleSync();
        assertTrue(main.getPlayerDiscardPilePresenter().getListOfDisplayedCards().isEmpty());
    }

    @Test
    public void testUpdateUIPlayarea() throws Throwable {
        MainActivity main = activityRule.getActivity();
        runOnUiThread(
                () -> {
                    main.toMainMenu(new View(main));
                    main.startGame(new View(main));
                });

        Card testCard = new Card("Test1", 2, Faction.NONE, List.of(new DamageEffect(2)));
        Deck<Card> cardList = new Deck<>();
        cardList.add(testCard);

        Message sendDeck = new Message(MessageType.FULL_CARD_DECK);
        sendDeck.setData(DataKey.COLLECTION, cardList);
        main.updateUI(sendDeck);

        Message addCardToPlayArea =
                Communication.createAddCardMessage(0, DeckType.PLAYED, testCard.getId());
        main.updateUI(addCardToPlayArea);
        getInstrumentation().waitForIdleSync();
        assertTrue(
                main.getPlayAreaPresenter()
                        .getListOfDisplayedCards()
                        .get(0)
                        .getCard()
                        .isIdentical(testCard));

        Message removeCardFromPlayArea =
                Communication.createRemoveCardMessage(0, DeckType.PLAYED, testCard.getId());
        main.updateUI(removeCardFromPlayArea);
        getInstrumentation().waitForIdleSync();
        assertTrue(main.getPlayAreaPresenter().getListOfDisplayedCards().isEmpty());
    }

    @Test
    public void testUpdateUIMarket() throws Throwable {
        MainActivity main = activityRule.getActivity();
        runOnUiThread(
                () -> {
                    main.toMainMenu(new View(main));
                    main.startGame(new View(main));
                });

        Card testCard = new Card("Test1", 2, Faction.NONE, List.of(new DamageEffect(2)));
        Deck<Card> cardList = new Deck<>();
        cardList.add(testCard);

        Message sendDeck = new Message(MessageType.FULL_CARD_DECK);
        sendDeck.setData(DataKey.COLLECTION, cardList);
        main.updateUI(sendDeck);

        Message addCardToMarket =
                Communication.createAddCardMessage(0, DeckType.FOR_PURCHASE, testCard.getId());
        main.updateUI(addCardToMarket);
        getInstrumentation().waitForIdleSync();
        assertTrue(
                main.getMarketPresenter()
                        .getListOfDisplayedCards()
                        .get(0)
                        .getCard()
                        .isIdentical(testCard));

        Message removeCardFromMarket =
                Communication.createRemoveCardMessage(0, DeckType.FOR_PURCHASE, testCard.getId());
        main.updateUI(removeCardFromMarket);
        getInstrumentation().waitForIdleSync();
        assertTrue(main.getMarketPresenter().getListOfDisplayedCards().isEmpty());
    }

    @Test
    public void testUpdateUIChampions() throws Throwable {
        MainActivity main = activityRule.getActivity();
        runOnUiThread(
                () -> {
                    main.toMainMenu(new View(main));
                    main.startGame(new View(main));
                });

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
        cardList.add(testChampion);

        Message sendDeck = new Message(MessageType.FULL_CARD_DECK);
        sendDeck.setData(DataKey.COLLECTION, cardList);
        main.updateUI(sendDeck);

        Message addChampionToChampionArea =
                Communication.createAddCardMessage(0, DeckType.CHAMPIONS, testChampion.getId());
        main.updateUI(addChampionToChampionArea);
        getInstrumentation().waitForIdleSync();
        assertTrue(
                main.getOpponentPlayedChampionsPresenter()
                        .getListOfDisplayedCards()
                        .get(0)
                        .getCard()
                        .isIdentical(testChampion));

        Message expendChampion = Communication.createExpendChampionMessage(0, testChampion.getId());
        main.updateUI(expendChampion);
        getInstrumentation().waitForIdleSync();
        assertTrue(
                main.getOpponentPlayedChampionsPresenter()
                        .getListOfDisplayedCards()
                        .get(0)
                        .isExpended());

        Message resetChampion = Communication.createResetChampionMessage(0, testChampion.getId());
        main.updateUI(resetChampion);
        getInstrumentation().waitForIdleSync();
        assertFalse(
                main.getOpponentPlayedChampionsPresenter()
                        .getListOfDisplayedCards()
                        .get(0)
                        .isExpended());

        Message removeChampionFromChampionArea =
                Communication.createRemoveCardMessage(0, DeckType.CHAMPIONS, testChampion.getId());
        main.updateUI(removeChampionFromChampionArea);
        getInstrumentation().waitForIdleSync();
        assertTrue(main.getOpponentPlayedChampionsPresenter().getListOfDisplayedCards().isEmpty());

        addChampionToChampionArea =
                Communication.createAddCardMessage(1, DeckType.CHAMPIONS, testChampion.getId());
        main.updateUI(addChampionToChampionArea);
        getInstrumentation().waitForIdleSync();
        assertTrue(
                main.getPlayerPlayedChampionsPresenter()
                        .getListOfDisplayedCards()
                        .get(0)
                        .getCard()
                        .isIdentical(testChampion));

        expendChampion = Communication.createExpendChampionMessage(1, testChampion.getId());
        main.updateUI(expendChampion);
        getInstrumentation().waitForIdleSync();
        assertTrue(
                main.getPlayerPlayedChampionsPresenter()
                        .getListOfDisplayedCards()
                        .get(0)
                        .isExpended());

        resetChampion = Communication.createResetChampionMessage(1, testChampion.getId());
        main.updateUI(resetChampion);
        getInstrumentation().waitForIdleSync();
        assertFalse(
                main.getPlayerPlayedChampionsPresenter()
                        .getListOfDisplayedCards()
                        .get(0)
                        .isExpended());

        removeChampionFromChampionArea =
                Communication.createRemoveCardMessage(1, DeckType.CHAMPIONS, testChampion.getId());
        main.updateUI(removeChampionFromChampionArea);
        getInstrumentation().waitForIdleSync();
        assertTrue(main.getPlayerPlayedChampionsPresenter().getListOfDisplayedCards().isEmpty());
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
