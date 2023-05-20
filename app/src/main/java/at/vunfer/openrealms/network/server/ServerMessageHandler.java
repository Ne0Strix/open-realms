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
    private static final String TAG = "ServerMessageHandler";
    private ServerThread serverThread;

    private void ensureServerThreadInitialized() {
        if (serverThread == null) {
            serverThread = ServerThread.getInstance();
        }
    }

    public void handleMessage(Message message) {
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
            default:
                Log.i(TAG, "Received message of unknown type.");
        }
    }

    private void handleTouchedCard(Message message, GameSession gameSession, Player currentPlayer) {
        int cardId = (int) message.getData(DataKey.CARD_ID);

        try {
            if (currentPlayer.getPlayArea().playCardById(cardId)) {
                Log.i(TAG, "Card " + cardId + " played successfully.");
                sendCardMovementToAllClients(
                        gameSession, currentPlayer, DeckType.HAND, DeckType.PLAYED, cardId);
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
            } else {
                Log.i(TAG, "Card cannot be played/bought by this player.");
            }
        } catch (IllegalArgumentException e) {
            Log.i(TAG, "Card ID could not be resolved");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendCardMovementToAllClients(
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

    private void sendChampionExpendedToAllClients(
            GameSession gameSession, Player currentPlayer, int cardId) throws IOException {
        serverThread.sendMessageToAllClients(
                createExpendChampionMessage(
                        gameSession.getPlayerTurnNumber(currentPlayer), cardId));
    }

    private void handleEndTurn(GameSession gameSession, Player currentPlayer) {
        Log.i(
                TAG,
                "Size remaining deck: "
                        + currentPlayer.getPlayArea().getPlayerCards().getHandCards().size());
        serverThread.discardCardsAfterTurn(
                gameSession.getPlayerTurnNumber(currentPlayer), currentPlayer);

        printCardsFromPlayer(currentPlayer);
        gameSession.endTurn();
        Deck<Card> restockedFromDiscarded =
                currentPlayer.getPlayArea().getPlayerCards().getRestockedFromDiscarded();

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
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void printCardsFromPlayer(Player player) {
        for (Card card : player.getPlayArea().getPlayerCards().getHandCards()) {
            Log.i(TAG, "Card in player Hand: " + card.getId());
        }
    }
}
