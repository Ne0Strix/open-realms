/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms;

import at.vunfer.openrealms.network.Message;

public interface UIUpdateListener {
    void updateUI(Message message);
}
