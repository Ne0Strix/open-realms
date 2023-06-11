/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.network;

import java.io.IOException;

public interface IHandleMessage {
    void handleMessage(Message message) throws IOException;
}
