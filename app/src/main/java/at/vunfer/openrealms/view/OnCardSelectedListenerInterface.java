/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.view;

import at.vunfer.openrealms.model.Card;
import at.vunfer.openrealms.model.PlayArea;

public interface OnCardSelectedListenerInterface {
    void onCardSelected(Card card, PlayArea playArea);
}
