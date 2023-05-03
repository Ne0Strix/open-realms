/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.network.server;

import android.util.Log;
import at.vunfer.openrealms.network.DataKey;
import at.vunfer.openrealms.network.IHandleMessage;
import at.vunfer.openrealms.network.Message;

public class MessageHandler implements IHandleMessage {

    private static final String TAG = "ServerMessageHandler";
    private ServerThread serverThread;

    public void handleMessage(Message message) {
        if (serverThread == null) {
            serverThread = ServerThread.getInstance();
        }
        switch (message.getType()) {
            case TOUCHED:
                int cardId = (int) message.getData(DataKey.CARD_ID);
                if (cardId
                        != serverThread
                                .getGameSession()
                                .getCurrentPlayer()
                                .getPlayArea()
                                .playCardById(cardId)) {
                    throw new IllegalStateException();
                }
                break;
            case CHOICE:
                // TODO instructions for backend
                break;
            case END_TURN:
                serverThread.getGameSession().endTurn();
                break;
            default:
                Log.i(TAG, "Received message of unknown type.");
        }
    }
}
