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
import at.vunfer.openrealms.network.server.card_actions.AttackChampion;
import at.vunfer.openrealms.network.server.card_actions.BuyCard;
import at.vunfer.openrealms.network.server.card_actions.CardAction;
import at.vunfer.openrealms.network.server.card_actions.ExpendChampion;
import at.vunfer.openrealms.network.server.card_actions.PlayCard;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

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
            case REMATCH_REQUEST:
                handleRematch();
                break;
            case NAME:
                handleName(message);
                break;
            default:
                Log.e(TAG, "Received message of unknown type.");
        }
    }

    private void handleName(Message message) throws IOException {
        serverThread.sendNameChangeToAll(
                (String) message.getData(DataKey.NAME),
                (int) message.getData(DataKey.TARGET_PLAYER));
    }

    private void handleRematch() throws IOException {
        serverThread.sendRematchToAll();
    }

    private void handleUncoverCheat(Message message, GameSession gameSession, Player currentPlayer)
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

        List<CardAction> cardActions =
                Arrays.asList(
                        new PlayCard(this),
                        new BuyCard(this),
                        new ExpendChampion(this),
                        new AttackChampion(this));

        try {
            for (CardAction action : cardActions) {
                if (action.handleAction(cardId, gameSession, currentPlayer)) {
                    return;
                }
            }
        } catch (IllegalArgumentException | IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public void checkDrawnCard(GameSession gameSession, Player currentPlayer) throws IOException {
        Deck<Card> drawnCards = currentPlayer.getPlayArea().getCardDrawnFromSpecialAbility();
        if (!drawnCards.isEmpty()) { // push changes from special ability
            for (Card c : drawnCards) {
                if (c.getName().equals("PLACEHOLDER")) {
                    sendRestockUpdate(gameSession, currentPlayer);
                    continue;
                }
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

    public void handleEndTurn(GameSession gameSession, Player currentPlayerBeforeNewTurn) {
        resetCheatForNewTurn(currentPlayerBeforeNewTurn);

        resetCardsAtTurnEnd(gameSession, currentPlayerBeforeNewTurn);

        gameSession.endTurn();

        cleanupAfterEndTurn(gameSession, currentPlayerBeforeNewTurn);

        correctCardPiles(gameSession, currentPlayerBeforeNewTurn);

        dealNewHandAndUpdateStats(gameSession, currentPlayerBeforeNewTurn);
    }

    private void resetCheatForNewTurn(Player currentPlayerBeforeNewTurn) {
        currentPlayerBeforeNewTurn.getPlayArea().setCheat(false);
    }

    void resetCardsAtTurnEnd(GameSession gameSession, Player currentPlayerBeforeNewTurn) {
        serverThread.discardCardsAfterTurn(
                gameSession.getPlayerTurnNumber(currentPlayerBeforeNewTurn),
                currentPlayerBeforeNewTurn);
        serverThread.resetChampionsAfterTurn(
                gameSession.getPlayerTurnNumber(currentPlayerBeforeNewTurn),
                currentPlayerBeforeNewTurn);
    }

    void cleanupAfterEndTurn(GameSession gameSession, Player opponentInNewTurn) {
        handleKilledChampionsAtTurnEnd(gameSession, opponentInNewTurn);
        serverThread.dealMarketCardsToPurchaseAreaToAll();
    }

    void correctCardPiles(GameSession gameSession, Player opponentInNewTurn) {
        sendRestockUpdate(gameSession, opponentInNewTurn);
    }

    void dealNewHandAndUpdateStats(GameSession gameSession, Player opponentInNewTurn) {
        try {
            serverThread.dealHandCardsBasedOnTurnNumber(
                    gameSession.getPlayerTurnNumber(opponentInNewTurn), opponentInNewTurn);
            serverThread.sendMessageToAllClients(
                    createPlayerStatsMessage(
                            gameSession.getPlayerTurnNumber(opponentInNewTurn), opponentInNewTurn));
            serverThread.sendMessageToAllClients(
                    createPlayerStatsMessage(
                            gameSession.getPlayerTurnNumber(
                                    gameSession.getOpponent(opponentInNewTurn)),
                            gameSession.getOpponent(opponentInNewTurn)));
            serverThread.sendTurnNotificationToAllClients(
                    gameSession.getPlayerTurnNumber(gameSession.getCurrentPlayer()));
            serverThread.sendCheatStatusToAll(false);
            currentPlayer.getPlayArea().clearDrawnByCheat();
        } catch (IOException e) {
            throw new ServerMessageHandlerException("Error while handling Server Message", e);
        }
    }

    public void handleKilledChampionsAtTurnEnd(GameSession gameSession, Player currentPlayer) {
        Player opponent = gameSession.getOpponent(currentPlayer);
        Deck<Card> discardedChampions = opponent.getPlayArea().getAtTurnEndDiscardedChampions();
        for (Card c : discardedChampions) {
            try {
                sendChampionKilledToAllClients(gameSession, currentPlayer, c.getId());
            } catch (IOException e) {
                throw new ServerMessageHandlerException("Error while handling Server Message", e);
            }
        }
    }

    public void sendRestockUpdate(GameSession gameSession, Player currentPlayer) {
        Deck<Card> restockedFromDiscarded =
                currentPlayer.getPlayArea().getPlayerCards().getRestockedFromDiscarded();
        if (restockedFromDiscarded != null) {
            serverThread.sendRestockDeckFromDiscard(
                    gameSession.getPlayerTurnNumber(currentPlayer), restockedFromDiscarded);
            restockedFromDiscarded.clear();
        }
    }

    private class ServerMessageHandlerException extends RuntimeException {
        public ServerMessageHandlerException(
                String errorWhileHandlingServerMessagesThread, IOException e) {}
    }
}
