/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.network.server;

import static at.vunfer.openrealms.network.Communication.createAddCardMessage;
import static at.vunfer.openrealms.network.Communication.createExpendChampionMessage;
import static at.vunfer.openrealms.network.Communication.createPlayerStatsMessage;
import static at.vunfer.openrealms.network.Communication.createRemoveCardMessage;

import android.util.Log;
import at.vunfer.openrealms.model.*;
import at.vunfer.openrealms.network.DataKey;
import at.vunfer.openrealms.network.DeckType;
import at.vunfer.openrealms.network.IHandleMessage;
import at.vunfer.openrealms.network.Message;
import java.io.IOException;

public class ServerMessageHandler implements IHandleMessage {
    public static final String TAG = "ServerMessageHandler";
    private ServerThread serverThread;

    public void ensureServerThreadInitialized() {
        if (serverThread == null) {
            serverThread = ServerThread.getInstance();
        }
    }

    public void handleMessage(Message message) throws IOException {
        ensureServerThreadInitialized();

        GameSession gameSession = serverThread.getGameSession();
        Player currentPlayer = gameSession.getCurrentPlayer();

        switch (message.getType()) {
            case TOUCHED:
                handleTouchedCard(message, gameSession, currentPlayer);
                break;
            case CHOICE:
                // TODO instructions for backend
                break;
            case END_TURN:
                handleEndTurn(gameSession, currentPlayer);
                break;
            case CHEAT:
                handleCheat(message, currentPlayer);
                break;
            case UNCOVER_CHEAT:
                handleUncoverCheat(message, gameSession, currentPlayer);
                break;
            default:
                Log.i(TAG, "Received message of unknown type.");
        }
    }

    public void handleUncoverCheat(Message message, GameSession gameSession, Player currentPlayer)
            throws IOException {
        boolean isCheatActive = currentPlayer.getPlayArea().getCheat();
        if (isCheatActive) {
            Deck<Card> cards = currentPlayer.getPlayArea().getDrawnByCheat();
            currentPlayer.getPlayArea().destroyDrawnByCheat();
            for (Card card : cards) {
                serverThread.sendMessageToAllClients(
                        createRemoveCardMessage(
                                gameSession.getPlayerTurnNumber(currentPlayer),
                                DeckType.DISCARD,
                                card.getId()));
            }
            currentPlayer.getPlayArea().clearDrawnByCheat();
            currentPlayer.getPlayArea().setHealth(currentPlayer.getPlayArea().getHealth() - 10);
            serverThread.sendMessageToAllClients(
                    createPlayerStatsMessage(
                            gameSession.getPlayerTurnNumber(currentPlayer), currentPlayer));
            currentPlayer.getPlayArea().setCheat(false);
            serverThread.sendCheatStatusToAll(false);
        } else {
            Player opponent = gameSession.getOpponent(currentPlayer);
            opponent.getPlayArea().setHealth(opponent.getPlayArea().getHealth() - 5);
            serverThread.sendMessageToAllClients(
                    createPlayerStatsMessage(gameSession.getPlayerTurnNumber(opponent), opponent));
            serverThread.sendMessageToAllClients(
                    createPlayerStatsMessage(
                            gameSession.getPlayerTurnNumber(currentPlayer), currentPlayer));
        }
    }

    public void handleCheat(Message message, Player currentPlayer) {
        boolean cheatActive = (boolean) message.getData(DataKey.CHEAT_ACTIVATE);
        currentPlayer.getPlayArea().setCheat(cheatActive);
        serverThread.sendCheatStatusToAll(cheatActive);
        Log.i(TAG, "Cheat mode set to " + cheatActive);
    }

