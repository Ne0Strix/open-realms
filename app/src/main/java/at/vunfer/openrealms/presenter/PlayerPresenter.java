/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.presenter;

import at.vunfer.openrealms.model.Player;

public class PlayerPresenter {

    private View view;
    private Player player;

    public PlayerPresenter(Player player) {
        this.player = player;
    }

    public void attachView(View view) {
        this.view = view;
    }

    public void detachView() {
        this.view = null;
    }

    public void updatePlayerStats() {
        int coin = player.getPlayArea().getTurnCoins();
        int damage = player.getPlayArea().getTurnDamage();
        int healing = player.getPlayArea().getTurnHealing();

        if (view != null) {
            view.displayPlayerStats(coin, damage, healing);
        }
    }

    public interface View {
        void displayPlayerStats(int coin, int damage, int healing);
    }
}
