package at.vunfer.openrealms.view;

import at.vunfer.openrealms.model.Card;
import at.vunfer.openrealms.model.PlayArea;

public interface OnCardSelectedListenerInterface {
    void onCardSelected(Card card, PlayArea playArea);
}