    public void handleTouchedCard(Message message, GameSession gameSession, Player currentPlayer) {
        int cardId = (int) message.getData(DataKey.CARD_ID);

        try {
            int cardType = currentPlayer.getPlayArea().playCardById(cardId);
            if (cardType == 1) {
                Log.i(TAG, "Card " + cardId + " played successfully.");
                sendCardMovementToAllClients(
                        gameSession, currentPlayer, DeckType.HAND, DeckType.PLAYED, cardId);
                checkDrawnCard(gameSession, currentPlayer);
            } else if (cardType == 2) {
                Log.i(TAG, "Champion " + cardId + " played successfully.");
                sendCardMovementToAllClients(
                        gameSession, currentPlayer, DeckType.HAND, DeckType.CHAMPIONS, cardId);
                checkDrawnCard(gameSession, currentPlayer);
            } else if (currentPlayer.getPlayArea().buyCardById(cardId)) {
                Log.i(TAG, "Card " + cardId + " bought successfully.");
                sendCardMovementToAllClients(
                        gameSession,
                        currentPlayer,
                        DeckType.FOR_PURCHASE,
                        DeckType.DISCARD,
                        cardId);
            } else if (currentPlayer.getPlayArea().expendChampionById(cardId)) {
                Log.i(TAG, "Champion " + cardId + " expended successfully.");
                sendChampionExpendedToAllClients(gameSession, currentPlayer, cardId);
                checkDrawnCard(gameSession, currentPlayer);
            } else if (currentPlayer
                    .getPlayArea()
                    .attackChampionById(
                            cardId, gameSession.getOpponent(currentPlayer).getPlayArea())) {
                Log.i(TAG, "Champion " + cardId + " attacked successfully.");
                sendChampionKilledToAllClients(gameSession, currentPlayer, cardId);
            } else {
                Log.i(TAG, "Card cannot be played/bought by this player.");
            }
        } catch (IllegalArgumentException e) {
            Log.i(TAG, "Card ID could not be resolved");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void checkDrawnCard(GameSession gameSession, Player currentPlayer) throws IOException {
        Deck<Card> drawnCards = currentPlayer.getPlayArea().getCardDrawnFromSpecialAbility();
        if (!drawnCards.isEmpty()) { // push changes from special ability
            for (Card c : drawnCards) {
                sendCardMovementToAllClients(
                        gameSession, currentPlayer, DeckType.DECK, DeckType.HAND, c.getId());
            }
            gameSession.getCurrentPlayer().getPlayArea().resetCardDrawnFromSpecialAbility();
        }
    }

    public void sendChampionKilledToAllClients(
            GameSession gameSession, Player currentPlayer, int cardId) throws IOException {
        serverThread.sendMessageToAllClients(
                createRemoveCardMessage(
                        gameSession.getPlayerTurnNumber(gameSession.getOpponent(currentPlayer)),
                        DeckType.CHAMPIONS,
                        cardId));
        serverThread.sendMessageToAllClients(
                createAddCardMessage(
                        gameSession.getPlayerTurnNumber(gameSession.getOpponent(currentPlayer)),
                        DeckType.DISCARD,
                        cardId));
        serverThread.sendMessageToAllClients(
                createPlayerStatsMessage(
                        gameSession.getPlayerTurnNumber(currentPlayer), currentPlayer));
    }

    public void sendCardMovementToAllClients(
            GameSession gameSession,
            Player currentPlayer,
            DeckType deckTypeRemove,
            DeckType deckTypeAdd,
            int cardId)
            throws IOException {
        serverThread.sendMessageToAllClients(
                createRemoveCardMessage(
                        gameSession.getPlayerTurnNumber(currentPlayer), deckTypeRemove, cardId));
        serverThread.sendMessageToAllClients(
                createAddCardMessage(
                        gameSession.getPlayerTurnNumber(currentPlayer), deckTypeAdd, cardId));
        serverThread.sendMessageToAllClients(
                createPlayerStatsMessage(
                        gameSession.getPlayerTurnNumber(currentPlayer), currentPlayer));
    }

    public void sendChampionExpendedToAllClients(
            GameSession gameSession, Player currentPlayer, int cardId) throws IOException {
        serverThread.sendMessageToAllClients(
                createExpendChampionMessage(
                        gameSession.getPlayerTurnNumber(currentPlayer), cardId));
        serverThread.sendMessageToAllClients(
                createPlayerStatsMessage(
                        gameSession.getPlayerTurnNumber(currentPlayer), currentPlayer));
    }

    public void handleEndTurn(GameSession gameSession, Player currentPlayer) {
        currentPlayer.getPlayArea().setCheat(false);
        Log.i(
                TAG,
                "Size remaining deck: "
                        + currentPlayer.getPlayArea().getPlayerCards().getHandCards().size());
        serverThread.discardCardsAfterTurn(
                gameSession.getPlayerTurnNumber(currentPlayer), currentPlayer);
        serverThread.resetChampionsAfterTurn(
                gameSession.getPlayerTurnNumber(currentPlayer), currentPlayer);
        Log.d(TAG, "Champions resetted.");

        printCardsFromPlayer(currentPlayer);
        gameSession.endTurn();
        Deck<Card> restockedFromDiscarded =
                currentPlayer.getPlayArea().getPlayerCards().getRestockedFromDiscarded();

        handleKilledChampionsAtTurnEnd(gameSession, currentPlayer);

        serverThread.dealMarketCardsToPurchaseAreaToAll();

        printCardsFromPlayer(currentPlayer);
        if (restockedFromDiscarded != null) {
            serverThread.sendRestockDeckFromDiscard(
                    gameSession.getPlayerTurnNumber(currentPlayer), restockedFromDiscarded);
            Log.i(TAG, "sendRestockDeckFromDiscard called.");
        }
        Log.i(
                TAG,
                "Size deck after restock: "
                        + currentPlayer.getPlayArea().getPlayerCards().getHandCards().size());
        try {
            serverThread.dealHandCardsBasedOnTurnNumber(
                    gameSession.getPlayerTurnNumber(currentPlayer), currentPlayer);
            Log.i(TAG, "dealHandCardsBasedOnTurnNumber called.");
            serverThread.sendMessageToAllClients(
                    createPlayerStatsMessage(
                            gameSession.getPlayerTurnNumber(currentPlayer), currentPlayer));
            serverThread.sendMessageToAllClients(
                    createPlayerStatsMessage(
                            gameSession.getPlayerTurnNumber(gameSession.getOpponent(currentPlayer)),
                            gameSession.getOpponent(currentPlayer)));
            Log.i(TAG, "createPlayerStatsMessage called.");
            serverThread.sendTurnNotificationToAllClients(
                    gameSession.getPlayerTurnNumber(gameSession.getCurrentPlayer()));
            Log.i(TAG, "sendTurnNotificationToAllClients called.");
            serverThread.sendCheatStatusToAll(false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void handleKilledChampionsAtTurnEnd(GameSession gameSession, Player currentPlayer) {
        Player opponent = gameSession.getOpponent(currentPlayer);
        Deck<Card> discardedChampions = opponent.getPlayArea().getAtTurnEndDiscardedChampions();
        for (Card c : discardedChampions) {
            try {
                sendChampionKilledToAllClients(gameSession, currentPlayer, c.getId());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void printCardsFromPlayer(Player player) {
        for (Card card : player.getPlayArea().getPlayerCards().getHandCards()) {
            Log.i(TAG, "Card in player Hand: " + card.getId());
        }
    }
}
