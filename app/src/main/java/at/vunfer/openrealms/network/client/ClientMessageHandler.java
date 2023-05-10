/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.network.client;

import at.vunfer.openrealms.UIUpdateListener;
import at.vunfer.openrealms.network.IHandleMessage;
import at.vunfer.openrealms.network.Message;

public class ClientMessageHandler implements IHandleMessage {
    private static final String TAG = "ClientMessageHandler";
    private final UIUpdateListener uiUpdater;

    public ClientMessageHandler(UIUpdateListener uiUpdater) {
        this.uiUpdater = uiUpdater;
    }

    public void handleMessage(Message message) {
        uiUpdater.updateUI(message);
    }
}
