/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.view.view_interfaces;

import at.vunfer.openrealms.view.CardView;
import java.util.List;

public interface CardPileView {
    void updateView(List<CardView> cardsToDisplay);
}
