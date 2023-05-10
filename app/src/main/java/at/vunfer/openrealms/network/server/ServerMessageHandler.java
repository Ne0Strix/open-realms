/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.network.server;

import android.util.Log;
import at.vunfer.openrealms.model.Card;
import at.vunfer.openrealms.model.Deck;
import at.vunfer.openrealms.model.GameSession;
import at.vunfer.openrealms.model.PlayArea;
import at.vunfer.openrealms.model.Player;
import at.vunfer.openrealms.network.DataKey;
import at.vunfer.openrealms.network.DeckType;
import at.vunfer.openrealms.network.IHandleMessage;
import at.vunfer.openrealms.network.Message;
import java.io.IOException;

public class ServerMessageHandler implements IHandleMessage {
    private static final String TAG = "ServerMessageHandler";
    private ServerThread serverThread;

    public void handleMessage(Message message) {
        if (serverThread == null) {
            serverThread = ServerThread.getInstance();
        }
        GameSession gameSession = serverThread.getGameSession();
        Player currentPlayer = gameSession.getCurrentPlayer();
        PlayArea currentPlayerPlayArea = currentPlayer.getPlayArea();

        switch (message.getType()) {
            case TOUCHED:
                int cardId = (int) message.getData(DataKey.CARD_ID);
                int cardType = currentPlayerPlayArea.playCardById(cardId);
                switch (cardType) {
                    case 0:
                        Log.i(TAG, "Card " + cardId + " played successfully.");
                        try {
                            serverThread.sendMessageToAllClients(
                                    serverThread.createRemoveCardMessage(
                                            gameSession.getPlayerTurnNumber(currentPlayer),
                                            DeckType.HAND,
                                            cardId));
                            serverThread.sendMessageToAllClients(
                                    serverThread.createAddCardMessage(
                                            gameSession.getPlayerTurnNumber(currentPlayer),
                                            DeckType.PLAYED,
                                            cardId));
                            serverThread.sendMessageToAllClients(
                                    serverThread.createPlayerStatsMessage(
                                            gameSession.getPlayerTurnNumber(currentPlayer),
                                            currentPlayer));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        break;
                    case 1:
                        Log.i(TAG, "Card " + cardId + " bought successfully.");
                        try {
                            serverThread.sendMessageToAllClients(
                                    serverThread.createRemoveMarketCardMessage(
                                            DeckType.FOR_PURCHASE, cardId));
                            serverThread.sendMessageToAllClients(
                                    serverThread.createAddCardMessage(
                                            gameSession.getPlayerTurnNumber(currentPlayer),
                                            DeckType.DISCARD,
                                            cardId));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        break;
                    case -1:
                        // TODO instructions for backend
                        Log.i(TAG, "Card can not be played/bought by this player.");
                        break;
                    default:
                        Log.i(TAG, "Card ID could not be resolved");
                }
                break;
            case CHOICE:
                // TODO instructions for backend
                break;
            case END_TURN:
                Log.i(
                        TAG,
                        "Size remaining deck: "
                                + currentPlayer
                                        .getPlayArea()
                                        .getPlayerCards()
                                        .getHandCards()
                                        .size());
                serverThread.discardCardsAfterTurn(
                        gameSession.getPlayerTurnNumber(currentPlayer), currentPlayer);
                Log.i(TAG, "before Sleep before endTurn() called.");
                //                try {
                //                    sleep(100);
                //                } catch (InterruptedException e) {
                //                }
                printCardsFromPlayer(currentPlayer);

                Deck<Card> restockedFromDiscarded = gameSession.endTurn();

                printCardsFromPlayer(currentPlayer);
                if (restockedFromDiscarded
                        != null) { // case that discardPile was emptied to restock deck
                    serverThread.sendRestockDeckFromDiscard(
                            gameSession.getPlayerTurnNumber(currentPlayer),
                            currentPlayer,
                            restockedFromDiscarded);
                    Log.i(TAG, "sendRestockDeckFromDiscard called.");
                }
                //                try {
                //                    sleep(100);
                //                } catch (InterruptedException e) {
                //                }
                Log.i(
                        TAG,
                        "Size deck after restock: "
                                + currentPlayer
                                        .getPlayArea()
                                        .getPlayerCards()
                                        .getHandCards()
                                        .size());
                try {
                    serverThread.dealHandCardsBasedOnTurnNumber(
                            gameSession.getPlayerTurnNumber(currentPlayer), currentPlayer);
                    Log.i(TAG, "dealHandCardsBasedOnTurnNumber called.");
                    serverThread.sendMessageToAllClients(
                            serverThread.createPlayerStatsMessage(
                                    gameSession.getPlayerTurnNumber(currentPlayer), currentPlayer));
                    serverThread.sendMessageToAllClients(
                            serverThread.createPlayerStatsMessage(
                                    gameSession.getPlayerTurnNumber(gameSession.getOpponent(currentPlayer)), gameSession.getOpponent(currentPlayer)));
                    Log.i(TAG, "createPlayerStatsMessage called.");
                    serverThread.sendTurnNotificationToAllClients(
                            gameSession.getPlayerTurnNumber(currentPlayer));
                    Log.i(TAG, "sendTurnNotificationToAllClients called.");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            default:
                Log.i(TAG, "Received message of unknown type.");
        }
    }

    public void printCardsFromPlayer(Player player) {
        for (Card card : player.getPlayArea().getPlayerCards().getHandCards()) {
            Log.i(TAG, "Card in player Hand: " + card.getId());
        }
    }
}
