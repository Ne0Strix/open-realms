/* Licensed under GNU GPL v3.0 (C) 2024 */
package at.vunfer.openrealms.network.online;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import at.vunfer.openrealms.R;
import java.util.List;

public class LobbyAdapter extends RecyclerView.Adapter<LobbyAdapter.LobbyViewHolder> {

    private List<Lobby> lobbies;
    private OnJoinClickListener onJoinClickListener;

    public interface OnJoinClickListener {
        void onJoinClick(Lobby lobby);
    }

    public static class LobbyViewHolder extends RecyclerView.ViewHolder {
        public TextView lobbyName;
        public TextView numberOfPlayers;
        public Button joinButton;

        public LobbyViewHolder(View view) {
            super(view);
            lobbyName = view.findViewById(R.id.txtLobbyName);
            numberOfPlayers = view.findViewById(R.id.txtLobbyPlayerCount);
            joinButton = view.findViewById(R.id.btnJoinLobby);
        }
    }

    public LobbyAdapter(List<Lobby> lobbies, OnJoinClickListener onJoinClickListener) {
        this.lobbies = lobbies;
        this.onJoinClickListener = onJoinClickListener;
    }

    @Override
    public LobbyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view =
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.lobby_list_item, parent, false);
        return new LobbyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LobbyViewHolder holder, int position) {
        Lobby lobby = lobbies.get(position);
        holder.lobbyName.setText(lobby.getName());
        holder.numberOfPlayers.setText(lobby.getNumberOfPlayers() + "/2");
        holder.joinButton.setOnClickListener(v -> onJoinClickListener.onJoinClick(lobby));
        if (lobby.isInUse()) {
            holder.numberOfPlayers.setTextColor(Color.RED);
            holder.joinButton.setText("LEAVE");
        } else {
            holder.joinButton.setText("JOIN");
            holder.numberOfPlayers.setTextColor(Color.BLACK);
        }
    }

    @Override
    public int getItemCount() {
        return lobbies.size();
    }

    public void updateLobbies(List<Lobby> newLobbies) {
        lobbies.clear();
        lobbies.addAll(newLobbies);
        notifyDataSetChanged();
    }
}
