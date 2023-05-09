/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.network.server;

import android.util.Log;
import at.vunfer.openrealms.model.GameSession;
import at.vunfer.openrealms.model.PlayArea;
import at.vunfer.openrealms.model.Player;
import at.vunfer.openrealms.network.DataKey;
import at.vunfer.openrealms.network.DeckType;
import at.vunfer.openrealms.network.IHandleMessage;
import at.vunfer.openrealms.network.Message;
import at.vunfer.openrealms.network.MessageType;
import java.io.IOException;

public class ServerMessageHandler implements IHandleMessage {

    private static final String TAG = "ServerMessageHandler";
    private ServerThread serverThread;

    public void handleMessage(Message message) {
        if (serverThread == null) {
            serverThread = ServerThread.getInstance();
        }
        switch (message.getType()) {
            case TOUCHED:
                int cardId = (int) message.getData(DataKey.CARD_ID);

                GameSession session = serverThread.getGameSession();
                Player player = session.getCurrentPlayer();
                PlayArea area = player.getPlayArea();

                // play card

                // 0-handcard, 1-marketcard
                int typeOfCard = area.playOrBuyCardById(cardId);

                // send an answer back
                // handcard
                if (typeOfCard == 0) {
                    Message msg = new Message(MessageType.REMOVE_CARD);
                    msg.setData(DataKey.CARD_ID, cardId);
                    msg.setData(DataKey.DECK, DeckType.HAND);
                    try {
                        serverThread.sendMsgToAll(msg);
                    } catch (IOException ex) {
                        Log.e("Error", "IO Exception!");
                    }

                    Message msg2 = new Message(MessageType.ADD_CARD);
                    msg2.setData(DataKey.CARD_ID, cardId);
                    msg2.setData(DataKey.DECK, DeckType.PLAYED_CARDS);
                    // msg2.setData(DataKey.TARGET_PLAYER, player.);
                    try {
                        serverThread.sendMsgToAll(msg2);
                    } catch (IOException ex) {
                        Log.e("Error", "IO Exception!");
                    }

                } else if (typeOfCard == 1) {
                    Message msg = new Message(MessageType.REMOVE_CARD);
                    msg.setData(DataKey.CARD_ID, cardId);
                    msg.setData(DataKey.DECK, DeckType.MARKET);
                    try {
                        serverThread.sendMsgToAll(msg);
                    } catch (IOException ex) {
                        Log.e("Error", "IO Exception!");
                    }

                    Message msg2 = new Message(MessageType.ADD_CARD);
                    msg2.setData(DataKey.CARD_ID, cardId);
                    msg2.setData(DataKey.DECK, DeckType.DISCARDED);
                    try {
                        serverThread.sendMsgToAll(msg);
                    } catch (IOException ex) {
                        Log.e("Error", "IO Exception!");
                    }
                }

                break;

            case CHOICE:
                // TODO instructions for backend
                break;
            case END_TURN:
                // handkarten gehen auf discard pile (Ablagestapel), PlayedCards kommen auch
                // auf Ablagestapel
                // neue Karte kommt aufs Markt - restock()
                // 5 neue Karten werden gezogen

                serverThread.getGameSession().endTurn();
                break;
            default:
                Log.i(TAG, "Received message of unknown type.");
        }
    }
}
