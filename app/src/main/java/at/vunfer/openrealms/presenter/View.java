/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.presenter;

import at.vunfer.openrealms.model.Card;
import java.util.List;

public interface View {
    void displayMarket(List<Card> market);
}
